package com.example.oweather.core.util

import com.example.oweather.core.model.ForecastSource
import com.example.oweather.core.model.WeatherForecast

fun formatForecastSource(weather: WeatherForecast): String = when (weather.source) {
    ForecastSource.NETWORK -> "Сеть · Получено: ${formatTimestamp(weather.updatedAt)}"
    ForecastSource.CACHE -> "Сохранённый прогноз · Данные от: ${formatTimestamp(weather.updatedAt)}"
    ForecastSource.FALLBACK_ASSET -> "Демонстрационные данные · Не прогноз для выбранного города"
}
