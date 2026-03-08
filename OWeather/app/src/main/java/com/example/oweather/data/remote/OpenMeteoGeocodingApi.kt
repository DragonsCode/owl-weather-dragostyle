package com.example.oweather.data.remote

import com.example.oweather.data.remote.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoGeocodingApi {
    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "ru",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}
