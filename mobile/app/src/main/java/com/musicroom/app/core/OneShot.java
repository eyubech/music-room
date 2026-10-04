package com.musicroom.app.core;

import androidx.annotation.Nullable;

/** A LiveData value that must be acted on once, even if it is re-delivered. */
public final class OneShot<T> {

    private final T value;
    private boolean consumed;

    public OneShot(T value) {
        this.value = value;
    }

    /** Returns the value the first time, then null. */
    @Nullable
    public T consume() {
        if (consumed) {
            return null;
        }
        consumed = true;
        return value;
    }
}
