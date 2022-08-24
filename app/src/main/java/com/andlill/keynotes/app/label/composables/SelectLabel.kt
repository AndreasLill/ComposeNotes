package com.andlill.keynotes.app.label.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectLabel(text: String, checked: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onClick) {
        Box {
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 4.dp),
                enabled = false,
                onClick = {},
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            )
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(start = 56.dp, end = 56.dp),
                textAlign = TextAlign.Start,
                text = text,
                fontSize = 15.sp,
                color = MaterialTheme.colors.onSurface
            )
            Checkbox(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp),
                colors = CheckboxDefaults.colors(
                    uncheckedColor = MaterialTheme.colors.onSurface
                ),
                checked = checked,
                onCheckedChange = {
                    onClick()
                }
            )
        }
    }
}