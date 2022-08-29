package com.andlill.keynotes.app.note.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.keynotes.ui.shared.util.clearFocusOnKeyboardDismiss

@Composable
fun NoteTitleTextField(placeholder: String, state: TextFieldValue, readOnly: Boolean, onValueChange: (TextFieldValue) -> Unit) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        modifier = Modifier
            .clearFocusOnKeyboardDismiss()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        readOnly = readOnly,
        value = state,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Next)
            }
        ),
        decorationBox = { innerTextField ->
            if (state.text.isEmpty()) {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                )
            }
            innerTextField()
        }
    )
}