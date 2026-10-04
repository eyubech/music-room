package com.musicroom.app.network;

import com.google.gson.Gson;
import com.musicroom.app.BuildConfig;
import com.musicroom.app.core.ClientInfo;
import com.musicroom.app.core.SessionStore;
import com.musicroom.app.network.api.AuthApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Retrofit setup for one backend address. */
public final class ApiClient {

    private final String baseUrl;
    private final Retrofit retrofit;
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    public ApiClient(String baseUrl, ClientInfo clientInfo, SessionStore session,
                     Runnable onSessionExpired) {
        this.baseUrl = baseUrl;
        GsonConverterFactory json = GsonConverterFactory.create(new Gson());

        OkHttpClient base = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new ClientInfoInterceptor(clientInfo))
                .build();

        AuthApi refreshApi = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(base)
                .addConverterFactory(json)
                .build()
                .create(AuthApi.class);

        OkHttpClient.Builder client = base.newBuilder()
                .addInterceptor(new AuthInterceptor(session))
                .authenticator(new TokenAuthenticator(session, refreshApi, onSessionExpired));
        if (BuildConfig.DEBUG) {
            // BASIC logs method, URL and status only: never bodies or headers (passwords, tokens).
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            client.addInterceptor(logging);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .addConverterFactory(json)
                .build();
    }

    public String baseUrl() {
        return baseUrl;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> service) {
        return (T) services.computeIfAbsent(service, retrofit::create);
    }
}
