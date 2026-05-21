package com.example.proyectofinalmovil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CinemaBlueBright,
    onPrimary = NightBackground,
    primaryContainer = CinemaBlue,
    onPrimaryContainer = NightText,
    secondary = ButterHighlight,
    onSecondary = NightBackground,
    secondaryContainer = NightSurfaceAlt,
    onSecondaryContainer = NightText,
    tertiary = CinemaBlueDeep,
    onTertiary = NightText,
    background = NightBackground,
    onBackground = NightText,
    surface = NightSurface,
    onSurface = NightText,
    surfaceVariant = NightSurfaceAlt,
    onSurfaceVariant = NightTextSoft,
    outline = ButterHighlight.copy(alpha = 0.20f),
)

private val LightColorScheme = lightColorScheme(
    primary = CinemaBlue,
    onPrimary = CreamSurface,
    primaryContainer = CinemaBlueBright,
    onPrimaryContainer = CreamSurface,
    secondary = ButterHighlight,
    onSecondary = WarmText,
    secondaryContainer = CreamSurfaceAlt,
    onSecondaryContainer = WarmText,
    tertiary = CinemaBlueDeep,
    onTertiary = CreamSurface,
    background = CreamBackground,
    onBackground = WarmText,
    surface = CreamSurface,
    onSurface = WarmText,
    surfaceVariant = CreamSurfaceAlt,
    onSurfaceVariant = WarmTextSoft,
    outline = WarmBorder,
)

@Composable
fun ProyectoFinalMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
