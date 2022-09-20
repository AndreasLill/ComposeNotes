package com.andlill.composenotes.ui.shared.label

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoteLabel(modifier: Modifier = Modifier, icon: ImageVector, text: String, color: Color) {
    Surface(
        modifier = modifier.height(24.dp),
        color = color,
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically).size(16.dp),
                imageVector = icon,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}