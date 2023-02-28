package com.andlill.composenotes.ui.shared.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.composenotes.R
import com.andlill.composenotes.ui.shared.button.DialogButton
import com.andlill.composenotes.ui.shared.text.DialogTitle
import com.andlill.composenotes.ui.shared.util.clearFocusOnKeyboardDismiss
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDialog(state: Boolean, title: String, placeholder: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }

    if (state) {
        LaunchedEffect(Unit) {
            // Keyboard doesn't show unless there is a delay. Jetpack Compose bug?
            delay(100)
            focusRequester.requestFocus()
        }

        Dialog(
            onDismissRequest = {
                onDismiss()
                textFieldValue = textFieldValue.copy(text = "")
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
                        textFieldValue = textFieldValue.copy(text = "")
                    }
            ) {
                Column(
                    modifier = Modifier
                        // Important to align dialog above keyboard.
                        .align(Alignment.Center)
                        .background(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface
                        )
                        .padding(16.dp)
                ) {
                    DialogTitle(text = title)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clearFocusOnKeyboardDismiss()
                            .focusRequester(focusRequester),
                        maxLines = 1,
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = placeholder,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onConfirm(textFieldValue.text)
                                textFieldValue = textFieldValue.copy(text = "")
                            }
                        ),
                        value = textFieldValue,
                        onValueChange = {
                            textFieldValue = it
                        })
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                            DialogButton(
                                text = stringResource(R.string.button_cancel),
                                backgroundColor = MaterialTheme.colorScheme.surface,
                                textColor = MaterialTheme.colorScheme.primary,
                                onClick = {
                                    onDismiss()
                                    textFieldValue = textFieldValue.copy(text = "")
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DialogButton(
                                text = stringResource(R.string.button_ok),
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                textColor = MaterialTheme.colorScheme.onPrimary,
                                onClick = {
                                    onConfirm(textFieldValue.text)
                                    textFieldValue = textFieldValue.copy(text = "")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}