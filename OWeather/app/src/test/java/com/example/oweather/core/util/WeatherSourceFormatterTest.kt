package com.example.oweather.core.util

import com.example.oweather.core.model.ForecastSource
import com.example.oweather.core.model.WeatherForecast
import org.junit.Assert.*
import org.junit.Test

class WeatherSourceFormatterTest {
    private val forecast = WeatherForecast("test", "Test city", 1.0, 2.0, 12.0, 3.0, 1, emptyList(), 123456L, ForecastSource.NETWORK)

    @Test fun networkIncludesSourceAndObservationTime() {
        val label = formatForecastSource(forecast)
        assertTrue(label.startsWith("Сеть"))
        assertTrue(label.contains(formatTimestamp(forecast.updatedAt)))
    }

    @Test fun cacheIncludesSourceAndOriginalObservationTime() {
        val label = formatForecastSource(forecast.copy(source = ForecastSource.CACHE))
        assertTrue(label.startsWith("Сохранённый прогноз"))
        assertTrue(label.contains(formatTimestamp(forecast.updatedAt)))
    }

    @Test fun demoNeverClaimsAFreshForecastOrShowsAnObservationTime() {
        val label = formatForecastSource(forecast.copy(source = ForecastSource.FALLBACK_ASSET))
        assertTrue(label.contains("Демонстрационные данные"))
        assertTrue(label.contains("Не прогноз для выбранного города"))
        assertFalse(label.contains(formatTimestamp(forecast.updatedAt)))
        assertFalse(label.contains("Обновлено"))
    }
}
