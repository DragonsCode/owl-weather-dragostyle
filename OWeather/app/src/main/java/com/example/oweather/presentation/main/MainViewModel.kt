package com.example.oweather.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oweather.core.model.City
import com.example.oweather.core.model.WeatherForecast
import com.example.oweather.data.preferences.UserPreferencesRepository
import com.example.oweather.domain.location.LocationResult
import com.example.oweather.domain.location.LocationTracker
import com.example.oweather.domain.repository.CityRepository
import com.example.oweather.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var weatherObservationJob: Job? = null
    private var observedCityKey: String? = null
    private var locationStateInitialized = false

    init {
        observeCities()
        observePreferences()
    }

    fun setLocationPermission(granted: Boolean) {
        locationStateInitialized = true
        val changed = granted != _uiState.value.locationPermissionGranted
        _uiState.update { it.copy(locationPermissionGranted = granted) }
        if (changed) {
            refreshWeather()
        } else if (_uiState.value.weather == null) {
            refreshWeather()
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val target = resolveTarget()
            if (target == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        weather = null,
                        errorMessage = "Доступ к локации отсутствует. Выберите город в списке."
                    )
                }
                return@launch
            }

            startWeatherObservation(target.cityKey)
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = weatherRepository.refreshWeather(
                cityKey = target.cityKey,
                cityName = target.cityName,
                latitude = target.latitude,
                longitude = target.longitude
            )

            result
                .onSuccess { weather ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            weather = weather,
                            errorMessage = null
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Не удалось обновить прогноз. Показаны локальные данные, если доступны."
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun observeCities() {
        viewModelScope.launch {
            cityRepository.observeCities().collect { cities ->
                _uiState.update { current ->
                    val selected = cities.firstOrNull { it.id == current.selectedCityId }
                    current.copy(savedCities = cities, selectedCity = selected)
                }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collect { prefs ->
                _uiState.update { current ->
                    val selected = current.savedCities.firstOrNull { it.id == prefs.selectedCityId }
                    current.copy(selectedCityId = prefs.selectedCityId, selectedCity = selected)
                }
                if (locationStateInitialized) {
                    refreshWeather()
                }
            }
        }
    }

    private suspend fun resolveTarget(): WeatherTarget? {
        if (_uiState.value.locationPermissionGranted) {
            when (val location = locationTracker.getCurrentLocation()) {
                is LocationResult.Success -> {
                    return WeatherTarget(
                        cityKey = CURRENT_LOCATION_KEY,
                        cityName = "Текущее местоположение",
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }

                LocationResult.PermissionDenied -> {
                    // fall through to selected city
                }

                LocationResult.Unavailable -> {
                    // fall through to selected city
                }
            }
        }

        val selectedCity: City = _uiState.value.selectedCity ?: return null
        return WeatherTarget(
            cityKey = cityKeyForCity(selectedCity.id),
            cityName = selectedCity.name,
            latitude = selectedCity.latitude,
            longitude = selectedCity.longitude
        )
    }

    private fun startWeatherObservation(cityKey: String) {
        if (observedCityKey == cityKey) return

        observedCityKey = cityKey
        weatherObservationJob?.cancel()
        weatherObservationJob = viewModelScope.launch {
            weatherRepository.observeWeather(cityKey).collect { cachedWeather ->
                if (cachedWeather != null) {
                    _uiState.update { it.copy(weather = cachedWeather) }
                }
            }
        }
    }

    private data class WeatherTarget(
        val cityKey: String,
        val cityName: String,
        val latitude: Double,
        val longitude: Double
    )

    companion object {
        const val CURRENT_LOCATION_KEY = "current_location"

        fun cityKeyForCity(cityId: Long): String = "city_$cityId"
    }
}
