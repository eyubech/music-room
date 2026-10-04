package com.musicroom.app.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.musicroom.app.R;

/**
 * Slowly drifting glow (emerald, teal, gold) behind hero areas. It fades into the page
 * background at the top and bottom so it never shows a hard edge.
 */
public class AuroraView extends View {

    private static final long CYCLE_MS = 24_000;

    /**
     * Per blob: base x, base y, drift x, drift y (fractions of the size), radius (fraction of the
     * larger side), x / y speed (whole turns per cycle, so the loop is seamless), phase.
     */
    private static final float[][] BLOBS = {
            {0.28f, 0.38f, 0.18f, 0.10f, 0.75f, 1f, 1f, 0.0f},
            {0.80f, 0.32f, 0.15f, 0.12f, 0.60f, 1f, 2f, 2.1f},
            {0.55f, 0.68f, 0.20f, 0.08f, 0.55f, 2f, 1f, 4.2f},
    };

    private final int[] colors;
    private final int background;
    private final Paint blobPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fadePaint = new Paint();
    private final Matrix matrix = new Matrix();
    private final RadialGradient[] shaders = new RadialGradient[BLOBS.length];
    private final float[] radii = new float[BLOBS.length];
    @Nullable
    private ValueAnimator animator;
    private float time;

    public AuroraView(@NonNull Context context) {
        this(context, null);
    }

    public AuroraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        colors = new int[]{
                ContextCompat.getColor(context, R.color.aurora_emerald),
                ContextCompat.getColor(context, R.color.aurora_teal),
                ContextCompat.getColor(context, R.color.aurora_gold),
        };
        background = ContextCompat.getColor(context, R.color.surface);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float size = Math.max(w, h);
        for (int i = 0; i < BLOBS.length; i++) {
            radii[i] = Math.max(size * BLOBS[i][4], 1f);
            shaders[i] = new RadialGradient(0, 0, radii[i], colors[i],
                    colors[i] & 0x00FFFFFF, Shader.TileMode.CLAMP);
        }
        int clear = background & 0x00FFFFFF;
        fadePaint.setShader(new LinearGradient(0, 0, 0, h,
                new int[]{background, clear, clear, background},
                new float[]{0f, 0.18f, 0.55f, 1f}, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        canvas.drawColor(background);
        double t = time * Math.PI * 2;
        for (int i = 0; i < BLOBS.length; i++) {
            float[] b = BLOBS[i];
            float cx = w * (b[0] + b[2] * (float) Math.sin(t * b[5] + b[7]));
            float cy = h * (b[1] + b[3] * (float) Math.cos(t * b[6] + b[7]));
            matrix.setTranslate(cx, cy);
            shaders[i].setLocalMatrix(matrix);
            blobPaint.setShader(shaders[i]);
            canvas.drawCircle(cx, cy, radii[i], blobPaint);
        }
        canvas.drawRect(0, 0, w, h, fadePaint);
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
