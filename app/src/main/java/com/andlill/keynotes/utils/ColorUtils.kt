package com.andlill.keynotes.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

object ColorUtils {
    fun Color.darken(value: Float = 0.5f): Color {
        return Color(ColorUtils.blendARGB(this.toArgb(), Color.Black.toArgb(), value))
    }
}