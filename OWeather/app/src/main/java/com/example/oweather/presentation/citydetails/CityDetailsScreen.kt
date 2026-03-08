package com.example.oweather.presentation.citydetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oweather.presentation.common.CurrentWeatherCard
import com.example.oweather.presentation.common.DailyForecastRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDetailsScreen(
    onBack: () -> Unit,
    viewModel: CityDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.city?.name ?: "Детали города") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Button(onClick = viewModel::refreshWeather, modifier = Modifier.fillMaxWidth()) {
                    Text(if (uiState.isLoading) "Обновление..." else "Обновить")
                }
            }

            uiState.city?.let { city ->
                item {
                    Column {
                        Text("Координаты: ${city.latitude}, ${city.longitude}")
                        city.note?.let { note ->
                            Text(
                                text = "Заметка: $note",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            uiState.weather?.let { weather ->
                item {
                    CurrentWeatherCard(weather = weather)
                }
                item {
                    Text(
                        text = "Прогноз на 7 дней",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(weather.daily) { day ->
                    DailyForecastRow(forecast = day)
                }
            }

            uiState.errorMessage?.let { error ->
                item {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
