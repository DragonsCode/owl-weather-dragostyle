package com.example.oweather.presentation.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oweather.R
import com.example.oweather.core.util.hasLocationPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val mapKey = stringResource(R.string.google_maps_key)
    val hasMapKey = mapKey.isNotBlank() && !mapKey.contains("ADD_YOUR_MAPS_API_KEY", ignoreCase = true)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.setLocationPermission(granted)
    }

    LaunchedEffect(Unit) {
        val granted = context.hasLocationPermission()
        viewModel.setLocationPermission(granted)
        if (!granted) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Карта") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (!hasMapKey) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.maps_setup_title),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = stringResource(R.string.maps_setup_message))
                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://developers.google.com/maps/documentation/android-sdk/get-api-key")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.open_maps_docs))
                }
            }
            return@Scaffold
        }

        val firstPosition = when {
            uiState.currentLocation != null -> LatLng(
                uiState.currentLocation!!.first,
                uiState.currentLocation!!.second
            )

            uiState.cities.isNotEmpty() -> LatLng(
                uiState.cities.first().latitude,
                uiState.cities.first().longitude
            )

            else -> LatLng(41.3111, 69.2797)
        }

        val cameraState = rememberCameraPositionState()

        LaunchedEffect(firstPosition) {
            cameraState.animate(CameraUpdateFactory.newLatLngZoom(firstPosition, 9f))
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            cameraPositionState = cameraState,
            properties = MapProperties(
                isMyLocationEnabled = uiState.permissionGranted
            )
        ) {
            uiState.currentLocation?.let { (lat, lon) ->
                Marker(
                    state = MarkerState(position = LatLng(lat, lon)),
                    title = "Текущее местоположение",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }

            uiState.cities.forEach { city ->
                Marker(
                    state = MarkerState(position = LatLng(city.latitude, city.longitude)),
                    title = city.name,
                    snippet = city.note
                )
            }
        }
    }
}
