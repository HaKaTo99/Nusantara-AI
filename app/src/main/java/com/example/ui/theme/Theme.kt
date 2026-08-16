package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color(0xFF003344),
    primaryContainer = Color(0xFF004D66),
    onPrimaryContainer = Color(0xFFB8EAFF),
    secondary = NeonViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3B1E6D),
    onSecondaryContainer = Color(0xFFEADBFF),
    tertiary = EmeraldGreen,
    onTertiary = Color(0xFF003820),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder,
    error = CoralError
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007A99),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F3FF),
    onPrimaryContainer = Color(0xFF001F29),
    secondary = Color(0xFF6A1B9A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5F5),
    onSecondaryContainer = Color(0xFF2A004E),
    tertiary = Color(0xFF008955),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder,
    error = CoralError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
