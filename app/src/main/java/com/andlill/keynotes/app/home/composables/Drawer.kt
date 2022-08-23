package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    onSelectItem: (String) -> Unit,
    onAddLabel: (Label) -> Unit,
    onFilterTrash: (Boolean) -> Unit,
    onFilterLabel: (Label?) -> Unit,
    onClose: () -> Unit,
    onEditLabels: () -> Unit
) {
    val context = LocalContext.current
    val titleNotes = remember { context.resources.getString(R.string.drawer_item_notes) }
    val titleTrash = remember { context.resources.getString(R.string.drawer_item_trash) }
    val selectedId = rememberSaveable { mutableStateOf(-1) }

    Column(modifier = Modifier
        .background(MaterialTheme.colors.surface)
        .statusBarsPadding()
        .navigationBarsPadding()
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
                onClose()
            })
            DrawerItem(selectedId = selectedId.value, id = -2, icon = Icons.Outlined.Delete, text = titleTrash, onClick = {
                selectedId.value = -2
                onSelectItem(titleTrash)
                onFilterTrash(true)
                onClose()
            })
        }
        Divider(modifier = Modifier.padding(top = 16.dp, bottom = 0.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)) {
            TextButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = {
                    DialogUtils.showInputDialog(
                        title = context.resources.getString(R.string.home_screen_dialog_create_label_title),
                        placeholder = context.resources.getString(R.string.home_screen_dialog_create_label_placeholder),
                        onConfirm = {
                            onAddLabel(Label(value = it))
                        }
                    )
                }) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.drawer_item_new_label).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            }
            TextButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = onEditLabels
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.drawer_item_edit).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            }
        }
        LazyColumn {
            items(items = labels, key = { label -> label.id }) { label ->
                DrawerItem(
                    selectedId = selectedId.value,
                    id = label.id,
                    icon = Icons.Outlined.Label,
                    text = label.value,
                    onClick = {
                        selectedId.value = label.id
                        onSelectItem(label.value)
                        onFilterLabel(label)
                        onClose()
                    },
                )
            }
        }
    }
}