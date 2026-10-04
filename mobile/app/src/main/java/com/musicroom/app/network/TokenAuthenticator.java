package com.musicroom.app.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.musicroom.app.core.SessionStore;
import com.musicroom.app.network.api.AuthApi;
import com.musicroom.app.network.dto.RefreshRequest;
import com.musicroom.app.network.dto.TokenResponse;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * On a 401, trades the refresh token for a new pair (the backend rotates refresh tokens) and
 * retries the request once. If the refresh token is rejected, the session is over.
 */
public final class TokenAuthenticator implements Authenticator {

    private final SessionStore session;
    private final AuthApi refreshApi;
    private final Runnable onSessionExpired;

    /** @param refreshApi must use a client without this authenticator, to avoid loops */
    public TokenAuthenticator(SessionStore session, AuthApi refreshApi, Runnable onSessionExpired) {
        this.session = session;
        this.refreshApi = refreshApi;
        this.onSessionExpired = onSessionExpired;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) {
        String sent = response.request().header("Authorization");
        if (sent == null || response.priorResponse() != null) {
            return null; // anonymous call, or the retry was rejected too
        }
        synchronized (this) {
            String access = session.getAccessToken();
            if (access != null && !sent.equals("Bearer " + access)) {
                return withToken(response.request(), access); // another call refreshed meanwhile
            }
            String refresh = session.getRefreshToken();
            if (refresh == null) {
                return null;
            }
            retrofit2.Response<TokenResponse> result;
            try {
                result = refreshApi.refresh(new RefreshRequest(refresh)).execute();
            } catch (IOException e) {
                return null; // offline: keep the session, only this call fails
            }
            TokenResponse tokens = result.body();
            if (result.isSuccessful() && tokens != null) {
                session.save(tokens.accessToken, tokens.refreshToken);
                return withToken(response.request(), tokens.accessToken);
            }
            if (result.code() == 400 || result.code() == 401 || result.code() == 403) {
                session.clear();
                onSessionExpired.run();
            }
            return null;
        }
    }

    private static Request withToken(Request request, String token) {
        return request.newBuilder().header("Authorization", "Bearer " + token).build();
    }
}
