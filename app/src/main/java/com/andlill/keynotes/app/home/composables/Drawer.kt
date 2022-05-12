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
    val editLabelDialogState = remember { mutableStateOf(false) }
    val createLabelDialogState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .background(MaterialTheme.colors.surface)
        .statusBarsPadding()
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
            DrawerItem(icon = Icons.Outlined.Add, text = "New Label", color = MaterialTheme.colors.onSurface.copy(0.5f), onClick = {
                createLabelDialogState.value = true
            })
            CreateLabelDialog(
                state = createLabelDialogState,
                onConfirm = {
                    onAddLabel(it)
                },
            )
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
                        editLabelDialogState.value = true
                    }
                )
                EditLabelDialog(
                    initialValue = label,
                    state = editLabelDialogState,
                    onConfirm = {
                        onAddLabel(it)
                    },
                    onDelete = {
                        onDeleteLabel(it)
                    },
                )
            }
        }
    }
}