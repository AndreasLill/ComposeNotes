package com.andlill.composenotes.app.note.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoteLabelChip(
    background: Color,
    icon: ImageVector,
    text: String?,
    contentAlpha: Float = 1f,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.height(32.dp),
        color = background,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = icon,
                tint = MaterialTheme.colorScheme.onSurface.copy(contentAlpha),
                contentDescription = null
            )
            text?.let {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(contentAlpha)
                )
            }
        }
    }
}