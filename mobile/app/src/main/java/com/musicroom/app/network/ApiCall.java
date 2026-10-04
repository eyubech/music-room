package com.musicroom.app.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.core.Resource;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Runs a Retrofit call and publishes its progress into a LiveData. */
public final class ApiCall {

    private ApiCall() {
    }

    public static <T> void enqueue(Call<T> call, MutableLiveData<Resource<T>> target) {
        enqueue(call, target, null);
    }

    /**
     * Must be called from the main thread.
     *
     * @param onSuccess runs before the success is published (e.g. to store the session)
     */
    public static <T> void enqueue(Call<T> call, MutableLiveData<Resource<T>> target,
                                   @Nullable Consumer<T> onSuccess) {
        Resource<T> current = target.getValue();
        T previous = current == null ? null : current.data;
        target.setValue(Resource.loading(previous));

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> c, @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    T body = response.body();
                    if (onSuccess != null) {
                        onSuccess.accept(body);
                    }
                    target.postValue(Resource.success(body));
                } else {
                    target.postValue(Resource.error(ApiError.fromResponse(response), previous));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> c, @NonNull Throwable t) {
                target.postValue(Resource.error(ApiError.network(), previous));
            }
        });
    }
}
