package com.musicroom.app.network.dto;

public final class VerifyEmailRequest {
    public final String email;
    public final String code;

    public VerifyEmailRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
