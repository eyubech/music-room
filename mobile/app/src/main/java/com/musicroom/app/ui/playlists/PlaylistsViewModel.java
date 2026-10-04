package com.musicroom.app.ui.playlists;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.musicroom.app.MusicRoomApp;
import com.musicroom.app.core.Resource;
import com.musicroom.app.data.PlaylistRepository;
import com.musicroom.app.network.dto.CreatePlaylistRequest;
import com.musicroom.app.network.dto.EditLicense;
import com.musicroom.app.network.dto.PageDto;
import com.musicroom.app.network.dto.PlaylistDto;
import com.musicroom.app.network.dto.Visibility;

/** Used by the list screen and the create screen (each with its own instance). */
public class PlaylistsViewModel extends AndroidViewModel {

    private final PlaylistRepository playlists;
    private final MutableLiveData<Resource<PageDto<PlaylistDto>>> list = new MutableLiveData<>();
    private final MutableLiveData<Resource<PlaylistDto>> created = new MutableLiveData<>();

    /** Choices of the create form, kept across configuration changes. */
    Visibility visibility = Visibility.PUBLIC;
    EditLicense editLicense = EditLicense.EVERYONE;

    public PlaylistsViewModel(@NonNull Application application) {
        super(application);
        playlists = MusicRoomApp.services(application).playlists;
    }

    public LiveData<Resource<PageDto<PlaylistDto>>> playlists() {
        return list;
    }

    public LiveData<Resource<PlaylistDto>> created() {
        return created;
    }

    public void refresh() {
        playlists.list(list);
    }

    public void create(CreatePlaylistRequest request) {
        playlists.create(request, created);
    }
}
