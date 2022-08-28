package com.andlill.keynotes.ui.shared.modifier

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@SuppressLint("ComposableNaming")
@Composable
fun OrientationPadding(portrait: PaddingValues = PaddingValues(), landscape: PaddingValues = PaddingValues()): PaddingValues {
    val config = LocalConfiguration.current
    return when (config.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> landscape
        Configuration.ORIENTATION_PORTRAIT -> portrait
        else -> PaddingValues()
    }
}