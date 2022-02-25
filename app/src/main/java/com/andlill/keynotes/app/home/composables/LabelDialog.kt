package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
fun LabelDialog(initialValue: Label?, state: MutableState<Boolean>, onClick: (Label) -> Unit, onDeleteClick: (Label) -> Unit, onDismiss: () -> Unit) {
    var label by remember { mutableStateOf(Label()) }

    LaunchedEffect(state.value) {
        // Set label to initial value when called if any.
        label = initialValue ?: Label()
    }

    if (state.value) {
        Dialog(
            onDismissRequest = {
                onDismiss()
                state.value = false
            }) {
            Column(modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)) {
                Text(
                    text = stringResource(R.string.home_screen_label_dialog_title).uppercase(),
                    letterSpacing = 1.sp,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    singleLine = true,
                    placeholder = {
                        Text(text = stringResource(R.string.home_screen_label_dialog_placeholder), fontSize = 15.sp)
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onClick(label)
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
                    initialValue?.let {
                        // Show delete button if initial value is not empty.
                        OutlinedButton(
                            modifier = Modifier.align(Alignment.CenterStart),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colors.error
                            ),
                            onClick = {
                                onDeleteClick(label)
                                state.value = false
                            }) {
                            ButtonText(text = stringResource(R.string.home_screen_label_dialog_button_delete), color = MaterialTheme.colors.error)
                        }
                    }
                    OutlinedButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                            onClick(label)
                            state.value = false
                        }) {
                        ButtonText(text = stringResource(R.string.home_screen_label_dialog_button_ok))
                    }
                }
            }
        }
    }
}