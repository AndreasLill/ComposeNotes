package com.andlill.keynotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightAppPalette = lightColors(
    primary = Color(0xFFB39DDB),
    onPrimary = Color(0xFF263238),
    secondary = Color(0xFF5C6BC0),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFECEFF1),
    onBackground = Color(0xFF263238),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF263238),
)

private val DarkAppPalette = darkColors(
    primary = Color.DarkGray,
    onPrimary = Color.White,
    secondary = Color.Blue,
    onSecondary = Color.White,
    background = Color.DarkGray,
    onBackground = Color.White,
    surface = Color.DarkGray,
    onSurface = Color.White,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) DarkAppPalette else LightAppPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}