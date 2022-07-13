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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andlill.keynotes.app.home.HomeViewModel
import com.andlill.keynotes.model.Label
import kotlinx.coroutines.launch

@Composable
fun Drawer(state: DrawerState, viewModel: HomeViewModel) {
    val editLabelDialogState = remember { mutableStateOf(false) }
    val createLabelDialogState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .background(MaterialTheme.colors.surface)
        .statusBarsPadding()
        .fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            DrawerItem(viewModel.drawerSelectedItem, id = 0, icon = Icons.Outlined.Home, text = "Notes", onClick = {
                scope.launch { state.close() }
                viewModel.drawerSelectedItem = 0
                viewModel.drawerSelectedItemName = "Notes"
                viewModel.onFilterDeleted(false)
            })
            DrawerItem(viewModel.drawerSelectedItem, id = 1, icon = Icons.Outlined.Delete, text = "Deleted", onClick = {
                scope.launch { state.close() }
                viewModel.drawerSelectedItem = 1
                viewModel.drawerSelectedItemName = "Deleted"
                viewModel.onFilterDeleted(true)
            })
        }
        Divider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
        Column {
            DrawerItem(viewModel.drawerSelectedItem, id = 2, icon = Icons.Outlined.Add, alpha = 0.32f, text = "New Label", onClick = {
                createLabelDialogState.value = true
            })
        }
        CreateLabelDialog(
            state = createLabelDialogState,
            onConfirm = {
                viewModel.onAddLabel(it)
            },
        )
        LazyColumn {
            itemsIndexed(viewModel.labels) { index, label ->
                // Index minus static drawer items above.
                DrawerItem(viewModel.drawerSelectedItem, id = index + 3, icon = Icons.Outlined.Label, text = label.value, showEditButton = true,
                    onClick = {
                        scope.launch { state.close() }
                        viewModel.drawerSelectedItem = index + 3
                        viewModel.drawerSelectedItemName = label.value
                        viewModel.onFilterLabel(label)
                    },
                    onEditClick = {
                        viewModel.drawerSelectedLabel = label
                        editLabelDialogState.value = true
                    }
                )
            }
        }
        EditLabelDialog(
            label = viewModel.drawerSelectedLabel,
            state = editLabelDialogState,
            onConfirm = {
                viewModel.onUpdateLabel(it)
                viewModel.drawerSelectedLabel = Label()
            },
            onDelete = {
                // Set selected item back to zero if this deleted label was selected.
                if (it.value == viewModel.drawerSelectedItemName) {
                    viewModel.drawerSelectedItem = 0
                    viewModel.drawerSelectedItemName = "Notes"
                    viewModel.onFilterLabel(null)
                }
                viewModel.onDeleteLabel(it)
                viewModel.drawerSelectedLabel = Label()
            },
            onDismiss = {
                viewModel.drawerSelectedLabel = Label()
            }
        )
    }
}