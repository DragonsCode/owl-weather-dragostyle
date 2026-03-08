package com.example.oweather.core.model

enum class ForecastSource {
    NETWORK,
    CACHE,
    FALLBACK_ASSET
}

data class WeatherForecast(
    val cityKey: String,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val currentTemperature: Double,
    val currentWindSpeed: Double,
    val weatherCode: Int,
    val daily: List<DailyForecast>,
    val updatedAt: Long,
    val source: ForecastSource
)
