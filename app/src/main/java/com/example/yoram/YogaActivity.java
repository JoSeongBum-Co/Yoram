package com.example.yoram;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;

public class YogaActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private PreviewView viewFinder;
    private ProcessCameraProvider cameraProvider;
    private OverlayView overlayView;
    private Interpreter tflite;
    private long lastAnalyzedTimestamp = 0;
    private int count = 20; // 초기 카운트 값
    private String targetPoseName = "left"; // 목표 요가 자세 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga);
        viewFinder = findViewById(R.id.viewFinder);
        overlayView = findViewById(R.id.overlayView);
        overlayView.setOverlayText("Count : " + count);

        try {
            tflite = new Interpreter(loadModelFile("dongjumodel.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private MappedByteBuffer loadModelFile(String modelFileName) throws IOException {
        try (FileInputStream fis = new FileInputStream(getAssets().openFd(modelFileName).getFileDescriptor())) {
            FileChannel fileChannel = fis.getChannel();
            long startOffset = getAssets().openFd(modelFileName).getStartOffset();
            long declaredLength = getAssets().openFd(modelFileName).getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                cameraProvider = ProcessCameraProvider.getInstance(this).get();
                bindPreview();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview() {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), image -> {
            long currentTime = System.currentTimeMillis();
            // 마지막 분석 시간으로부터 1초가 지났는지 확인
            if (currentTime - lastAnalyzedTimestamp >= 1000) { // 1000ms = 1초
                byte[] imageData = convertImageToByteArray(image);
                String poseName = runInference(imageData);

                if (poseName.equals(targetPoseName)) {
                    runOnUiThread(() -> {
                        count--;
                    });
                }

                lastAnalyzedTimestamp = currentTime; // 현재 시간을 마지막 분석 시간으로 업데이트
            }

            image.close();
        });

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
    }

    private float[][] convertByteArrayToFloatArray(byte[] imageData) {
        // 이미지 크기 및 채널 수에 따라 배열 크기를 조정합니다.
        // 예: 224x224 크기의 컬러 이미지를 입력으로 사용하는 경우
        int IMAGE_SIZE = 224;
        int NUM_CHANNELS = 3;
        float[][] floatArray = new float[1][IMAGE_SIZE * IMAGE_SIZE * NUM_CHANNELS];

        for (int i = 0; i < imageData.length; i++) {
            // 바이트 값을 0에서 255 범위에서 0.0에서 1.0 범위로 변환합니다.
            int pixel = imageData[i] & 0xff;
            floatArray[0][i * NUM_CHANNELS + 0] = pixel / 255.0f; // R 채널
            floatArray[0][i * NUM_CHANNELS + 1] = pixel / 255.0f; // G 채널
            floatArray[0][i * NUM_CHANNELS + 2] = pixel / 255.0f; // B 채널
        }

        return floatArray;
    }

    private String runInference(byte[] imageData) {
        float[][] inputData = convertByteArrayToFloatArray(imageData);
        float[][] outputData = new float[10][2]; // 모델의 출력 형식에 맞게 수정 필요
        tflite.run(inputData, outputData);
        return interpretOutput(outputData); // 모델의 출력을 해석하는 함수 필요
    }

    private byte[] convertImageToByteArray(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        // Y, U, and V 버퍼를 하나의 배열(nv21)로 병합
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        // YUV -> Bitmap 변환
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, out);
        byte[] imageBytes = out.toByteArray();

        // Bitmap -> RGB 바이트 배열
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private String interpretOutput(float[][] outputData) {
        // TODO: 모델의 출력 데이터를 해석하여 자세 이름을 반환하는 로직 구현
        return outputData[0][0] > outputData[0][1] ? "left" : "right";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                // 권한 거부 처리
            }
        }
    }
}
