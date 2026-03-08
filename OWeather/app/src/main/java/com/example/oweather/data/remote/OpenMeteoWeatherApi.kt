package com.example.oweather.data.remote

import com.example.oweather.data.remote.dto.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoWeatherApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weathercode",
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): ForecastResponse
}
