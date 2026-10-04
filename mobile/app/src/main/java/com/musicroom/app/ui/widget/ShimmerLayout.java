package com.musicroom.app.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Skeleton container: its children are placeholder shapes ("bones") and a soft highlight sweeps
 * across all of them at once, so the whole page shimmers as one surface.
 */
public class ShimmerLayout extends FrameLayout {

    private static final long SWEEP_MS = 1400;
    private static final int HIGHLIGHT = 0x22FFFFFF;
    private static final float TILT_DEGREES = 15f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix matrix = new Matrix();
    @Nullable
    private LinearGradient gradient;
    @Nullable
    private ValueAnimator animator;
    private float bandWidth;
    private float progress;

    public ShimmerLayout(@NonNull Context context) {
        this(context, null);
    }

    public ShimmerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        // Paint the highlight only where a bone already is.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bandWidth = Math.max(w * 0.45f, 1f);
        gradient = new LinearGradient(0, 0, bandWidth, 0,
                new int[]{0x00FFFFFF, HIGHLIGHT, 0x00FFFFFF}, new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP);
        paint.setShader(gradient);
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if (gradient == null || animator == null) {
            super.dispatchDraw(canvas);
            return;
        }
        int save = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
        super.dispatchDraw(canvas);
        float x = -bandWidth * 1.5f + progress * (getWidth() + bandWidth * 3f);
        matrix.setRotate(TILT_DEGREES);
        matrix.postTranslate(x, 0);
        gradient.setLocalMatrix(matrix);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        canvas.restoreToCount(save);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        updateAnimation();
    }

    private void updateAnimation() {
        if (isAttachedToWindow() && isShown() && Motion.enabled()) {
            start();
        } else {
            stop();
        }
    }

    private void start() {
        if (animator != null) {
            return;
        }
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(SWEEP_MS);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            progress = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    private void stop() {
        if (animator != null) {
            animator.cancel();
            animator = null;
            invalidate();
        }
    }
}
