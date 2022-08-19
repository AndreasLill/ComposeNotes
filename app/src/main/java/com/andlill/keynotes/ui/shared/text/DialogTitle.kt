package com.andlill.keynotes.ui.shared.text

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun DialogTitle(text: String, color: Color = MaterialTheme.colors.primary) {
    Text(
        text = text.uppercase(),
        letterSpacing = 1.sp,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = color
    )
}