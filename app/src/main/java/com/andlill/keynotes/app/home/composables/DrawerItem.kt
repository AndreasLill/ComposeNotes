package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerItem(icon: ImageVector, text: String, color: Color = MaterialTheme.colors.onSurface, showEditButton: Boolean = false, onClick: () -> Unit = {}, onEditClick: () -> Unit = {}) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .clickable { onClick() }
        .padding(start = 16.dp, end = 16.dp)
    ) {
        Row(modifier = Modifier.align(CenterStart)) {
            Icon(
                modifier = Modifier.align(CenterVertically),
                imageVector = icon,
                contentDescription = null,
                tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                modifier = Modifier.align(CenterVertically),
                text = text,
                fontSize = 15.sp,
                color = color)
        }
        if (showEditButton) {
            IconButton(
                modifier = Modifier.align(CenterEnd),
                onClick = {
                    onEditClick()
                }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = color.copy(0.25f))
            }
        }
    }
}