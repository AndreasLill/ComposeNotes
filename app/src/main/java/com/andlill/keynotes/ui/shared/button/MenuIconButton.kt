package com.andlill.keynotes.ui.shared.button

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MenuIconButton(modifier: Modifier = Modifier, icon: ImageVector, color: Color, description: String? = null, alpha: Float = 1f, onClick: () -> Unit) {
    IconButton(
        modifier = modifier,
        onClick = onClick) {
        Icon(
            contentDescription = description,
            imageVector = icon,
            tint = color.copy(alpha),
        )
    }
}