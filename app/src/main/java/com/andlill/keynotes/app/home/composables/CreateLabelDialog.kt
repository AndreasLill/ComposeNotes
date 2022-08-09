package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.R
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.ui.shared.button.DialogButton
import com.andlill.keynotes.ui.shared.text.DialogTitle
import com.andlill.keynotes.ui.shared.util.clearFocusOnKeyboardDismiss
import kotlinx.coroutines.delay

@Composable
fun CreateLabelDialog(state: MutableState<Boolean>, onConfirm: (Label) -> Unit) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val inputService = LocalTextInputService.current

    if (state.value) {

        LaunchedEffect(Unit) {
            // Keyboard doesn't show unless there is a delay. Jetpack Compose bug?
            delay(50)
            focusRequester.requestFocus()
            inputService?.showSoftwareKeyboard()
        }

        Dialog(
            onDismissRequest = {
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
                    DialogTitle(text = stringResource(R.string.home_screen_dialog_create_label_title))
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
                                text = stringResource(R.string.home_screen_dialog_create_label_placeholder),
                                fontSize = 15.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onConfirm(Label(value = text))
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
                        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                            DialogButton(
                                text = stringResource(R.string.button_cancel),
                                backgroundColor = Color.Transparent,
                                textColor = MaterialTheme.colors.primary,
                                onClick = {
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
                                    onConfirm(Label(value = text))
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