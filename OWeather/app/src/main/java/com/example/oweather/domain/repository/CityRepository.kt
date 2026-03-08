package com.example.oweather.domain.repository

import com.example.oweather.core.model.City
import com.example.oweather.core.model.GeoCity
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun observeCities(): Flow<List<City>>

    suspend fun getCityById(cityId: Long): City?

    suspend fun searchCities(query: String): List<GeoCity>

    suspend fun insertCity(city: GeoCity): Long

    suspend fun updateCity(city: City)

    suspend fun deleteCity(city: City)
}
