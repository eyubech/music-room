package com.musicroom.app.ui.widget;

import android.animation.ValueAnimator;
import android.view.View;

/** Shared animation rules for the custom views. */
public final class Motion {

    private Motion() {
    }

    /** False when the user turned animations off (accessibility): views then stay still. */
    public static boolean enabled() {
        return ValueAnimator.areAnimatorsEnabled();
    }

    /** Fades {@code in} in and {@code out} out, e.g. a skeleton giving way to content. */
    public static void crossfade(View out, View in) {
        if (in.getVisibility() == View.VISIBLE && out.getVisibility() != View.VISIBLE) {
            return;
        }
        in.animate().cancel();
        out.animate().cancel();
        in.setAlpha(0f);
        in.setVisibility(View.VISIBLE);
        in.animate().alpha(1f).setDuration(260).start();
        out.animate().alpha(0f).setDuration(180)
                .withEndAction(() -> {
                    out.setVisibility(View.GONE);
                    out.setAlpha(1f);
                })
                .start();
    }
}
