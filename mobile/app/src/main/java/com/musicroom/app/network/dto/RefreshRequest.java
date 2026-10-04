package com.musicroom.app.network.dto;

public final class RefreshRequest {
    public final String refreshToken;

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
