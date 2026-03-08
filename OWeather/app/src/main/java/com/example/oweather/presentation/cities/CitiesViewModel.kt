package com.example.oweather.presentation.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oweather.core.model.City
import com.example.oweather.core.model.GeoCity
import com.example.oweather.data.preferences.UserPreferencesRepository
import com.example.oweather.domain.repository.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitiesUiState())
    val uiState: StateFlow<CitiesUiState> = _uiState.asStateFlow()

    init {
        observeSavedCities()
        observePreferences()
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun search() {
        viewModelScope.launch {
            val query = _uiState.value.searchQuery.trim()
            if (query.isEmpty()) {
                _uiState.update { it.copy(searchResults = emptyList()) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }
            val results = cityRepository.searchCities(query)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    searchResults = results,
                    error = if (results.isEmpty()) "Ничего не найдено" else null
                )
            }
        }
    }

    fun addCity(city: GeoCity) {
        viewModelScope.launch {
            val cityId = cityRepository.insertCity(city)
            preferencesRepository.setSelectedCityId(cityId)
            _uiState.update {
                it.copy(message = "Город добавлен", searchResults = emptyList(), searchQuery = "")
            }
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch {
            cityRepository.deleteCity(city)
            if (_uiState.value.selectedCityId == city.id) {
                preferencesRepository.setSelectedCityId(null)
            }
            _uiState.update { it.copy(message = "Город удален") }
        }
    }

    fun updateCity(city: City, newName: String, newNote: String) {
        viewModelScope.launch {
            val updated = city.copy(
                name = newName.trim().ifEmpty { city.name },
                note = newNote.trim().ifEmpty { null }
            )
            cityRepository.updateCity(updated)
            _uiState.update { it.copy(message = "Город обновлен") }
        }
    }

    fun setPrimaryCity(cityId: Long) {
        viewModelScope.launch {
            preferencesRepository.setSelectedCityId(cityId)
            _uiState.update { it.copy(message = "Основной город изменен") }
        }
    }

    fun clearTransientMessages() {
        _uiState.update { it.copy(message = null, error = null) }
    }

    private fun observeSavedCities() {
        viewModelScope.launch {
            cityRepository.observeCities().collect { cities ->
                _uiState.update { it.copy(savedCities = cities) }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collect { prefs ->
                _uiState.update { it.copy(selectedCityId = prefs.selectedCityId) }
            }
        }
    }
}
