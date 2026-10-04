package com.musicroom.app.network;

import androidx.annotation.NonNull;

import com.musicroom.app.core.ClientInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** Adds the platform, device and app version to every request (subject V.6). */
public final class ClientInfoInterceptor implements Interceptor {

    private final ClientInfo info;

    public ClientInfoInterceptor(ClientInfo info) {
        this.info = info;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .header("X-Platform", info.platform)
                .header("X-Device", info.device)
                .header("X-App-Version", info.appVersion)
                .header("X-Device-Id", info.deviceId)
                .build();
        return chain.proceed(request);
    }
}
