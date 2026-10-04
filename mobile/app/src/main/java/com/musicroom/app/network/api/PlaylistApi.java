package com.musicroom.app.network.api;

import com.musicroom.app.network.dto.CreatePlaylistRequest;
import com.musicroom.app.network.dto.PageDto;
import com.musicroom.app.network.dto.PlaylistDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/** Music Playlist Editor. */
public interface PlaylistApi {

    /** Public playlists plus private ones the user is invited to. */
    @GET("api/v1/playlists")
    Call<PageDto<PlaylistDto>> list(@Query("page") int page, @Query("size") int size);

    @POST("api/v1/playlists")
    Call<PlaylistDto> create(@Body CreatePlaylistRequest body);
}
