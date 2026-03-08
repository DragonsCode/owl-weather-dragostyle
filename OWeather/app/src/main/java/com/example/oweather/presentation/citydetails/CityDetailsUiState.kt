package com.example.oweather.presentation.citydetails

import com.example.oweather.core.model.City
import com.example.oweather.core.model.WeatherForecast

data class CityDetailsUiState(
    val isLoading: Boolean = false,
    val city: City? = null,
    val weather: WeatherForecast? = null,
    val errorMessage: String? = null
)
