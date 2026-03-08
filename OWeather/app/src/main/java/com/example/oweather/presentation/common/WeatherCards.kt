package com.example.oweather.presentation.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oweather.core.model.DailyForecast
import com.example.oweather.core.model.WeatherForecast
import com.example.oweather.core.util.formatIsoDate
import com.example.oweather.core.util.formatTimestamp
import com.example.oweather.core.util.mapWeatherCode

@Composable
fun CurrentWeatherCard(
    weather: WeatherForecast,
    modifier: Modifier = Modifier
) {
    val visual = mapWeatherCode(weather.weatherCode)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = visual.description,
                    tint = visual.color,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text(
                        text = weather.cityName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = visual.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${weather.currentTemperature}°C",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Ветер: ${weather.currentWindSpeed} м/с",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Обновлено: ${formatTimestamp(weather.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DailyForecastRow(
    forecast: DailyForecast,
    modifier: Modifier = Modifier
) {
    val visual = mapWeatherCode(forecast.weatherCode)
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = visual.description,
                    tint = visual.color,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column {
                    Text(text = formatIsoDate(forecast.date))
                    Text(
                        text = visual.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(text = "${forecast.temperatureMin}° / ${forecast.temperatureMax}°")
        }
    }
}
