package com.example.oweather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val cityKey: String,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val currentTemperature: Double,
    val currentWindSpeed: Double,
    val weatherCode: Int,
    val dailyJson: String,
    val updatedAt: Long,
    val source: String
)
