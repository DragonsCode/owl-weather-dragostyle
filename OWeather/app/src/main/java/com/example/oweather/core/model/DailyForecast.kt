package com.example.oweather.core.model

data class DailyForecast(
    val date: String,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val weatherCode: Int
)
