package com.andlill.keynotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.andlill.keynotes.R

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

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val light = lightColors(
        primary = colorResource(R.color.light_primary),
        onPrimary = colorResource(R.color.light_on_primary),
        background = colorResource(R.color.light_surface),
        onBackground = colorResource(R.color.light_on_surface),
        surface = colorResource(R.color.light_surface),
        onSurface = colorResource(R.color.light_on_surface),
    )

    val dark = darkColors(
        primary = colorResource(R.color.dark_primary),
        onPrimary = colorResource(R.color.dark_on_primary),
        background = colorResource(R.color.dark_surface),
        onBackground = colorResource(R.color.dark_on_surface),
        surface = colorResource(R.color.dark_surface),
        onSurface = colorResource(R.color.dark_on_surface),
    )

    MaterialTheme(
        colors = if (isSystemInDarkTheme()) dark else light,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}