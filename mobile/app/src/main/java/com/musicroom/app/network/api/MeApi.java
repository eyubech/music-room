package com.musicroom.app.network.api;

import com.musicroom.app.network.dto.DeviceDto;
import com.musicroom.app.network.dto.GoogleTokenRequest;
import com.musicroom.app.network.dto.ProfileDto;
import com.musicroom.app.network.dto.ProfileUpdateRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/** Endpoints about the authenticated user. */
public interface MeApi {

    @GET("api/v1/me")
    Call<ProfileDto> profile();

    @PATCH("api/v1/me")
    Call<ProfileDto> updateProfile(@Body ProfileUpdateRequest body);

    @POST("api/v1/me/links/google")
    Call<ProfileDto> linkGoogle(@Body GoogleTokenRequest body);

    @DELETE("api/v1/me/links/{provider}")
    Call<ProfileDto> unlink(@Path("provider") String provider);

    @GET("api/v1/me/devices")
    Call<List<DeviceDto>> devices();
}
