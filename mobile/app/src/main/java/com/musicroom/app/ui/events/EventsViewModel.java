package com.musicroom.app.ui.events;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.MusicRoomApp;
import com.musicroom.app.core.Resource;
import com.musicroom.app.data.EventRepository;
import com.musicroom.app.network.dto.EventDto;
import com.musicroom.app.network.dto.PageDto;

public class EventsViewModel extends AndroidViewModel {

    private final EventRepository events;
    private final MutableLiveData<Resource<PageDto<EventDto>>> list = new MutableLiveData<>();

    public EventsViewModel(@NonNull Application application) {
        super(application);
        events = MusicRoomApp.services(application).events;
    }

    public LiveData<Resource<PageDto<EventDto>>> events() {
        return list;
    }

    public void refresh() {
        events.list(list);
    }
}
