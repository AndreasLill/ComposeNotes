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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.model.NoteWrapper

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LabelDialog(state: MutableState<Boolean>, note: NoteWrapper, labels: List<Label>, onClick: (Label) -> Unit) {
    if (state.value) {
        Dialog(onDismissRequest = { state.value = false }) {
            Column(modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)) {
                Text(
                    text = "Labels".uppercase(),
                    letterSpacing = 1.sp,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(labels) { label ->
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
                                        contentDescription = "Label")
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colors.onSurface,
                                        text = label.value)
                                }
                                Checkbox(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    checked = note.labels.contains(label),
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