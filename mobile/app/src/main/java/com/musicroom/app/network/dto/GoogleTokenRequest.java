package com.musicroom.app.network.dto;

/** Google ID token, verified by the backend before it issues its own session. */
public final class GoogleTokenRequest {
    public final String idToken;

    public GoogleTokenRequest(String idToken) {
        this.idToken = idToken;
    }
}
