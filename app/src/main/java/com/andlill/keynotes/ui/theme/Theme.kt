package com.andlill.keynotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightNoteColors = listOf(
    Color(0xFFFFCDD2),
    Color(0xFFE1BEE7),
    Color(0xFFC5CAE9),
    Color(0xFFB3E5FC),
    Color(0xFFB2DFDB),
    Color(0xFFDCEDC8),
    Color(0xFFFFF9C4),
    Color(0xFFFFE0B2),
)

val DarkNoteColors = listOf(
    Color(0xFF613B3B),
    Color(0xFF413352),
    Color(0xFF3A3E5F),
    Color(0xFF31414E),
    Color(0xFF2F4D48),
    Color(0xFF384B30),
    Color(0xFF554638),
    Color(0xFF533E34),
)

private val LightAppPalette = lightColors(
    primary = Color(0xFFFF1744),
    onPrimary = Color(0xFF263238),
    secondary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF263238),
    background = Color(0xFFECEFF1),
    onBackground = Color(0xFF263238),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF263238),
)

private val DarkAppPalette = darkColors(
    primary = Color(0xFFFF1744),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF263238),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFF263238),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF263238),
    onSurface = Color(0xFFFFFFFF),
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