package com.example.oweather.presentation.cities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oweather.core.model.City

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitiesScreen(
    onBack: () -> Unit,
    onCityOpen: (Long) -> Unit,
    viewModel: CitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var cityToEdit by remember { mutableStateOf<City?>(null) }

    LaunchedEffect(uiState.message, uiState.error) {
        val text = uiState.message ?: uiState.error
        if (!text.isNullOrBlank()) {
            snackbarHostState.showSnackbar(text)
            viewModel.clearTransientMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Города") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Поиск через Open-Meteo Geocoding",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onQueryChanged,
                    label = { Text("Введите город") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                Button(
                    onClick = { viewModel.search() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.isLoading) "Поиск..." else "Найти")
                }
            }

            if (uiState.searchResults.isNotEmpty()) {
                item {
                    Text(
                        text = "Результаты",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(uiState.searchResults) { city ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = city.name, fontWeight = FontWeight.SemiBold)
                            Text(text = "${city.country.orEmpty()} ${city.adminArea.orEmpty()}")
                            Text(text = "${city.latitude}, ${city.longitude}")
                            Button(onClick = { viewModel.addCity(city) }) {
                                Text("Добавить")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Сохраненные города",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (uiState.savedCities.isEmpty()) {
                item {
                    Text("Список пуст")
                }
            }

            items(uiState.savedCities, key = { it.id }) { city ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = city.name, fontWeight = FontWeight.Bold)
                                if (uiState.selectedCityId == city.id) {
                                    Text(
                                        text = "Основной город",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                city.note?.let {
                                    Text(text = "Заметка: $it")
                                }
                                Text(text = "${city.latitude}, ${city.longitude}")
                            }
                            Row {
                                IconButton(onClick = { cityToEdit = city }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                                }
                                IconButton(onClick = { viewModel.deleteCity(city) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onCityOpen(city.id) }) {
                                Text("Детали")
                            }
                            Button(onClick = { viewModel.setPrimaryCity(city.id) }) {
                                Text("Сделать основным")
                            }
                        }
                    }
                }
            }
        }
    }

    cityToEdit?.let { city ->
        EditCityDialog(
            city = city,
            onDismiss = { cityToEdit = null },
            onSave = { name, note ->
                viewModel.updateCity(city, name, note)
                cityToEdit = null
            }
        )
    }
}

@Composable
private fun EditCityDialog(
    city: City,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember(city.id) { mutableStateOf(city.name) }
    var note by remember(city.id) { mutableStateOf(city.note.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать город") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Заметка") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, note) }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
