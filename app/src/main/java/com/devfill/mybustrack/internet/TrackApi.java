package com.devfill.mybustrack.internet;

import com.devfill.mybustrack.model.TrackInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TrackApi {

    @GET("/track.php")
    Call<TrackInfo> getTrackInfo(
            @Query(value = "event") String event,
            @Query(value = "trackId") String trackId);
}
