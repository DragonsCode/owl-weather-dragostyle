package com.example.oweather.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oweather.domain.location.LocationResult
import com.example.oweather.domain.location.LocationTracker
import com.example.oweather.domain.repository.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            cityRepository.observeCities().collect { cities ->
                _uiState.update { it.copy(cities = cities) }
            }
        }
    }

    fun setLocationPermission(granted: Boolean) {
        _uiState.update { it.copy(permissionGranted = granted) }
        if (granted) {
            loadCurrentLocation()
        } else {
            _uiState.update { it.copy(currentLocation = null) }
        }
    }

    fun loadCurrentLocation() {
        if (!_uiState.value.permissionGranted) return

        viewModelScope.launch {
            when (val result = locationTracker.getCurrentLocation()) {
                is LocationResult.Success -> {
                    _uiState.update {
                        it.copy(
                            currentLocation = result.latitude to result.longitude,
                            errorMessage = null
                        )
                    }
                }

                LocationResult.PermissionDenied -> {
                    _uiState.update {
                        it.copy(errorMessage = "Нет доступа к локации")
                    }
                }

                LocationResult.Unavailable -> {
                    _uiState.update {
                        it.copy(errorMessage = "Текущая позиция недоступна")
                    }
                }
            }
        }
    }
}
