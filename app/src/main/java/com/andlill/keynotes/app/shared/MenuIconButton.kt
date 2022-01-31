package com.andlill.keynotes.app.shared

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MenuIconButton(icon: ImageVector, color: Color, description: String? = null, alpha: Float = 1f, onClick: () -> Unit) {
    IconButton(onClick = { onClick() }) {
        Icon(
            contentDescription = description,
            imageVector = icon,
            tint = color.copy(alpha),
        )
    }
}