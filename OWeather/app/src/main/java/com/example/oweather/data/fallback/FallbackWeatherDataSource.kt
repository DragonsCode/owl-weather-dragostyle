package com.example.oweather.data.fallback

import android.content.Context
import com.example.oweather.core.model.DailyForecast
import com.example.oweather.core.model.ForecastSource
import com.example.oweather.core.model.WeatherForecast
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FallbackWeatherDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {

    fun loadFallbackForecast(
        cityKey: String,
        cityName: String,
        latitude: Double,
        longitude: Double
    ): WeatherForecast? {
        return runCatching {
            val json = context.assets.open(FALLBACK_FILE).bufferedReader().use { it.readText() }
            val fallback = gson.fromJson(json, FallbackPayload::class.java)
            val today = LocalDate.now()
            val daily = fallback.daily.map { item ->
                DailyForecast(
                    date = today.plusDays(item.offsetDays.toLong()).toString(),
                    temperatureMax = item.temperatureMax,
                    temperatureMin = item.temperatureMin,
                    weatherCode = item.weatherCode
                )
            }

            WeatherForecast(
                cityKey = cityKey,
                cityName = cityName,
                latitude = latitude,
                longitude = longitude,
                currentTemperature = fallback.current.temperature,
                currentWindSpeed = fallback.current.windSpeed,
                weatherCode = fallback.current.weatherCode,
                daily = daily,
                updatedAt = System.currentTimeMillis(),
                source = ForecastSource.FALLBACK_ASSET
            )
        }.getOrNull()
    }

    private data class FallbackPayload(
        val current: FallbackCurrent,
        val daily: List<FallbackDaily>
    )

    private data class FallbackCurrent(
        val temperature: Double,
        val windSpeed: Double,
        val weatherCode: Int
    )

    private data class FallbackDaily(
        val offsetDays: Int,
        val temperatureMax: Double,
        val temperatureMin: Double,
        val weatherCode: Int
    )

    private companion object {
        const val FALLBACK_FILE = "weather_fallback.json"
    }
}
