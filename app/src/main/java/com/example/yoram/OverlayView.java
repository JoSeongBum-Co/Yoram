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
    private void updateTextPeriodically() {
        final int[] count = {30};
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {


            @Override
            public void run() {
                // 1초마다 텍스트를 변경
                setOverlayText("남은 시간 : " + count[0]);
                // 다음 업데이트를 예약
                // 여기서 행동을 인식하는 함수를 달음.
                boolean right_pose = true;
                if(right_pose){
                    count[0]--;
                }
                if(count[0] == 0){
                    setOverlayText("다음 동작으로 넘어갑니다.");
                }

                handler.postDelayed(this, 1000);
            }
        }, 1000); // 초기에 1초 후에 시작
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
            overlayDrawable.setBounds(0, 0, getWidth(), getHeight());
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
        }
    }
}
