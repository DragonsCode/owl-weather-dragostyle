package com.example.oweather.data.repository

import com.example.oweather.core.model.ForecastSource
import com.example.oweather.core.model.WeatherForecast
import com.example.oweather.data.fallback.FallbackWeatherDataSource
import com.example.oweather.data.local.dao.WeatherCacheDao
import com.example.oweather.data.remote.OpenMeteoWeatherApi
import com.example.oweather.domain.repository.WeatherRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: OpenMeteoWeatherApi,
    private val weatherCacheDao: WeatherCacheDao,
    private val fallbackSource: FallbackWeatherDataSource,
    private val gson: Gson
) : WeatherRepository {

    override fun observeWeather(cityKey: String): Flow<WeatherForecast?> {
        return weatherCacheDao.observeByCityKey(cityKey)
            .map { entity -> entity?.toDomain(gson) }
    }

    override suspend fun refreshWeather(
        cityKey: String,
        cityName: String,
        latitude: Double,
        longitude: Double
    ): Result<WeatherForecast> {
        return try {
            val response = weatherApi.getForecast(latitude = latitude, longitude = longitude)
            val weather = response.toDomain(
                cityKey = cityKey,
                cityName = cityName,
                source = ForecastSource.NETWORK
            ) ?: error("Weather response is empty")

            weatherCacheDao.upsert(weather.toCacheEntity(gson))
            Result.success(weather)
        } catch (networkError: Exception) {
            Timber.w(networkError, "Network weather refresh failed for %s", cityName)

            val fallback = fallbackSource.loadFallbackForecast(
                cityKey = cityKey,
                cityName = cityName,
                latitude = latitude,
                longitude = longitude
            )

            if (fallback != null) {
                weatherCacheDao.upsert(fallback.toCacheEntity(gson))
                return Result.success(fallback)
            }

            val cached = weatherCacheDao.getByCityKey(cityKey)?.toDomain(gson)
                ?.copy(source = ForecastSource.CACHE)

            if (cached != null) {
                Result.success(cached)
            } else {
                Result.failure(networkError)
            }
        }
    }
}
