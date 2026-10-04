package com.musicroom.app.network;

import androidx.annotation.NonNull;

import com.musicroom.app.core.SessionStore;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** Sends the access token, when there is one. */
public final class AuthInterceptor implements Interceptor {

    private final SessionStore session;

    public AuthInterceptor(SessionStore session) {
        this.session = session;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String token = session.getAccessToken();
        if (token == null || request.header("Authorization") != null) {
            return chain.proceed(request);
        }
        return chain.proceed(request.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build());
    }
}
