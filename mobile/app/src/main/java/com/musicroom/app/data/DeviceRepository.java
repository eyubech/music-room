package com.musicroom.app.data;

import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.core.Resource;
import com.musicroom.app.core.ServiceLocator;
import com.musicroom.app.network.ApiCall;
import com.musicroom.app.network.api.DelegationApi;
import com.musicroom.app.network.api.MeApi;
import com.musicroom.app.network.dto.DelegationDto;
import com.musicroom.app.network.dto.DeviceDto;

import java.util.List;

/** The user's devices and Music Control Delegation. */
public final class DeviceRepository {

    private final ServiceLocator services;

    public DeviceRepository(ServiceLocator services) {
        this.services = services;
    }

    public void devices(MutableLiveData<Resource<List<DeviceDto>>> out) {
        ApiCall.enqueue(services.api().create(MeApi.class).devices(), out);
    }

    /** Devices other users let this user control. */
    public void receivedDelegations(MutableLiveData<Resource<List<DelegationDto>>> out) {
        ApiCall.enqueue(services.api().create(DelegationApi.class).list("received"), out);
    }
}
