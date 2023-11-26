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
    public static int count = 30;
    String pose_name = "left";

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
        setOverlayImage(R.drawable.overlay_image);
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
        if (count > 0) {
            setOverlayText("남은 시간 : " + --count);
        } else {
            setOverlayText("다음 동작으로 넘어갑니다.");
            count = 30; // 카운트 재설정
            setOverlayImage(R.drawable.cat_stretch_pose); // 예시 이미지 변경
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
            overlayDrawable.setBounds(0, 0, (int)(getWidth() * 0.9), (int)(getHeight()*0.9));
            overlayDrawable.draw(canvas);
        }

        // 텍스트를 그립니다.
        if (overlayText != null) {
            if (textPaint == null) {
                textPaint = new Paint();
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(50); // 텍스트 크기 설정
            }

            canvas.drawText(overlayText, 60, 50, textPaint); // 텍스트 위치 설정
            canvas.drawText("카메라에 보이시는 동작을 따라하세요.", 30, 110, textPaint);
            canvas.drawText("정확한 동작을 하셔야 카운트가 줄어듭니다.", 30, 170, textPaint);
            canvas.drawText("동작 이름 : " + "고개 돌리기", 30, 230, textPaint);
        }
    }
}