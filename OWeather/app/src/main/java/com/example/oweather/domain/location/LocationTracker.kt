package com.example.oweather.domain.location

interface LocationTracker {
    suspend fun getCurrentLocation(): LocationResult
}

sealed interface LocationResult {
    data class Success(val latitude: Double, val longitude: Double) : LocationResult
    data object PermissionDenied : LocationResult
    data object Unavailable : LocationResult
}
