package com.musicroom.app.ui.devices;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.MusicRoomApp;
import com.musicroom.app.core.Resource;
import com.musicroom.app.data.DeviceRepository;
import com.musicroom.app.network.dto.DelegationDto;
import com.musicroom.app.network.dto.DeviceDto;

import java.util.List;

public class DevicesViewModel extends AndroidViewModel {

    private final DeviceRepository repository;
    private final MutableLiveData<Resource<List<DeviceDto>>> devices = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<DelegationDto>>> delegations = new MutableLiveData<>();

    public DevicesViewModel(@NonNull Application application) {
        super(application);
        repository = MusicRoomApp.services(application).devices;
    }

    public LiveData<Resource<List<DeviceDto>>> devices() {
        return devices;
    }

    public LiveData<Resource<List<DelegationDto>>> delegations() {
        return delegations;
    }

    public void refresh() {
        repository.devices(devices);
        repository.receivedDelegations(delegations);
    }
}
