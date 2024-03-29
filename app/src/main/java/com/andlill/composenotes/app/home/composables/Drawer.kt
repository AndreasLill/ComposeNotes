package com.andlill.composenotes.app.home.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.BuildConfig
import com.andlill.composenotes.R
import com.andlill.composenotes.model.Label
import com.andlill.composenotes.model.NoteFilter
import com.andlill.composenotes.utils.DialogUtils

@Composable
fun Drawer(
    labels: List<Label>,
    onFilter: (NoteFilter) -> Unit,
    onAddLabel: (String) -> Unit,
    onEditLabels: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val selectedId = rememberSaveable { mutableStateOf(-1) }

    LazyColumn(modifier = Modifier
        .statusBarsPadding()
        .navigationBarsPadding()
        .fillMaxHeight()) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(R.string.app_name),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        item {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = BuildConfig.VERSION_NAME,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            DrawerItem(selectedId = selectedId.value, id = -1, icon = Icons.Outlined.Notes, text = stringResource(R.string.drawer_item_notes), onClick = {
                selectedId.value = -1
                onFilter(NoteFilter(
                    name = context.resources.getString(R.string.drawer_item_notes),
                    type = NoteFilter.Type.AllNotes
                ))
                onClose()
            })
        }
        item {
            DrawerItem(selectedId = selectedId.value, id = -2, icon = Icons.Outlined.Alarm, text = stringResource(R.string.drawer_item_reminders), onClick = {
                selectedId.value = -2
                onFilter(NoteFilter(
                    name = context.resources.getString(R.string.drawer_item_reminders),
                    type = NoteFilter.Type.Reminders
                ))
                onClose()
            })
        }
        item {
            DrawerItem(selectedId = selectedId.value, id = -3, icon = Icons.Outlined.Delete, text = stringResource(R.string.drawer_item_trash), onClick = {
                selectedId.value = -3
                onFilter(NoteFilter(
                    name = context.resources.getString(R.string.drawer_item_trash),
                    type = NoteFilter.Type.Trash
                ))
                onClose()
            })
        }
        item {
            Divider(modifier = Modifier.padding(top = 16.dp, bottom = 0.dp))
        }
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)) {
                TextButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = {
                        DialogUtils.showInputDialog(
                            title = context.resources.getString(R.string.home_screen_dialog_create_label_title),
                            placeholder = context.resources.getString(R.string.home_screen_dialog_create_label_placeholder),
                            onConfirm = onAddLabel
                        )
                    }) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.drawer_item_new_label),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                TextButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = onEditLabels
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.drawer_item_edit),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        items(items = labels, key = { label -> label.id }) { label ->
            DrawerItem(
                selectedId = selectedId.value,
                id = label.id,
                icon = Icons.Outlined.Label,
                text = label.value,
                onClick = {
                    selectedId.value = label.id
                    onFilter(NoteFilter(
                        name = label.value,
                        type = NoteFilter.Type.Label,
                        label = label
                    ))
                    onClose()
                },
            )
        }
    }
}