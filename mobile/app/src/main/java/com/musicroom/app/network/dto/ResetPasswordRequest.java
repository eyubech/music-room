package com.musicroom.app.network.dto;

public final class ResetPasswordRequest {
    public final String email;
    public final String code;
    public final String newPassword;

    public ResetPasswordRequest(String email, String code, String newPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
    }
}
