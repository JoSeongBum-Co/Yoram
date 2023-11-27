package com.example.yoram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class OverlayView extends View {

    private Drawable overlayDrawable;
    private String overlayText;
    private Paint textPaint;
    private Handler handler;
    public static int count = 15;
    public String[] yoga_array = {"전사자세", "다리당기기", "코브라자세"};
    public int[] yoga_id_array = {R.drawable.warrior, R.drawable.for_back_pose, R.drawable.cobra_pose};
    public int yoga_count = 0;

    public OverlayView(Context context) {
        super(context);
        init();
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 기본 이미지 리소스 ID와 텍스트를 초기화
        updateTextPeriodically();
        invalidate();
    }

    // 이미지 리소스 ID를 설정하는 메서드
    public void setOverlayImage(int resourceId) {
        try {
            overlayDrawable = ContextCompat.getDrawable(getContext(), resourceId);
            invalidate(); // 뷰를 다시 그리도록 요청
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 텍스트를 주기적으로 업데이트하는 메서드
    // 클래스 멤버 변수로 Handler 선언

    public void updateTextPeriodically() {
        if (yoga_count == yoga_id_array.length) {
            yoga_count = 0;
        }

        setOverlayImage(yoga_id_array[yoga_count]);
        if (count > 0) {
            setOverlayText("남은 시간 : " + count--);
        } else {
            setOverlayText("다음 동작으로 넘어갑니다.");
            count = 15; // 카운트 재설정
            yoga_count++;
        }
    }


    // 텍스트를 설정하는 메서드
    public void setOverlayText(String text) {
        overlayText = text;
        invalidate(); // 뷰를 다시 그리도록 요청
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 카메라 영상 위에 이미지를 그립니다.
        if (overlayDrawable != null) {
            int imageWidth = (int) (getWidth() * 0.8);
            int imageHeight = (int) (getHeight() * 0.8);

            // 이미지를 가운데에 위치시키기 위한 x, y 시작 좌표를 계산
            int x = (getWidth() - imageWidth) / 2;
            int y = (getHeight() - imageHeight) / 2;

            overlayDrawable.setBounds(x, y, x + imageWidth, y + imageHeight);
            overlayDrawable.draw(canvas);
        }


        // 텍스트를 그립니다.
        if (overlayText != null) {
            if (textPaint == null) {
                textPaint = new Paint();
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(50); // 텍스트 크기 설정
            }
            if (yoga_count < yoga_array.length) {
                canvas.drawText(overlayText, 60, 50, textPaint); // 텍스트 위치 설정
                canvas.drawText("카메라에 보이시는 동작을 따라하세요.", 30, 110, textPaint);
                canvas.drawText("정확한 동작을 하셔야 카운트가 줄어듭니다.", 30, 170, textPaint);
                canvas.drawText("동작 이름 : " + yoga_array[yoga_count], 30, 230, textPaint);
            }
        }
    }
}