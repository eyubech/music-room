package com.musicroom.app.core;

import androidx.annotation.Nullable;

import com.musicroom.app.network.ApiError;

/** State of a server call as observed by the UI. */
public final class Resource<T> {

    public enum Status { LOADING, SUCCESS, ERROR }

    public final Status status;
    /** While loading or after an error, the data of the previous success (if any). */
    @Nullable
    public final T data;
    @Nullable
    public final ApiError error;

    private boolean handled;

    private Resource(Status status, @Nullable T data, @Nullable ApiError error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Resource<T> loading(@Nullable T previous) {
        return new Resource<>(Status.LOADING, previous, null);
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(ApiError error, @Nullable T previous) {
        return new Resource<>(Status.ERROR, previous, error);
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    /**
     * Returns true only the first time it is called. Use it for one-shot reactions (navigation,
     * snackbars) since LiveData re-delivers the last value when a screen is recreated.
     */
    public boolean markHandled() {
        if (handled) {
            return false;
        }
        handled = true;
        return true;
    }
}
