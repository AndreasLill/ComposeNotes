package com.andlill.composenotes.app.note.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.R
import com.andlill.composenotes.ui.shared.util.clearFocusOnKeyboardDismiss

@Composable
fun NoteBodyTextField(state: TextFieldValue, readOnly: Boolean, focusRequester: FocusRequester, onValueChange: (TextFieldValue) -> Unit) {
    BasicTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .clearFocusOnKeyboardDismiss()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 48.dp)
            .fillMaxWidth(),
        readOnly = readOnly,
        value = state,
        onValueChange = onValueChange,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = TextStyle(
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        ),
        decorationBox = { innerTextField ->
            if (state.text.isEmpty()) {
                Text(
                    text = stringResource(R.string.note_screen_placeholder_body),
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                )
            }
            innerTextField()
        }
    )
}