package com.musicroom.app.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.MusicRoomApp;
import com.musicroom.app.core.AppConfig;
import com.musicroom.app.core.ClientInfo;
import com.musicroom.app.core.Resource;
import com.musicroom.app.core.ServiceLocator;
import com.musicroom.app.network.ApiCall;
import com.musicroom.app.network.api.SystemApi;
import com.musicroom.app.network.dto.HealthDto;

public class SettingsViewModel extends AndroidViewModel {

    private final ServiceLocator services;
    private final MutableLiveData<Resource<HealthDto>> health = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        services = MusicRoomApp.services(application);
    }

    public AppConfig config() {
        return services.config();
    }

    public ClientInfo clientInfo() {
        return services.clientInfo();
    }

    public LiveData<Resource<HealthDto>> health() {
        return health;
    }

    /** @param url a normalized address, not necessarily saved yet */
    public void test(String url) {
        ApiCall.enqueue(services.newClient(url).create(SystemApi.class).health(), health);
    }
}
