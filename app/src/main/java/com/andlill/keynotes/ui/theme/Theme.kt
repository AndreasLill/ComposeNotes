package com.andlill.keynotes.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val isDynamicColorScheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val isDarkTheme = isSystemInDarkTheme()

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