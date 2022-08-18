package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.keynotes.R
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.utils.DialogUtils

@Composable
fun Drawer(
    labels: List<Label>,
    labelEditMode: Boolean,
    onLabelEditMode: (Boolean) -> Unit,
    onSelectItem: (String) -> Unit,
    onAddLabel: (Label) -> Unit,
    onUpdateLabel: (Label) -> Unit,
    onDeleteLabel: (Label) -> Unit,
    onFilterTrash: (Boolean) -> Unit,
    onFilterLabel: (Int?) -> Unit,
    onClose: () -> Unit
) {

    val context = LocalContext.current
    val createLabelDialogState = remember { mutableStateOf(false) }
    val titleNotes = remember { context.resources.getString(R.string.drawer_item_notes) }
    val titleTrash = remember { context.resources.getString(R.string.drawer_item_trash) }
    val selectedId = rememberSaveable { mutableStateOf(-1) }

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
            DrawerItem(selectedId = selectedId.value, id = -1, icon = Icons.Outlined.Home, text = titleNotes, onClick = {
                selectedId.value = -1
                onSelectItem(titleNotes)
                onFilterTrash(false)
                onLabelEditMode(false)
                onClose()
            })
            DrawerItem(selectedId = selectedId.value, id = -2, icon = Icons.Outlined.Delete, text = titleTrash, onClick = {
                selectedId.value = -2
                onSelectItem(titleTrash)
                onFilterTrash(true)
                onLabelEditMode(false)
                onClose()
            })
        }
        Divider(modifier = Modifier.padding(top = 16.dp, bottom = 0.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)) {
            DrawerLabelButton(
                modifier = Modifier.align(Alignment.CenterStart),
                icon = Icons.Outlined.Add,
                text = stringResource(R.string.drawer_item_new_label).uppercase(),
                onClick = {
                    createLabelDialogState.value = true
                }
            )
            DrawerLabelButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                icon = if (!labelEditMode) Icons.Outlined.Edit else Icons.Outlined.ArrowBack,
                text = if (!labelEditMode) stringResource(R.string.drawer_item_edit).uppercase() else stringResource(R.string.drawer_item_done).uppercase(),
                onClick = {
                    if (labels.isNotEmpty()) {
                        onLabelEditMode(!labelEditMode)
                    }
                }
            )
        }
        CreateLabelDialog(
            state = createLabelDialogState,
            onConfirm = {
                onAddLabel(it)
            },
        )
        LazyColumn {
            items(items = labels, key = { label -> label.id }) { label ->
                if (labelEditMode) {
                    DrawerLabelEdit(
                        initialText = label.value,
                        onUpdate = {
                            val updatedLabel = label.copy(value = it)
                            // Update selected label and name if this label was updated.
                            if (selectedId.value == label.id) {
                                onSelectItem(it)
                                onFilterLabel(label.id)
                            }
                            onUpdateLabel(updatedLabel)
                        },
                        onDelete = {
                            DialogUtils.showConfirmDialog(String.format(context.resources.getString(R.string.home_screen_dialog_confirm_label_delete), label.value)) {
                                // Set selected item back to zero if this deleted label was selected.
                                if (selectedId.value == label.id) {
                                    selectedId.value = -1
                                    onSelectItem(titleNotes)
                                    onFilterLabel(null)
                                }
                                // Close label edit mode if last one was deleted.
                                if (labels.size == 1) {
                                    onLabelEditMode(false)
                                }
                                onDeleteLabel(label)
                            }
                        }
                    )
                }
                else {
                    DrawerItem(
                        selectedId = selectedId.value,
                        id = label.id,
                        icon = Icons.Outlined.Label,
                        text = label.value,
                        onClick = {
                            selectedId.value = label.id
                            onSelectItem(label.value)
                            onFilterLabel(label.id)
                            onClose()
                        },
                    )
                }
            }
        }
    }
}