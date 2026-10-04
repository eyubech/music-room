package com.musicroom.app.ui.events;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.MusicRoomApp;
import com.musicroom.app.core.Resource;
import com.musicroom.app.data.EventRepository;
import com.musicroom.app.network.dto.CreateEventRequest;
import com.musicroom.app.network.dto.EventDto;
import com.musicroom.app.network.dto.Visibility;
import com.musicroom.app.network.dto.VoteLicense;

import java.time.LocalDate;
import java.time.LocalTime;

/** Holds the form choices (options, venue, time window) so they survive configuration changes. */
public class CreateEventViewModel extends AndroidViewModel {

    private final EventRepository events;
    private final MutableLiveData<Resource<EventDto>> result = new MutableLiveData<>();

    Visibility visibility = Visibility.PUBLIC;
    VoteLicense license = VoteLicense.EVERYONE;

    @Nullable
    Double latitude;
    @Nullable
    Double longitude;
    LocalDate date = LocalDate.now();
    LocalTime start = LocalTime.of(16, 0);
    LocalTime end = LocalTime.of(18, 0);

    public CreateEventViewModel(@NonNull Application application) {
        super(application);
        events = MusicRoomApp.services(application).events;
    }

    public LiveData<Resource<EventDto>> result() {
        return result;
    }

    public void create(CreateEventRequest request) {
        events.create(request, result);
    }
}
