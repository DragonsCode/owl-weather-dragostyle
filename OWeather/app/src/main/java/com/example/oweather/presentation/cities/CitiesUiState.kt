package com.example.oweather.presentation.cities

import com.example.oweather.core.model.City
import com.example.oweather.core.model.GeoCity

data class CitiesUiState(
    val savedCities: List<City> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<GeoCity> = emptyList(),
    val selectedCityId: Long? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
