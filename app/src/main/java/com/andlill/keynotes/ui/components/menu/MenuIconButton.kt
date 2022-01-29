package com.andlill.keynotes.ui.components.menu

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MenuIconButton(icon: ImageVector, color: Color, description: String? = null, alpha: Float = 1f, onClick: () -> Unit) {
    IconButton(onClick = { onClick() }) {
        Icon(
            modifier = Modifier.alpha(alpha),
            contentDescription = description,
            imageVector = icon,
            tint = color,
        )
    }
}