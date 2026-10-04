package com.musicroom.app.network.api;

import com.musicroom.app.network.dto.HealthDto;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SystemApi {

    @GET("api/v1/health")
    Call<HealthDto> health();
}
