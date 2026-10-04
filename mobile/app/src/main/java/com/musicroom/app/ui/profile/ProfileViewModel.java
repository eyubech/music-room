package com.musicroom.app.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.MusicRoomApp;
import com.musicroom.app.core.Resource;
import com.musicroom.app.core.ServiceLocator;
import com.musicroom.app.network.dto.ProfileDto;
import com.musicroom.app.network.dto.ProfileUpdateRequest;

/** Used by the profile screen and the edit screen (each with its own instance). */
public class ProfileViewModel extends AndroidViewModel {

    static final String PROVIDER_GOOGLE = "google";

    private final ServiceLocator services;
    private final MutableLiveData<Resource<ProfileDto>> profile = new MutableLiveData<>();
    private final MutableLiveData<Resource<ProfileDto>> saved = new MutableLiveData<>();

    /** The edit form is filled from the server only once, so user edits survive rotation. */
    boolean formFilled;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        services = MusicRoomApp.services(application);
    }

    public LiveData<Resource<ProfileDto>> profile() {
        return profile;
    }

    public LiveData<Resource<ProfileDto>> saved() {
        return saved;
    }

    public void load() {
        services.profile.load(profile);
    }

    public void save(ProfileUpdateRequest request) {
        services.profile.update(request, saved);
    }

    public void linkGoogle(String idToken) {
        services.profile.linkGoogle(idToken, profile);
    }

    public void unlinkGoogle() {
        services.profile.unlink(PROVIDER_GOOGLE, profile);
    }

    public void logout() {
        services.auth.logout();
    }
}
