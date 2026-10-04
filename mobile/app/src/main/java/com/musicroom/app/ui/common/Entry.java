package com.musicroom.app.ui.common;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import java.util.Objects;

/** One row of a list screen: a section header, a note, or a card. */
public final class Entry {

    public enum Type { INTRO, HEADER, NOTE, ITEM }

    public final Type type;
    public final String id;
    public final String title;
    @Nullable
    public final String subtitle;
    @Nullable
    public final String badge;
    @DrawableRes
    public final int icon;
    /** Shows the icon on a gradient cover (events, playlists) instead of a neutral tile. */
    public final boolean cover;

    private Entry(Type type, String id, String title, @Nullable String subtitle,
                  @Nullable String badge, @DrawableRes int icon, boolean cover) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.badge = badge;
        this.icon = icon;
        this.cover = cover;
    }

    /** Top of a list: a gold overline (e.g. a count) and one line about the page. */
    public static Entry intro(String overline, String text) {
        return new Entry(Type.INTRO, "intro", overline, text, null, 0, false);
    }

    public static Entry header(String text) {
        return new Entry(Type.HEADER, "header:" + text, text, null, null, 0, false);
    }

    public static Entry note(String text) {
        return new Entry(Type.NOTE, "note:" + text, text, null, null, 0, false);
    }

    public static Entry item(String id, String title, @Nullable String subtitle,
                             @Nullable String badge, @DrawableRes int icon) {
        return new Entry(Type.ITEM, id, title, subtitle, badge, icon, false);
    }

    public static Entry cover(String id, String title, @Nullable String subtitle,
                              @Nullable String badge, @DrawableRes int icon) {
        return new Entry(Type.ITEM, id, title, subtitle, badge, icon, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Entry)) {
            return false;
        }
        Entry other = (Entry) o;
        return type == other.type && icon == other.icon && cover == other.cover
                && id.equals(other.id) && title.equals(other.title)
                && Objects.equals(subtitle, other.subtitle) && Objects.equals(badge, other.badge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, title, subtitle, badge, icon, cover);
    }
}
