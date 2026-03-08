package com.example.oweather.data.repository

import com.example.oweather.core.model.City
import com.example.oweather.core.model.GeoCity
import com.example.oweather.data.local.dao.CityDao
import com.example.oweather.data.remote.OpenMeteoGeocodingApi
import com.example.oweather.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
    private val geocodingApi: OpenMeteoGeocodingApi
) : CityRepository {

    override fun observeCities(): Flow<List<City>> {
        return cityDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getCityById(cityId: Long): City? {
        return cityDao.getById(cityId)?.toDomain()
    }

    override suspend fun searchCities(query: String): List<GeoCity> {
        if (query.isBlank()) return emptyList()

        return runCatching {
            geocodingApi.searchCities(query.trim())
                .results
                .orEmpty()
                .map { it.toDomain() }
        }.getOrDefault(emptyList())
    }

    override suspend fun insertCity(city: GeoCity): Long {
        return cityDao.insert(city.toEntity(createdAt = System.currentTimeMillis()))
    }

    override suspend fun updateCity(city: City) {
        cityDao.update(city.toEntity())
    }

    override suspend fun deleteCity(city: City) {
        cityDao.delete(city.toEntity())
    }
}
