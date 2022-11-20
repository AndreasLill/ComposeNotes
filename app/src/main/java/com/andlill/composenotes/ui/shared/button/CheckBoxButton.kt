package com.andlill.composenotes.ui.shared.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CheckBoxButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    uncheckedBackgroundColor: Color = Color.Transparent,
    checkedBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedBorderColor: Color = MaterialTheme.colorScheme.onSurface,
    checkedBorderColor: Color = MaterialTheme.colorScheme.primary,
    checkMarkColor: Color = MaterialTheme.colorScheme.onPrimary,
    checkMarkIcon: ImageVector = Icons.Filled.Check,
    rippleSize: Dp = 24.dp,
    checkBoxSize: Dp = 24.dp,
    checkMarkSize: Dp = 20.dp,
    checked: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .size(checkBoxSize)
            .then(
                if (enabled)
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(
                            bounded = false,
                            radius = rippleSize,
                        ),
                        onClick = onClick
                    )
                else
                    Modifier
            ),
        color = if (checked) checkedBackgroundColor else uncheckedBackgroundColor,
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(
            width = 1.2.dp,
            color = if (checked) checkedBorderColor else uncheckedBorderColor,
        ),
        content = {
            Box {
                if (checked) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(checkMarkSize),
                        contentDescription = null,
                        imageVector = checkMarkIcon,
                        tint = checkMarkColor,
                    )
                }
            }
        }
    )
}