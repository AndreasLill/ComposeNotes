package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andlill.keynotes.model.Label
import kotlinx.coroutines.launch

@Composable
fun Drawer(state: DrawerState, labels: List<Label>, onFilterDeleted: (Boolean) -> Unit, onAddLabel: (Label) -> Unit, onDeleteLabel: (Label) -> Unit) {
    val selectedLabel = remember { mutableStateOf<Label?>(null) }
    val labelDialogState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    DrawerHeader()
    Column(modifier = Modifier
        .background(MaterialTheme.colors.surface)
        .fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            DrawerItem(icon = Icons.Outlined.Home, text = "Notes", onClick = {
                scope.launch {
                    onFilterDeleted(false)
                    state.close()
                }
            })
            DrawerItem(icon = Icons.Outlined.Delete, text = "Deleted", onClick = {
                scope.launch {
                    onFilterDeleted(true)
                    state.close()
                }
            })
        }
        Divider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
        Column {
            DrawerItem(icon = Icons.Outlined.Add, text = "Add Label", color = MaterialTheme.colors.onSurface.copy(0.6f), onClick = {
                labelDialogState.value = true
            })
        }
        LazyColumn {
            items(labels) { label ->
                DrawerItem(icon = Icons.Outlined.Label, text = label.value, showEditButton = true,
                    onClick = {
                        scope.launch {
                            state.close()
                        }
                    },
                    onEditClick = {
                        selectedLabel.value = label
                        labelDialogState.value = true
                    }
                )
            }
        }
        LabelDialog(
            initialValue = selectedLabel.value,
            state = labelDialogState,
            onClick = {
                onAddLabel(it)
                selectedLabel.value = null
            },
            onDeleteClick = {
                // Delete the selected label and set back to null.
                selectedLabel.value?.let(onDeleteLabel)
                selectedLabel.value = null
            },
            onDismiss = {
                selectedLabel.value = null
            }
        )
    }
}