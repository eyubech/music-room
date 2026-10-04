package com.musicroom.app.core;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Holds the session tokens. They live in the app's private storage, which other apps cannot
 * read, and backups are disabled in the manifest so they never leave the device.
 * Thread-safe: the token refresh runs on OkHttp threads.
 */
public final class SessionStore {

    private static final String KEY_ACCESS = "session.access_token";
    private static final String KEY_REFRESH = "session.refresh_token";

    private final SharedPreferences prefs;
    private final MutableLiveData<Boolean> loggedIn;

    public SessionStore(SharedPreferences prefs) {
        this.prefs = prefs;
        this.loggedIn = new MutableLiveData<>(isLoggedIn());
    }

    @Nullable
    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS, null);
    }

    @Nullable
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH, null);
    }

    public boolean isLoggedIn() {
        return getRefreshToken() != null;
    }

    /** Emits on every login, token refresh and logout. */
    public LiveData<Boolean> loggedIn() {
        return loggedIn;
    }

    public void save(String accessToken, String refreshToken) {
        prefs.edit()
                .putString(KEY_ACCESS, accessToken)
                .putString(KEY_REFRESH, refreshToken)
                .apply();
        loggedIn.postValue(true);
    }

    public void clear() {
        prefs.edit().remove(KEY_ACCESS).remove(KEY_REFRESH).apply();
        loggedIn.postValue(false);
    }
}
