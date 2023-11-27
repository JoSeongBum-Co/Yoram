package com.example.yoram;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.yoram.ml.ModelUnquant;
import com.google.android.gms.tflite.acceleration.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

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
    private String targetPoseName = "warrior"; // 목표 요가 자세 이름
    String[] yoga_array = {"활", "전사자세", "코브라자세", "고양이자세", "다리당기기"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga);
        viewFinder = findViewById(R.id.viewFinder);
        overlayView = findViewById(R.id.overlayView);


        try {
            tflite = new Interpreter(loadModelFile("model_unquant.tflite"));
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
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), image -> {
            if (System.currentTimeMillis() - lastAnalyzedTimestamp >= 1000) {
                Bitmap imageData = convertImageToByteArray(image);
                String poseName = runInference(imageData);

                if (poseName.equals(targetPoseName)) {
                    overlayView.updateTextPeriodically();
                }
                lastAnalyzedTimestamp = System.currentTimeMillis();
            }
            image.close();
        });

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
    }


    private float[][][][] convertByteArrayToFloatArray(byte[] imageData, int height, int width, int channels) {
        float[][][][] floatArray = new float[1][height][width][channels];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixelIndex = (i * width + j) * channels;
                for (int k = 0; k < channels; k++) {
                    float pixelValue = (imageData[pixelIndex + k] & 0xff) / 255.0f;
                    floatArray[0][i][j][k] = pixelValue;
                }
            }
        }

        return floatArray;
    }


    private String runInference(Bitmap imageData) {
        int imageSize = 224;
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            imageData.getPixels(intValues, 0, imageData.getWidth(), 0, 0, imageData.getWidth(), imageData.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for (int i = 0; i < 224; i++) {
                for (int j = 0; j < 224; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            String result = interpretOutput(confidences);
            return result;
            // find the index of the class with the biggest confidence.
            // Releases model resources if no longer used.
        } catch (IOException e) {
            // TODO Handle the exception
        }
//        float[][][][] inputData = convertByteArrayToFloatArray(imageData, 224, 224, 3);
//        float[][] outputData = new float[1][6];// 모델의 출력 형식에 맞게 수정 필요
//        tflite.run(inputData, outputData);
//        return interpretOutput(outputData); // 모델의 출력을 해석하는 함수 필요
        return "stand";
    }

    private Bitmap convertImageToByteArray(ImageProxy image) {
        // YUV 데이터를 가져오기 위한 준비
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        // YUV -> NV21 포맷으로 변환
        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        // NV21 -> Bitmap 변환
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, out);
        byte[] imageBytes = out.toByteArray();

        // Bitmap 객체 생성
        Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        // Bitmap 크기 조정 (224x224)
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 224, 224, true);

        // Bitmap -> RGB 바이트 배열 변환
//        int bytes = resizedBitmap.getByteCount();
//        ByteBuffer buffer = ByteBuffer.allocate(bytes);
//        resizedBitmap.copyPixelsToBuffer(buffer);
//        byte[] tempArray = buffer.array();

        return resizedBitmap;
    }


    private String interpretOutput(float[] outputData) {
        // TODO: 모델의 출력 데이터를 해석하여 자세 이름을 반환하는 로직 구현
        String result = "stand";
        int max_index = 0;
        float max_val = 0;
        Log.i("ARR", Arrays.toString(outputData));

        for (int i = 0; i < 6; i++) {
            if (max_val < outputData[i]) {
                max_val = outputData[i];
                max_index = i;
            }
        }
        //번호마다 요가 이름 부여
        if (max_index == 0) {
        } else if (max_index == 1) {
            result = "bow";
        } else if (max_index == 2) {
            result = "warrior";
        } else if (max_index == 3) {
            result = "cobra";
        } else if (max_index == 4) {
            result = "cat_pose";
        } else if (max_index == 5) {
            result = "for_back";
        }
//        Log.i("arr", Arrays.toString(confidences));

        // 정확한 동작을 할수있게 확률이 85퍼센트 이상이면 실행할수있게 만든다.
        if (max_val < 0.8) {
            result = "stand";
        }
        Log.i("yoga", result);

        return result;
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
