package com.example.oweather.core.model

data class GeoCity(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val adminArea: String?
)
