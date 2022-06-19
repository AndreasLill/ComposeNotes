package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andlill.keynotes.model.Label
import kotlinx.coroutines.launch

@Composable
fun Drawer(state: DrawerState, labels: List<Label>, onFilterDeleted: (Boolean) -> Unit, onFilterLabel: (Label) -> Unit, onAddLabel: (Label) -> Unit, onDeleteLabel: (Label) -> Unit) {
    val editLabelDialogState = remember { mutableStateOf(false) }
    val createLabelDialogState = remember { mutableStateOf(false) }
    val selectedItem = rememberSaveable  { mutableStateOf(0) }
    val selectedLabel = remember  { mutableStateOf(Label()) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .background(MaterialTheme.colors.surface)
        .statusBarsPadding()
        .fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            DrawerItem(selectedItem, id = 0, icon = Icons.Outlined.Home, text = "Notes", onClick = {
                scope.launch { state.close() }
                selectedItem.value = 0
                onFilterDeleted(false)
            })
            DrawerItem(selectedItem, id = 1, icon = Icons.Outlined.Delete, text = "Deleted", onClick = {
                scope.launch { state.close() }
                selectedItem.value = 1
                onFilterDeleted(true)
            })
        }
        Divider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
        Column {
            DrawerItem(selectedItem, id = 2, icon = Icons.Outlined.Add, alpha = 0.32f, text = "New Label", onClick = {
                createLabelDialogState.value = true
            })
        }
        CreateLabelDialog(
            state = createLabelDialogState,
            onConfirm = {
                onAddLabel(it)
            },
        )
        LazyColumn {
            itemsIndexed(labels) { index, label ->
                // Index minus static drawer items above.
                DrawerItem(selectedItem, id = index + 3, icon = Icons.Outlined.Label, text = label.value, showEditButton = true,
                    onClick = {
                        scope.launch { state.close() }
                        selectedItem.value = index + 3
                        onFilterLabel(label)
                    },
                    onEditClick = {
                        selectedLabel.value = label
                        editLabelDialogState.value = true
                    }
                )
            }
        }
        EditLabelDialog(
            label = selectedLabel.value,
            state = editLabelDialogState,
            onConfirm = {
                onAddLabel(it)
                selectedLabel.value = Label()
            },
            onDelete = {
                onDeleteLabel(it)
                selectedItem.value = 0
                onFilterDeleted(false)
                selectedLabel.value = Label()
            },
            onDismiss = {
                selectedLabel.value = Label()
            }
        )
    }
}