package com.musicroom.app.network.api;

import com.musicroom.app.network.dto.EmailRequest;
import com.musicroom.app.network.dto.GoogleTokenRequest;
import com.musicroom.app.network.dto.LoginRequest;
import com.musicroom.app.network.dto.RefreshRequest;
import com.musicroom.app.network.dto.RegisterRequest;
import com.musicroom.app.network.dto.ResetPasswordRequest;
import com.musicroom.app.network.dto.TokenResponse;
import com.musicroom.app.network.dto.VerifyEmailRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("api/v1/auth/register")
    Call<Void> register(@Body RegisterRequest body);

    @POST("api/v1/auth/login")
    Call<TokenResponse> login(@Body LoginRequest body);

    @POST("api/v1/auth/verify-email")
    Call<TokenResponse> verifyEmail(@Body VerifyEmailRequest body);

    @POST("api/v1/auth/resend-verification")
    Call<Void> resendVerification(@Body EmailRequest body);

    @POST("api/v1/auth/forgot-password")
    Call<Void> forgotPassword(@Body EmailRequest body);

    @POST("api/v1/auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest body);

    @POST("api/v1/auth/oauth/google")
    Call<TokenResponse> loginWithGoogle(@Body GoogleTokenRequest body);

    @POST("api/v1/auth/refresh")
    Call<TokenResponse> refresh(@Body RefreshRequest body);

    @POST("api/v1/auth/logout")
    Call<Void> logout(@Body RefreshRequest body);
}
