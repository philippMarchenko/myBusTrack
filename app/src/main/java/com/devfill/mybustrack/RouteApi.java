package com.devfill.mybustrack;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RouteApi {
	   @GET("/maps/api/directions/json")
	    RouteResponse getRoute(
	            @Query(value = "origin",encoded = false) String position,
	            @Query(value = "destination", encoded = false) String destination,
	            @Query("sensor") boolean sensor,
	            @Query("language") String language);

}
