package com.andlill.composenotes.app.home.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimeLabel(
    icon: ImageVector,
    text: String,
    repeat: String?,
    noteColor: Int = 0
) {
    Surface(
        color = if (noteColor == 0) MaterialTheme.colorScheme.onSurface.copy(0.08f) else MaterialTheme.colorScheme.surface.copy(0.4f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = icon,
                    tint = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = text,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
            }
            repeat?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Outlined.Repeat,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                    )
                }
            }
        }
    }
}