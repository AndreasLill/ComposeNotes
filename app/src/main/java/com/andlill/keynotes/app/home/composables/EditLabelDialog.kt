package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.R
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.ui.text.ButtonText

@Composable
fun EditLabelDialog(initialValue: Label, state: MutableState<Boolean>, onConfirm: (Label) -> Unit, onDelete: (Label) -> Unit) {
    var label by remember { mutableStateOf(Label()) }

    LaunchedEffect(state.value) {
        // Set label to initial value when dialog opens.
        label = initialValue
    }

    if (state.value) {
        Dialog(
            onDismissRequest = {
                state.value = false
            }) {
            Box(
                // Important to align dialog above keyboard.
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                Column(
                    modifier = Modifier
                        // Important to align dialog above keyboard.
                        .align(Alignment.Center)
                        .background(MaterialTheme.colors.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_screen_label_dialog_title).uppercase(),
                        letterSpacing = 1.sp,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.home_screen_label_dialog_placeholder),
                                fontSize = 15.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onConfirm(label)
                                state.value = false
                            }
                        ),
                        value = label.value,
                        onValueChange = {
                            label = label.copy(
                                value = it
                            )
                        })
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterStart),
                            onClick = {
                                onDelete(label)
                                state.value = false
                            },
                            content = {
                                Icon(
                                    contentDescription = null,
                                    imageVector = Icons.Outlined.Delete,
                                    tint = MaterialTheme.colors.error,
                                )
                            }
                        )
                        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                            OutlinedButton(
                                onClick = {
                                    state.value = false
                                }) {
                                ButtonText(text = stringResource(R.string.home_screen_label_dialog_button_cancel))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            OutlinedButton(
                                onClick = {
                                    onConfirm(label)
                                    state.value = false
                                }) {
                                ButtonText(text = stringResource(R.string.home_screen_label_dialog_button_ok))
                            }
                        }
                    }
                }
            }
        }
    }
}