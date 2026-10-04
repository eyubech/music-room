package com.musicroom.app.network.api;

import com.musicroom.app.network.dto.DelegationDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/** Music Control Delegation. */
public interface DelegationApi {

    /** @param role "granted" (by me) or "received" (control shared with me) */
    @GET("api/v1/delegations")
    Call<List<DelegationDto>> list(@Query("role") String role);
}
