package com.musicroom.app.network.dto;

/** Body of "resend verification code" and "forgot password". */
public final class EmailRequest {
    public final String email;

    public EmailRequest(String email) {
        this.email = email;
    }
}
