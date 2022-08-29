package com.andlill.keynotes.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val isDynamicColorScheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val isDarkTheme = isSystemInDarkTheme()

    setLightNoteColors(colorResource(R.color.light_surface))
    setDarkNoteColors(colorResource(R.color.dark_surface))

    MaterialTheme(
        colorScheme = when {
            isDynamicColorScheme && isDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
            isDynamicColorScheme && !isDarkTheme -> dynamicLightColorScheme(LocalContext.current)
            isDarkTheme -> darkColorScheme()
            else -> lightColorScheme()
        },
        typography = AppTypography,
        shapes = Shapes(),
        content = content
    )
}