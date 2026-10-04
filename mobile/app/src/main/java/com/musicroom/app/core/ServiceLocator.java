package com.musicroom.app.core;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.BuildConfig;
import com.musicroom.app.data.AuthRepository;
import com.musicroom.app.data.DeviceRepository;
import com.musicroom.app.data.EventRepository;
import com.musicroom.app.data.PlaylistRepository;
import com.musicroom.app.data.ProfileRepository;
import com.musicroom.app.network.ApiClient;

/** Creates and holds the app-wide objects. One instance, owned by MusicRoomApp. */
public final class ServiceLocator {

    private static final String PREFS = "music_room";
    private static final String FALLBACK_API_URL = "http://10.0.2.2:8080/";

    public final AuthRepository auth;
    public final ProfileRepository profile;
    public final EventRepository events;
    public final PlaylistRepository playlists;
    public final DeviceRepository devices;

    private final AppConfig config;
    private final SessionStore session;
    private final ClientInfo clientInfo;
    private final MutableLiveData<OneShot<Boolean>> sessionExpired = new MutableLiveData<>();
    private ApiClient apiClient;

    public ServiceLocator(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String defaultUrl = AppConfig.normalize(BuildConfig.DEFAULT_API_URL);
        config = new AppConfig(prefs, defaultUrl != null ? defaultUrl : FALLBACK_API_URL);
        session = new SessionStore(prefs);
        clientInfo = ClientInfo.create(prefs);

        auth = new AuthRepository(this);
        profile = new ProfileRepository(this);
        events = new EventRepository(this);
        playlists = new PlaylistRepository(this);
        devices = new DeviceRepository(this);
    }

    public AppConfig config() {
        return config;
    }

    public SessionStore session() {
        return session;
    }

    public ClientInfo clientInfo() {
        return clientInfo;
    }

    /** Emits when the server rejects the refresh token and the user has to log in again. */
    public LiveData<OneShot<Boolean>> sessionExpired() {
        return sessionExpired;
    }

    /** Client for the configured address, rebuilt when the address changes in Settings. */
    public synchronized ApiClient api() {
        String url = config.getApiUrl();
        if (apiClient == null || !apiClient.baseUrl().equals(url)) {
            apiClient = newClient(url);
        }
        return apiClient;
    }

    /** Client for any address, e.g. to test one before saving it. */
    public ApiClient newClient(String url) {
        return new ApiClient(url, clientInfo, session,
                () -> sessionExpired.postValue(new OneShot<>(true)));
    }
}
