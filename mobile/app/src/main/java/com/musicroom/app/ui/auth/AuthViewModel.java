package com.musicroom.app.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.MusicRoomApp;
import com.musicroom.app.core.Resource;
import com.musicroom.app.data.AuthRepository;
import com.musicroom.app.network.dto.TokenResponse;

/** Shared by the authentication screens; each screen gets its own instance. */
public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository auth;
    /** Calls that open a session: login, Google, email verification. */
    private final MutableLiveData<Resource<TokenResponse>> session = new MutableLiveData<>();
    /** Calls without a session: register, resend code, forgot / reset password. */
    private final MutableLiveData<Resource<Void>> action = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        auth = MusicRoomApp.services(application).auth;
    }

    public LiveData<Resource<TokenResponse>> session() {
        return session;
    }

    public LiveData<Resource<Void>> action() {
        return action;
    }

    public void login(String email, String password) {
        auth.login(email, password, session);
    }

    public void loginWithGoogle(String idToken) {
        auth.loginWithGoogle(idToken, session);
    }

    public void verifyEmail(String email, String code) {
        auth.verifyEmail(email, code, session);
    }

    public void register(String displayName, String email, String password) {
        auth.register(displayName, email, password, action);
    }

    public void resendVerification(String email) {
        auth.resendVerification(email, action);
    }

    public void forgotPassword(String email) {
        auth.forgotPassword(email, action);
    }

    public void resetPassword(String email, String code, String newPassword) {
        auth.resetPassword(email, code, newPassword, action);
    }
}
