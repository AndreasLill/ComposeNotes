package com.andlill.keynotes.ui.label

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoteLabel(text: String) {
    Surface(
        modifier = Modifier.height(24.dp),
        color = MaterialTheme.colors.surface.copy(0.4f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically).size(16.dp),
                imageVector = Icons.Outlined.Label,
                tint = MaterialTheme.colors.onSurface,
                contentDescription = "Label"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}