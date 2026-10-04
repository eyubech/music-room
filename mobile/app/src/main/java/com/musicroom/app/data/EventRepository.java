package com.musicroom.app.data;

import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.core.Resource;
import com.musicroom.app.core.ServiceLocator;
import com.musicroom.app.network.ApiCall;
import com.musicroom.app.network.api.EventApi;
import com.musicroom.app.network.dto.CreateEventRequest;
import com.musicroom.app.network.dto.EventDto;
import com.musicroom.app.network.dto.PageDto;

/** Music Track Vote events. */
public final class EventRepository {

    private static final int PAGE_SIZE = 50;

    private final ServiceLocator services;

    public EventRepository(ServiceLocator services) {
        this.services = services;
    }

    public void list(MutableLiveData<Resource<PageDto<EventDto>>> out) {
        ApiCall.enqueue(api().list(0, PAGE_SIZE), out);
    }

    public void create(CreateEventRequest request, MutableLiveData<Resource<EventDto>> out) {
        ApiCall.enqueue(api().create(request), out);
    }

    private EventApi api() {
        return services.api().create(EventApi.class);
    }
}
