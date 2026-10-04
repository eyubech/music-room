package com.musicroom.app.network.api;

import com.musicroom.app.network.dto.CreateEventRequest;
import com.musicroom.app.network.dto.EventDto;
import com.musicroom.app.network.dto.PageDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/** Music Track Vote events. */
public interface EventApi {

    /** Public events plus private ones the user is invited to. */
    @GET("api/v1/events")
    Call<PageDto<EventDto>> list(@Query("page") int page, @Query("size") int size);

    @POST("api/v1/events")
    Call<EventDto> create(@Body CreateEventRequest body);
}
