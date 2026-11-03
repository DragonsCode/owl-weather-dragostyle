package com.example.oweather.data.repository

import com.example.oweather.core.model.ForecastSource
import com.example.oweather.core.model.WeatherForecast
import com.example.oweather.data.fallback.WeatherFallbackDataSource
import com.example.oweather.data.local.dao.WeatherCacheDao
import com.example.oweather.data.local.entity.WeatherCacheEntity
import com.example.oweather.data.remote.OpenMeteoWeatherApi
import com.example.oweather.data.remote.dto.CurrentWeatherDto
import com.example.oweather.data.remote.dto.ForecastResponse
import com.google.gson.Gson
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class WeatherRepositoryImplTest {
    private val gson = Gson()
    private val real = WeatherForecast("city_test", "Test city", 1.0, 2.0, 12.0, 3.0, 1, emptyList(), 123456L, ForecastSource.NETWORK)
    private val demo = real.copy(currentTemperature = 25.0, updatedAt = 999999L, source = ForecastSource.FALLBACK_ASSET)

    private class FakeDao(var cached: WeatherCacheEntity? = null) : WeatherCacheDao {
        var writes = 0
        override fun observeByCityKey(cityKey: String): Flow<WeatherCacheEntity?> = flowOf(cached)
        override suspend fun getByCityKey(cityKey: String) = cached
        override suspend fun upsert(cache: WeatherCacheEntity) { writes++; cached = cache }
    }

    private class FakeFallback(private val weather: WeatherForecast?) : WeatherFallbackDataSource {
        var calls = 0
        override fun loadFallbackForecast(cityKey: String, cityName: String, latitude: Double, longitude: Double): WeatherForecast? {
            calls++
            return weather
        }
    }

    private fun api(error: Exception? = IOException("offline")) = object : OpenMeteoWeatherApi {
        override suspend fun getForecast(latitude: Double, longitude: Double, daily: String, currentWeather: Boolean, timezone: String, forecastDays: Int): ForecastResponse {
            if (error != null) throw error
            return ForecastResponse(1.0, 2.0, CurrentWeatherDto(14.0, 3.0, 1), null)
        }
    }

    private suspend fun WeatherRepositoryImpl.refresh() = refreshWeather("city_test", "Test city", 1.0, 2.0)

    @Test fun offlinePreservesRealCacheAndItsTimestamp() = runBlocking {
        val original = real.toCacheEntity(gson)
        val dao = FakeDao(original)
        val fallback = FakeFallback(demo)
        val result = WeatherRepositoryImpl(api(), dao, fallback, gson).refresh().getOrThrow()
        assertEquals(real.copy(source = ForecastSource.CACHE), result)
        assertEquals(original, dao.cached)
        assertEquals(0, dao.writes)
        assertEquals(0, fallback.calls)
    }

    @Test fun demoWithoutCacheIsNeverPersisted() = runBlocking {
        val dao = FakeDao()
        val result = WeatherRepositoryImpl(api(), dao, FakeFallback(demo), gson).refresh().getOrThrow()
        assertEquals(ForecastSource.FALLBACK_ASSET, result.source)
        assertNull(dao.cached)
        assertEquals(0, dao.writes)
    }

    @Test fun legacyDemoCacheIsNotRelabeledAsRealCache() = runBlocking {
        val dao = FakeDao(demo.toCacheEntity(gson))
        val result = WeatherRepositoryImpl(api(), dao, FakeFallback(null), gson).refresh()
        assertTrue(result.isFailure)
        assertEquals(0, dao.writes)
    }

    @Test fun observedNetworkRecordIsLabeledCacheWithOriginalTimestamp() = runBlocking {
        val dao = FakeDao(real.toCacheEntity(gson))
        val observed = WeatherRepositoryImpl(api(), dao, FakeFallback(null), gson).observeWeather("city_test").first()
        assertEquals(real.copy(source = ForecastSource.CACHE), observed)
    }

    @Test fun observedLegacyDemoKeepsDemoLabel() = runBlocking {
        val dao = FakeDao(demo.toCacheEntity(gson))
        val observed = WeatherRepositoryImpl(api(), dao, FakeFallback(null), gson).observeWeather("city_test").first()
        assertEquals(ForecastSource.FALLBACK_ASSET, observed?.source)
    }

    @Test fun successfulNetworkResponseReplacesCache() = runBlocking {
        val dao = FakeDao(real.toCacheEntity(gson))
        val fallback = FakeFallback(demo)
        val result = WeatherRepositoryImpl(api(null), dao, fallback, gson).refresh().getOrThrow()
        assertEquals(ForecastSource.NETWORK, result.source)
        assertEquals(14.0, result.currentTemperature, 0.0)
        assertEquals(result.toCacheEntity(gson), dao.cached)
        assertEquals(1, dao.writes)
        assertEquals(0, fallback.calls)
    }

    @Test fun noAvailableDataRetainsNetworkFailure() = runBlocking {
        val error = IOException("offline")
        val result = WeatherRepositoryImpl(api(error), FakeDao(), FakeFallback(null), gson).refresh()
        assertSame(error, result.exceptionOrNull())
    }

    @Test fun cancellationDoesNotReadFallbackOrWriteCache() = runBlocking {
        val error = CancellationException("cancelled")
        val dao = FakeDao()
        val fallback = FakeFallback(demo)
        try {
            WeatherRepositoryImpl(api(error), dao, fallback, gson).refresh()
            fail("Cancellation must propagate")
        } catch (actual: CancellationException) {
            assertSame(error, actual)
        }
        assertEquals(0, fallback.calls)
        assertEquals(0, dao.writes)
    }
}
