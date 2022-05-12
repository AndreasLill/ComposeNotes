package com.andlill.keynotes.app.note.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.keynotes.app.shared.clearFocusOnKeyboardDismiss

@Composable
fun NoteBodyTextField(placeholder: String, value: String, focusRequester: FocusRequester, onValueChange: (String) -> Unit) {
    BasicTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .clearFocusOnKeyboardDismiss()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = TextStyle(
            fontSize = 15.sp,
            color = MaterialTheme.colors.onSurface,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        ),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onSurface.copy(0.6f)
                    )
                )
            }
            innerTextField()
        }
    )
}