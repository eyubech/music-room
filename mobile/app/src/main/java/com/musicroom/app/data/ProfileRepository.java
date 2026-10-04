package com.musicroom.app.data;

import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.core.Resource;
import com.musicroom.app.core.ServiceLocator;
import com.musicroom.app.network.ApiCall;
import com.musicroom.app.network.api.MeApi;
import com.musicroom.app.network.dto.GoogleTokenRequest;
import com.musicroom.app.network.dto.ProfileDto;
import com.musicroom.app.network.dto.ProfileUpdateRequest;

public final class ProfileRepository {

    private final ServiceLocator services;

    public ProfileRepository(ServiceLocator services) {
        this.services = services;
    }

    public void load(MutableLiveData<Resource<ProfileDto>> out) {
        ApiCall.enqueue(api().profile(), out);
    }

    public void update(ProfileUpdateRequest request, MutableLiveData<Resource<ProfileDto>> out) {
        ApiCall.enqueue(api().updateProfile(request), out);
    }

    public void linkGoogle(String idToken, MutableLiveData<Resource<ProfileDto>> out) {
        ApiCall.enqueue(api().linkGoogle(new GoogleTokenRequest(idToken)), out);
    }

    public void unlink(String provider, MutableLiveData<Resource<ProfileDto>> out) {
        ApiCall.enqueue(api().unlink(provider), out);
    }

    private MeApi api() {
        return services.api().create(MeApi.class);
    }
}
