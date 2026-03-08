package com.example.oweather.presentation.main

import com.example.oweather.core.model.City
import com.example.oweather.core.model.WeatherForecast

data class MainUiState(
    val isLoading: Boolean = false,
    val weather: WeatherForecast? = null,
    val savedCities: List<City> = emptyList(),
    val selectedCityId: Long? = null,
    val selectedCity: City? = null,
    val locationPermissionGranted: Boolean = false,
    val errorMessage: String? = null
)
