package com.andlill.composenotes.app.home.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerItem(selectedId: Int, id: Int, icon: ImageVector, text: String, onClick: () -> Unit) {

    // Color depends on if this item is selected or not.
    val backgroundColor = if (selectedId == id) MaterialTheme.colorScheme.primary.copy(0.1f) else Color.Transparent
    val contentColor = if (selectedId == id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .padding(end = 16.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        onClick = {
            onClick()
        }
    ) {
        Row(modifier = Modifier.padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                color = contentColor)
        }
    }
}