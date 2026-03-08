package com.example.oweather.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = CloudWhite,
    secondary = SunAccent,
    background = CloudWhite,
    onBackground = StormGray,
    surface = CloudWhite,
    onSurface = StormGray,
    surfaceVariant = Mist,
    onSurfaceVariant = DeepBlue
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = CloudWhite,
    secondary = SunAccent,
    background = StormGray,
    onBackground = CloudWhite,
    surface = DeepBlue,
    onSurface = CloudWhite,
    surfaceVariant = Color(0xFF37474F),
    onSurfaceVariant = CloudWhite
)

@Composable
fun OWeatherTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
