package com.example.oweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("current_weather")
    val currentWeather: CurrentWeatherDto?,
    val daily: DailyDto?
)

data class CurrentWeatherDto(
    val temperature: Double,
    val windspeed: Double,
    val weathercode: Int
)

data class DailyDto(
    val time: List<String>?,
    @SerializedName("temperature_2m_max")
    val temperatureMax: List<Double>?,
    @SerializedName("temperature_2m_min")
    val temperatureMin: List<Double>?,
    val weathercode: List<Int>?
)
