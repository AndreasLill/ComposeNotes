package com.andlill.keynotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.andlill.keynotes.R

lateinit var LightNoteColors: List<Color>
lateinit var DarkNoteColors: List<Color>

private fun setLightNoteColors(defaultColor: Color) {
    LightNoteColors = listOf(
        defaultColor,

        Color(0xFFFFCDD2),
        Color(0xFFF8BBD0),
        Color(0xFFE1BEE7),
        Color(0xFFD1C4E9),
        Color(0xFFC5CAE9),
        Color(0xFFBBDEFB),
        Color(0xFFB3E5FC),
        Color(0xFFB2EBF2),

        Color(0xFFB2DFDB),
        Color(0xFFC8E6C9),
        Color(0xFFDCEDC8),
        Color(0xFFF0F4C3),
        Color(0xFFFFF9C4),
        Color(0xFFFFECB3),
        Color(0xFFFFE0B2),
        Color(0xFFFFCCBC),
    )
}

private fun setDarkNoteColors(defaultColor: Color) {
    DarkNoteColors = mutableListOf(
        defaultColor,

        Color(0xFF421B1B),
        Color(0xFF492136),
        Color(0xFF3C2953),
        Color(0xFF251E47),
        Color(0xFF23274E),
        Color(0xFF14243C),
        Color(0xFF162E41),
        Color(0xFF1F3E3F),

        Color(0xFF123A33),
        Color(0xFF133C16),
        Color(0xFF1D3613),
        Color(0xFF443F18),
        Color(0xFF462D17),
        Color(0xFF582C09),
        Color(0xFF491F09),
        Color(0xFF44190B),
    )
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    // Theme colors.
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

    // Note colors.
    setLightNoteColors(colorResource(R.color.light_surface))
    setDarkNoteColors(colorResource(R.color.dark_surface))

    MaterialTheme(
        colors = if (isSystemInDarkTheme()) dark else light,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}