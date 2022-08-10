package com.andlill.keynotes.app.note.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.R
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.ui.shared.text.DialogTitle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LabelDialog(state: MutableState<Boolean>, noteLabels: List<Label>, labels: List<Label>, onClick: (Label) -> Unit) {
    if (state.value) {
        Dialog(onDismissRequest = { state.value = false }) {
            Column(modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)) {
                DialogTitle(text = stringResource(R.string.note_screen_dialog_labels_title))
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(items = labels, key = { it.id }) { label ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            onClick = {
                                onClick(label)
                            }) {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 8.dp)) {
                                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                                    Icon(
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        imageVector = Icons.Outlined.Label,
                                        tint = MaterialTheme.colors.onSurface,
                                        contentDescription = null)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colors.onSurface,
                                        text = label.value)
                                }
                                Checkbox(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    checked = noteLabels.contains(label),
                                    onCheckedChange = {
                                        onClick(label)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}