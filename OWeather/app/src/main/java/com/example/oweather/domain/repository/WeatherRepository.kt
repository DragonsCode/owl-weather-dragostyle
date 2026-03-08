package com.example.oweather.domain.repository

import com.example.oweather.core.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun observeWeather(cityKey: String): Flow<WeatherForecast?>

    suspend fun refreshWeather(
        cityKey: String,
        cityName: String,
        latitude: Double,
        longitude: Double
    ): Result<WeatherForecast>
}
