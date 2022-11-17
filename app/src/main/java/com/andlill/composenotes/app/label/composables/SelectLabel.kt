package com.andlill.composenotes.app.label.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.model.Label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLabel(label: Label, checked: Boolean, onClick: (Label) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = {
            onClick(label)
        }) {
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
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(start = 56.dp, end = 56.dp),
                textAlign = TextAlign.Start,
                text = label.value,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Checkbox(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                colors = CheckboxDefaults.colors(
                    uncheckedColor = MaterialTheme.colorScheme.onSurface
                ),
                checked = checked,
                onCheckedChange = {
                    onClick(label)
                }
            )
        }
    }
}