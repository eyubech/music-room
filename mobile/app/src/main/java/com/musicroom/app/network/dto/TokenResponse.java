package com.musicroom.app.network.dto;

public final class TokenResponse {
    public String accessToken;
    public String refreshToken;
    /** Access token lifetime in seconds. */
    public long expiresIn;
}
