package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.keynotes.R
import com.andlill.keynotes.app.home.HomeViewModel
import com.andlill.keynotes.model.Label
import kotlinx.coroutines.launch

@Composable
fun Drawer(state: DrawerState, viewModel: HomeViewModel) {

    val scope = rememberCoroutineScope()
    val createLabelDialogState = remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .background(MaterialTheme.colors.surface)
        .statusBarsPadding()
        .fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(R.string.app_name),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            DrawerItem(viewModel.drawerSelectedItem, id = 0, icon = Icons.Outlined.Home, text = viewModel.drawerItemTitleNotes, onClick = {
                scope.launch { state.close() }
                viewModel.drawerSelectedItem = 0
                viewModel.drawerSelectedItemName = viewModel.drawerItemTitleNotes
                viewModel.onFilterDeleted(false)
                viewModel.labelEditMode = false
            })
            DrawerItem(viewModel.drawerSelectedItem, id = 1, icon = Icons.Outlined.Delete, text = viewModel.drawerItemTitleTrash, onClick = {
                scope.launch { state.close() }
                viewModel.drawerSelectedItem = 1
                viewModel.drawerSelectedItemName = viewModel.drawerItemTitleTrash
                viewModel.onFilterDeleted(true)
                viewModel.labelEditMode = false
            })
        }
        Divider(modifier = Modifier.padding(top = 16.dp, bottom = 0.dp))
        Box(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp)) {
            DrawerLabelButton(
                modifier = Modifier.align(Alignment.CenterStart),
                icon = Icons.Outlined.Add,
                text = "NEW LABEL",
                onClick = {
                    createLabelDialogState.value = true
                }
            )
            DrawerLabelButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                icon = if (!viewModel.labelEditMode) Icons.Outlined.Edit else Icons.Outlined.Check,
                text = if (!viewModel.labelEditMode) "EDIT" else "DONE",
                onClick = {
                    viewModel.labelEditMode = !viewModel.labelEditMode
                }
            )
        }
        CreateLabelDialog(
            state = createLabelDialogState,
            onConfirm = {
                viewModel.onAddLabel(it)
            },
        )
        LazyColumn {
            itemsIndexed(viewModel.labels) { index, label ->
                // ID = index plus static drawer items above (Notes + Trash).
                DrawerLabel(viewModel.drawerSelectedItem, id = index + 2, text = label.value, editMode = viewModel.labelEditMode,
                    onClick = {
                        scope.launch { state.close() }
                        viewModel.drawerSelectedItem = index + 2
                        viewModel.drawerSelectedItemName = label.value
                        viewModel.onFilterLabel(label)
                    },
                    onUpdate = {
                        val updatedLabel = label.copy(value = it)
                        // Update selected label and name if this label was updated.
                        if (label.value == viewModel.drawerSelectedItemName) {
                            viewModel.drawerSelectedItemName = it
                            viewModel.drawerSelectedLabel = updatedLabel
                        }
                        viewModel.onUpdateLabel(updatedLabel)
                        viewModel.onFilterLabel(updatedLabel)
                    },
                    onDelete = {
                        // Set selected item back to zero if this deleted label was selected.
                        if (label.value == viewModel.drawerSelectedItemName) {
                            viewModel.drawerSelectedItem = 0
                            viewModel.drawerSelectedItemName = viewModel.drawerItemTitleNotes
                            viewModel.onFilterLabel(null)
                        }
                        viewModel.onDeleteLabel(label)
                        viewModel.drawerSelectedLabel = Label()
                    }
                )
            }
        }
    }
}