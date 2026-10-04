package com.musicroom.app.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.musicroom.app.R;

/** The brand mark: five rounded bars (green to gold) moving like a live audio equalizer. */
public class EqualizerView extends View {

    private static final long CYCLE_MS = 6_000;
    /** Resting heights (fraction of the view), also used when animations are off. */
    private static final float[] REST = {0.40f, 0.72f, 1.00f, 0.62f, 0.45f};
    private static final float[] SPEED = {5f, 7f, 4f, 6f, 8f};
    private static final float[] PHASE = {0f, 1.7f, 3.1f, 4.4f, 0.9f};

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int top;
    private final int bottom;
    @Nullable
    private ValueAnimator animator;
    private float time;

    public EqualizerView(@NonNull Context context) {
        this(context, null);
    }

    public EqualizerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        top = ContextCompat.getColor(context, R.color.gold);
        bottom = ContextCompat.getColor(context, R.color.green);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paint.setShader(new LinearGradient(0, h, 0, 0, bottom, top, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int bars = REST.length;
        float w = getWidth() - getPaddingLeft() - getPaddingRight();
        float h = getHeight() - getPaddingTop() - getPaddingBottom();
        float slot = w / bars;
        float stroke = slot * 0.55f;
        paint.setStrokeWidth(stroke);
        float centerY = getPaddingTop() + h / 2f;
        float maxHalf = (h - stroke) / 2f;
        boolean moving = animator != null;
        for (int i = 0; i < bars; i++) {
            float level = moving
                    ? 0.25f + 0.75f * (0.5f + 0.5f * (float) Math.sin(time * Math.PI * 2 * SPEED[i] + PHASE[i]))
                    : REST[i];
            float half = Math.max(maxHalf * level, 0.5f);
            float x = getPaddingLeft() + slot * (i + 0.5f);
            canvas.drawLine(x, centerY - half, x, centerY + half, paint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Motion.enabled()) {
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(CYCLE_MS);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(a -> {
                time = (float) a.getAnimatedValue();
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
