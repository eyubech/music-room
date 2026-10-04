package com.musicroom.app.data;

import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.core.Resource;
import com.musicroom.app.core.ServiceLocator;
import com.musicroom.app.network.ApiCall;
import com.musicroom.app.network.api.PlaylistApi;
import com.musicroom.app.network.dto.CreatePlaylistRequest;
import com.musicroom.app.network.dto.PageDto;
import com.musicroom.app.network.dto.PlaylistDto;

/** Music Playlist Editor. */
public final class PlaylistRepository {

    private static final int PAGE_SIZE = 50;

    private final ServiceLocator services;

    public PlaylistRepository(ServiceLocator services) {
        this.services = services;
    }

    public void list(MutableLiveData<Resource<PageDto<PlaylistDto>>> out) {
        ApiCall.enqueue(api().list(0, PAGE_SIZE), out);
    }

    public void create(CreatePlaylistRequest request, MutableLiveData<Resource<PlaylistDto>> out) {
        ApiCall.enqueue(api().create(request), out);
    }

    private PlaylistApi api() {
        return services.api().create(PlaylistApi.class);
    }
}
