package com.andlill.keynotes.ui.shared.modifier

import android.content.res.Configuration
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration

fun Modifier.orientationModifiers(portrait: Modifier = Modifier, landscape: Modifier = Modifier): Modifier = composed {
    val config = LocalConfiguration.current
    when (config.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> landscape
        Configuration.ORIENTATION_PORTRAIT -> portrait
        else -> Modifier
    }
}