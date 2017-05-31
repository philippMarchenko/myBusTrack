package com.devfill.mybustrack;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RouteApi {
	   @GET("/maps/api/directions/json")
	   Call<RouteResponse> getRoute(
	            @Query(value = "origin",encoded = false) String position,
	            @Query(value = "destination", encoded = false) String destination,
				@Query(value = "key") String key,
	            @Query("sensor") boolean sensor,
	            @Query("language") String language);

}
