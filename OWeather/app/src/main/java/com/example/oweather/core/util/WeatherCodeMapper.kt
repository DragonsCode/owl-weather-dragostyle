package com.example.oweather.core.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class WeatherVisual(
    val description: String,
    val icon: ImageVector,
    val color: Color
)

fun mapWeatherCode(code: Int): WeatherVisual {
    return when (code) {
        0 -> WeatherVisual("Ясно", Icons.Filled.WbSunny, Color(0xFFFFB300))
        1, 2, 3 -> WeatherVisual("Переменная облачность", Icons.Filled.Cloud, Color(0xFF607D8B))
        45, 48 -> WeatherVisual("Туман", Icons.Filled.Cloud, Color(0xFF90A4AE))
        51, 53, 55, 56, 57 -> WeatherVisual("Морось", Icons.Filled.Grain, Color(0xFF4FC3F7))
        61, 63, 65, 66, 67, 80, 81, 82 -> WeatherVisual("Дождь", Icons.Filled.Umbrella, Color(0xFF2196F3))
        71, 73, 75, 77, 85, 86 -> WeatherVisual("Снег", Icons.Filled.AcUnit, Color(0xFF81D4FA))
        95, 96, 99 -> WeatherVisual("Гроза", Icons.Filled.Thunderstorm, Color(0xFF5E35B1))
        else -> WeatherVisual("Неизвестно", Icons.Filled.Cloud, Color(0xFFB0BEC5))
    }
}
