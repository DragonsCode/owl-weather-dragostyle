package com.example.oweather.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.example.oweather.core.util.hasLocationPermission
import com.example.oweather.domain.location.LocationResult
import com.example.oweather.domain.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FusedLocationTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationTracker {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationResult {
        if (!context.hasLocationPermission()) {
            return LocationResult.PermissionDenied
        }

        return runCatching {
            val tokenSource = CancellationTokenSource()
            val currentLocation = fusedLocationProviderClient
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, tokenSource.token)
                .await()

            val location = currentLocation ?: fusedLocationProviderClient.lastLocation.await()
            if (location != null) {
                LocationResult.Success(latitude = location.latitude, longitude = location.longitude)
            } else {
                LocationResult.Unavailable
            }
        }.getOrElse {
            LocationResult.Unavailable
        }
    }
}
