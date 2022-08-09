package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.R
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.ui.shared.button.DialogButton
import com.andlill.keynotes.ui.shared.text.DialogTitle

@Composable
fun EditLabelDialog(label: Label, state: MutableState<Boolean>, onConfirm: (Label) -> Unit, onDelete: (Label) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }

    LaunchedEffect(state.value) {
        // Set text value when dialog opens.
        text = label.value
    }

    if (state.value) {
        Dialog(
            onDismissRequest = {
                onDismiss()
                text = ""
                state.value = false
            }) {
            Box(
                // Important to align dialog above keyboard. Jetpack Compose bug?
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onDismiss()
                        text = ""
                        state.value = false
                    }
            ) {
                Column(
                    modifier = Modifier
                        // Important to align dialog above keyboard.
                        .align(Alignment.Center)
                        .background(MaterialTheme.colors.surface)
                        .padding(16.dp)
                ) {
                    DialogTitle(text = stringResource(R.string.home_screen_dialog_edit_label_title))
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onConfirm(label.copy(value = text))
                                text = ""
                                state.value = false
                            }
                        ),
                        value = text,
                        onValueChange = {
                            text = it
                        })
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            modifier = Modifier.align(Alignment.CenterStart),
                            onClick = {
                                onDelete(label)
                                text = ""
                                state.value = false
                            }) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                contentDescription = null,
                                imageVector = Icons.Outlined.Delete,
                                tint = MaterialTheme.colors.error,
                            )
                        }
                        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                            DialogButton(
                                text = stringResource(R.string.button_cancel),
                                backgroundColor = Color.Transparent,
                                textColor = MaterialTheme.colors.primary,
                                onClick = {
                                    onDismiss()
                                    text = ""
                                    state.value = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DialogButton(
                                text = stringResource(R.string.button_ok),
                                backgroundColor = MaterialTheme.colors.primary,
                                textColor = MaterialTheme.colors.onPrimary,
                                onClick = {
                                    onConfirm(label.copy(value = text))
                                    text = ""
                                    state.value = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}