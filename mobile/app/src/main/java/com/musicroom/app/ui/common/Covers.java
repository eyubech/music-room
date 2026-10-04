package com.musicroom.app.ui.common;

import android.graphics.drawable.GradientDrawable;

/**
 * Gradient "cover art" for events and playlists until they have real artwork. The gradient is
 * picked from the id, so an item keeps the same cover everywhere.
 */
final class Covers {

    /** Light-to-deep pairs: emerald, gold, burgundy, royal blue, amethyst, copper, teal, rose. */
    private static final int[][] GRADIENTS = {
            {0xFF1ED760, 0xFF0B3D20},
            {0xFFE9C97A, 0xFF6B4E16},
            {0xFFB23A48, 0xFF3B0D15},
            {0xFF4A6CF7, 0xFF121B4A},
            {0xFF9B5DE5, 0xFF2A1145},
            {0xFFE07A3F, 0xFF4A1E0A},
            {0xFF2EC4B6, 0xFF0B3B37},
            {0xFFE8A0A0, 0xFF5A2A2A},
    };

    private Covers() {
    }

    static GradientDrawable forId(String id, float cornerRadiusPx) {
        int[] colors = GRADIENTS[Math.floorMod(id.hashCode(), GRADIENTS.length)];
        GradientDrawable cover = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        cover.setCornerRadius(cornerRadiusPx);
        return cover;
    }
}
