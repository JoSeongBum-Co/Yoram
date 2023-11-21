package com.example.yoram;

import android.content.pm.PackageManager;
import android.os.Bundle;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;

public class YogaActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private PreviewView viewFinder;
    private ProcessCameraProvider cameraProvider;
    private OverlayView overlayView;
    private Interpreter tflite;
    private int count = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga);
        viewFinder = findViewById(R.id.viewFinder);
        overlayView = findViewById(R.id.overlayView);
        overlayView.setOverlayText("Count : " + count);

        // TensorFlow Lite 모델 초기화
        try {
            tflite = new Interpreter(loadModelFile("your_model.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
            // 모델 파일 로딩 실패 처리
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private MappedByteBuffer loadModelFile(String modelFileName) throws IOException {
        try (FileInputStream is = new FileInputStream(getAssets().openFd(modelFileName).getFileDescriptor())) {
            FileChannel channel = is.getChannel();
            long startOffset = getAssets().openFd(modelFileName).getStartOffset();
            long declaredLength = getAssets().openFd(modelFileName).getDeclaredLength();
            return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), image -> {
            // TODO: 이미지 프로세싱 및 모델 추론 코드 작성
            // byte[] imageData = convertImageToByteArray(image); // 예시 함수, 이미지를 바이트 배열로 변환해야 함

            // TensorFlow Lite 모델 추론
            // float[][] outputData = runInference(imageData);

            // UI 업데이트
            // runOnUiThread(() -> {
            // if (outputData[0][0] > SOME_CONFIDENCE_THRESHOLD) {
            //     count--;
            //     overlayView.setOverlayText("Count : " + count);
            // }
            // });

            image.close(); // 이미지 프로세싱이 끝나면 닫아주어야 함
        });

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
    }

    private float[][] runInference(byte[] imageData) {
        float[][] outputData = new float[1][1]; // 모델의 출력 크기에 따라 변경될 수 있음
        tflite.run(imageData, outputData);
        return outputData;
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
                // 권한이 거부됨
            }
        }
    }
}
