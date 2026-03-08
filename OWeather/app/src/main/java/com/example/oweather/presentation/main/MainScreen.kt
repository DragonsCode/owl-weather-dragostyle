package com.example.oweather.presentation.main

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oweather.core.util.hasLocationPermission
import com.example.oweather.presentation.common.CurrentWeatherCard
import com.example.oweather.presentation.common.DailyForecastRow

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onOpenCities: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenCityDetails: (Long) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.setLocationPermission(granted)
    }

    LaunchedEffect(Unit) {
        val granted = context.hasLocationPermission()
        viewModel.setLocationPermission(granted)
        if (!granted) {
            permissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val error = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(error)
        viewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Forecast") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { viewModel.refreshWeather() }) {
                        Text("Обновить")
                    }
                    Button(onClick = onOpenCities) {
                        Text("Добавить город")
                    }
                    Button(onClick = onOpenCities) {
                        Text("Список городов")
                    }
                    Button(onClick = onOpenMap) {
                        Text("Карта")
                    }
                }
            }

            if (!uiState.locationPermissionGranted) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Локация отключена")
                            Text(
                                text = "Прогноз будет показан для выбранного города.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            uiState.selectedCity?.let { selected ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Основной город: ${selected.name}")
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(onClick = { onOpenCityDetails(selected.id) }) {
                                Text("Открыть детали")
                            }
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
                itemsIndexed(weather.daily) { index, day ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 + index * 4 })
                    ) {
                        DailyForecastRow(forecast = day)
                    }
                }
            } ?: item {
                Text(
                    text = if (uiState.isLoading) "Загрузка прогноза..." else "Нет данных прогноза",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
