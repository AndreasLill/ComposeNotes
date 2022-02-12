package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerItem(icon: ImageVector, text: String, onClick: () -> Unit = {}) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .clickable { onClick() }
        .padding(start = 16.dp, end = 16.dp)) {
        Icon(
            modifier = Modifier.align(CenterVertically),
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.align(CenterVertically),
            text = text,
            fontSize = 15.sp,
            color = MaterialTheme.colors.onSurface)
    }
}