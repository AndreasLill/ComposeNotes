package com.andlill.composenotes.app.note.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.R
import com.andlill.composenotes.model.NoteCheckBox
import com.andlill.composenotes.ui.shared.util.clearFocusOnKeyboardDismiss

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NoteCheckBoxItem(
    modifier: Modifier,
    enabled: Boolean,
    focusRequester: FocusRequester? = null,
    checkBox: NoteCheckBox,
    contentColor: Color,
    onUpdate: (Int, Boolean, String) -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit
) {
    // Local checkbox and text field value state.
    var textFieldValue by remember { mutableStateOf(TextFieldValue(checkBox.text)) }
    var checkBoxValue by remember { mutableStateOf(checkBox.checked) }

    /* Disabled
    // Send update with a delay of 1500ms when user stops typing.
    LaunchedEffect(textFieldValue.text) {
        delay(1500)
        onUpdate(checkBox, checkBoxValue, textFieldValue.text)
    }
     */

    Row(modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Transparent,
                checkmarkColor = contentColor
            ),
            enabled = enabled,
            checked = checkBoxValue,
            onCheckedChange = {
                checkBoxValue = it
                onUpdate(checkBox.id, checkBoxValue, textFieldValue.text)
            }
        )
        BasicTextField(
            modifier = Modifier
                .fillMaxSize()
                .clearFocusOnKeyboardDismiss()
                .then(
                    if (focusRequester != null)
                        Modifier.focusRequester(focusRequester)
                    else
                        Modifier
                )
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyUp && event.key == Key.Backspace && textFieldValue.text.isEmpty()) {
                        onDelete()
                        return@onKeyEvent true
                    }
                    if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                        onDone()
                        return@onKeyEvent true
                    }
                    false
                }
                .onFocusChanged {
                    if (it.isFocused) {
                        textFieldValue =
                            textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
                    }
                },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDone()
                },
            ),
            readOnly = !enabled,
            value = textFieldValue,
            onValueChange = {
                // Ignore new line endings.
                if (it.text.endsWith("\n"))
                    return@BasicTextField
                textFieldValue = it
                onUpdate(checkBox.id, checkBoxValue, textFieldValue.text)
            },
            textStyle = TextStyle(
                color = contentColor,
                fontSize = 15.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart) {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                            text = stringResource(R.string.note_screen_placeholder_checkbox),
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}