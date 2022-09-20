package com.andlill.composenotes.ui.shared.modifier

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.focusIndicatorLine(
    interactionSource: MutableInteractionSource,
    baseColor: Color,
    focusColor: Color,
    baseWidth: Dp = Dp.Hairline,
    focusWidth: Dp = 2.dp,
    padding: Dp = 0.dp
): Modifier = composed {
    val isFocused by interactionSource.collectIsFocusedAsState()
    drawBehind {
        val color = if (isFocused) focusColor else baseColor
        val width = if (isFocused) focusWidth else baseWidth
        val strokeWidth = width.value * density
        val y = size.height - strokeWidth / 2
        drawLine(
            color = color,
            start = Offset(padding.toPx(), y),
            end = Offset(size.width - padding.toPx(), y),
            strokeWidth = strokeWidth
        )
    }
}