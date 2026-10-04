package com.musicroom.app.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.musicroom.app.R;

/** A thin ring with a gold-to-green light running around it (drawn around the avatar). */
public class GlowRingView extends View {

    private static final long TURN_MS = 5_000;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix matrix = new Matrix();
    private final int gold;
    private final int green;
    @Nullable
    private SweepGradient gradient;
    @Nullable
    private ValueAnimator animator;
    private float angle;

    public GlowRingView(@NonNull Context context) {
        this(context, null);
    }

    public GlowRingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gold = ContextCompat.getColor(context, R.color.gold);
        green = ContextCompat.getColor(context, R.color.green);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getResources().getDisplayMetrics().density * 2.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int clear = gold & 0x00FFFFFF;
        gradient = new SweepGradient(w / 2f, h / 2f,
                new int[]{gold, green, clear, clear, gold}, new float[]{0f, 0.25f, 0.5f, 0.85f, 1f});
        paint.setShader(gradient);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (gradient == null) {
            return;
        }
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        matrix.setRotate(angle, cx, cy);
        gradient.setLocalMatrix(matrix);
        float radius = Math.min(cx, cy) - paint.getStrokeWidth();
        canvas.drawCircle(cx, cy, radius, paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Motion.enabled()) {
            animator = ValueAnimator.ofFloat(0f, 360f);
            animator.setDuration(TURN_MS);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(a -> {
                angle = (float) a.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        super.onDetachedFromWindow();
    }
}
