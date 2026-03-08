package com.example.oweather.presentation.map

import com.example.oweather.core.model.City

data class MapUiState(
    val cities: List<City> = emptyList(),
    val permissionGranted: Boolean = false,
    val currentLocation: Pair<Double, Double>? = null,
    val errorMessage: String? = null
)
