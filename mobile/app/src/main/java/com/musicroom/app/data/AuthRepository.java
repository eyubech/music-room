package com.musicroom.app.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.core.Resource;
import com.musicroom.app.core.ServiceLocator;
import com.musicroom.app.network.ApiCall;
import com.musicroom.app.network.api.AuthApi;
import com.musicroom.app.network.dto.EmailRequest;
import com.musicroom.app.network.dto.GoogleTokenRequest;
import com.musicroom.app.network.dto.LoginRequest;
import com.musicroom.app.network.dto.RefreshRequest;
import com.musicroom.app.network.dto.RegisterRequest;
import com.musicroom.app.network.dto.ResetPasswordRequest;
import com.musicroom.app.network.dto.TokenResponse;
import com.musicroom.app.network.dto.VerifyEmailRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class AuthRepository {

    private final ServiceLocator services;

    public AuthRepository(ServiceLocator services) {
        this.services = services;
    }

    public void login(String email, String password, MutableLiveData<Resource<TokenResponse>> out) {
        ApiCall.enqueue(api().login(new LoginRequest(email, password)), out, this::startSession);
    }

    public void loginWithGoogle(String idToken, MutableLiveData<Resource<TokenResponse>> out) {
        ApiCall.enqueue(api().loginWithGoogle(new GoogleTokenRequest(idToken)), out, this::startSession);
    }

    public void register(String displayName, String email, String password,
                         MutableLiveData<Resource<Void>> out) {
        ApiCall.enqueue(api().register(new RegisterRequest(displayName, email, password)), out);
    }

    /** A verified account is logged in right away. */
    public void verifyEmail(String email, String code, MutableLiveData<Resource<TokenResponse>> out) {
        ApiCall.enqueue(api().verifyEmail(new VerifyEmailRequest(email, code)), out, this::startSession);
    }

    public void resendVerification(String email, MutableLiveData<Resource<Void>> out) {
        ApiCall.enqueue(api().resendVerification(new EmailRequest(email)), out);
    }

    public void forgotPassword(String email, MutableLiveData<Resource<Void>> out) {
        ApiCall.enqueue(api().forgotPassword(new EmailRequest(email)), out);
    }

    public void resetPassword(String email, String code, String newPassword,
                              MutableLiveData<Resource<Void>> out) {
        ApiCall.enqueue(api().resetPassword(new ResetPasswordRequest(email, code, newPassword)), out);
    }

    /** Revokes the refresh token on the server (best effort) and forgets the session locally. */
    public void logout() {
        String refreshToken = services.session().getRefreshToken();
        if (refreshToken != null) {
            api().logout(new RefreshRequest(refreshToken)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                }
            });
        }
        services.session().clear();
    }

    private void startSession(TokenResponse tokens) {
        if (tokens != null) {
            services.session().save(tokens.accessToken, tokens.refreshToken);
        }
    }

    private AuthApi api() {
        return services.api().create(AuthApi.class);
    }
}
