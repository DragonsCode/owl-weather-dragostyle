package com.example.oweather.data.repository

import com.example.oweather.core.model.City
import com.example.oweather.core.model.DailyForecast
import com.example.oweather.core.model.ForecastSource
import com.example.oweather.core.model.GeoCity
import com.example.oweather.core.model.WeatherForecast
import com.example.oweather.data.local.entity.CityEntity
import com.example.oweather.data.local.entity.WeatherCacheEntity
import com.example.oweather.data.remote.dto.ForecastResponse
import com.example.oweather.data.remote.dto.GeoResultDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun CityEntity.toDomain(): City {
    return City(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        note = note,
        createdAt = createdAt
    )
}

fun City.toEntity(): CityEntity {
    return CityEntity(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        note = note,
        createdAt = createdAt
    )
}

fun GeoCity.toEntity(createdAt: Long): CityEntity {
    return CityEntity(
        name = name,
        latitude = latitude,
        longitude = longitude,
        note = null,
        createdAt = createdAt
    )
}

fun GeoResultDto.toDomain(): GeoCity {
    return GeoCity(
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country,
        adminArea = admin1
    )
}

fun ForecastResponse.toDomain(
    cityKey: String,
    cityName: String,
    source: ForecastSource
): WeatherForecast? {
    val current = currentWeather ?: return null
    val dailyDto = daily

    val dates = dailyDto?.time.orEmpty()
    val maxValues = dailyDto?.temperatureMax.orEmpty()
    val minValues = dailyDto?.temperatureMin.orEmpty()
    val codes = dailyDto?.weathercode.orEmpty()

    val size = listOf(dates.size, maxValues.size, minValues.size, codes.size).minOrNull() ?: 0

    val dailyForecast = buildList {
        repeat(size) { index ->
            add(
                DailyForecast(
                    date = dates[index],
                    temperatureMax = maxValues[index],
                    temperatureMin = minValues[index],
                    weatherCode = codes[index]
                )
            )
        }
    }

    return WeatherForecast(
        cityKey = cityKey,
        cityName = cityName,
        latitude = latitude,
        longitude = longitude,
        currentTemperature = current.temperature,
        currentWindSpeed = current.windspeed,
        weatherCode = current.weathercode,
        daily = dailyForecast,
        updatedAt = System.currentTimeMillis(),
        source = source
    )
}

fun WeatherForecast.toCacheEntity(gson: Gson): WeatherCacheEntity {
    return WeatherCacheEntity(
        cityKey = cityKey,
        cityName = cityName,
        latitude = latitude,
        longitude = longitude,
        currentTemperature = currentTemperature,
        currentWindSpeed = currentWindSpeed,
        weatherCode = weatherCode,
        dailyJson = gson.toJson(daily),
        updatedAt = updatedAt,
        source = source.name
    )
}

fun WeatherCacheEntity.toDomain(gson: Gson): WeatherForecast {
    val listType = object : TypeToken<List<DailyForecast>>() {}.type
    val daily = runCatching {
        gson.fromJson<List<DailyForecast>>(dailyJson, listType)
    }.getOrDefault(emptyList())

    val forecastSource = runCatching { ForecastSource.valueOf(source) }
        .getOrDefault(ForecastSource.CACHE)

    return WeatherForecast(
        cityKey = cityKey,
        cityName = cityName,
        latitude = latitude,
        longitude = longitude,
        currentTemperature = currentTemperature,
        currentWindSpeed = currentWindSpeed,
        weatherCode = weatherCode,
        daily = daily,
        updatedAt = updatedAt,
        source = forecastSource
    )
}
