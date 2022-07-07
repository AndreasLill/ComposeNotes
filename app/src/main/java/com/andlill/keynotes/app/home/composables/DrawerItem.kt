package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerItem(selectedItem: Int, id: Int, icon: ImageVector, alpha: Float = 1f, text: String, showEditButton: Boolean = false, onClick: () -> Unit = {}, onEditClick: () -> Unit = {}) {

    // Color depends on if this item is selected or not.
    val backgroundColor = if (selectedItem == id) MaterialTheme.colors.primary.copy(0.1f) else Color.Transparent
    val contentColor = if (selectedItem == id) MaterialTheme.colors.primary.copy(alpha) else MaterialTheme.colors.onSurface.copy(alpha)

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
        Box(modifier = Modifier.padding(start = 16.dp)) {
            Row(modifier = Modifier.align(CenterStart)) {
                Icon(
                    modifier = Modifier.align(CenterVertically),
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    modifier = Modifier.align(CenterVertically),
                    text = text,
                    fontSize = 15.sp,
                    color = contentColor)
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
                        tint = contentColor.copy(0.32f))
                }
            }
        }
    }
}