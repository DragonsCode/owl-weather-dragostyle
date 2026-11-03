package com.example.oweather.data.fallback

import com.example.oweather.core.model.WeatherForecast

interface WeatherFallbackDataSource {
    fun loadFallbackForecast(
        cityKey: String,
        cityName: String,
        latitude: Double,
        longitude: Double
    ): WeatherForecast?
}
