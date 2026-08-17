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
    primary = LightPrimaryCyan,              // Deep Sapphire Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFF6FF),    // Soft Blue 50
    onPrimaryContainer = Color(0xFF1E3A8A),  // Deep Blue 900
    secondary = LightSecondaryViolet,        // Purple 700
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5F3FF),
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = LightEmeraldGreen,            // Emerald 700
    onTertiary = Color.White,
    background = LightBackground,            // Slate 50 (Pure crisp paper)
    onBackground = LightTextPrimary,         // Slate 900 (Ultra-sharp black)
    surface = LightSurface,                  // Pure White
    onSurface = LightTextPrimary,            // Slate 900
    surfaceVariant = LightSurfaceVariant,    // Slate 100
    onSurfaceVariant = LightTextSecondary,  // Slate 700
    outline = LightCardBorder,               // Slate 300
    error = LightCoralError
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
