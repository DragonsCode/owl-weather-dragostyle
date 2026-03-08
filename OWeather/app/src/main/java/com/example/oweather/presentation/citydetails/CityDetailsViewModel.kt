package com.example.oweather.presentation.citydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oweather.navigation.AppRoute
import com.example.oweather.presentation.main.MainViewModel
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
class CityDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityDetailsUiState())
    val uiState: StateFlow<CityDetailsUiState> = _uiState.asStateFlow()

    private val cityId: Long = savedStateHandle
        .get<Long>(AppRoute.CityDetails.CITY_ID_ARG)
        ?: -1L

    private var weatherObservationJob: Job? = null

    init {
        loadCity()
    }

    fun refreshWeather() {
        val city = _uiState.value.city ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = weatherRepository.refreshWeather(
                cityKey = MainViewModel.cityKeyForCity(city.id),
                cityName = city.name,
                latitude = city.latitude,
                longitude = city.longitude
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
                            errorMessage = "Не удалось обновить данные города"
                        )
                    }
                }
        }
    }

    private fun loadCity() {
        viewModelScope.launch {
            if (cityId <= 0L) {
                _uiState.update { it.copy(errorMessage = "Город не найден") }
                return@launch
            }

            val city = cityRepository.getCityById(cityId)
            if (city == null) {
                _uiState.update { it.copy(errorMessage = "Город не найден") }
                return@launch
            }

            _uiState.update { it.copy(city = city) }
            observeCachedWeather(city.id)
            refreshWeather()
        }
    }

    private fun observeCachedWeather(cityId: Long) {
        val key = MainViewModel.cityKeyForCity(cityId)
        weatherObservationJob?.cancel()
        weatherObservationJob = viewModelScope.launch {
            weatherRepository.observeWeather(key).collect { weather ->
                if (weather != null) {
                    _uiState.update { it.copy(weather = weather) }
                }
            }
        }
    }
}
