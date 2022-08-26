package com.andlill.keynotes.app.label.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.keynotes.R
import com.andlill.keynotes.ui.shared.util.clearFocusOnKeyboardDismiss

@Composable
fun CreateLabel(onCreate: (String) -> Unit) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = Modifier.fillMaxSize()) {
        BasicTextField(
            modifier = Modifier
                .align(Alignment.Center)
                .clearFocusOnKeyboardDismiss()
                .onFocusChanged {
                    if (!it.isFocused) {
                        textFieldValue = textFieldValue.copy(text = "")
                    }
                }
                .fillMaxWidth(),
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onCreate(textFieldValue.text)
                    textFieldValue = textFieldValue.copy(text = "")
                }
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colors.onSurface,
                fontSize = 15.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
            },
            decorationBox = { innerTextField ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, bottom = 4.dp, end = 56.dp),
                    contentAlignment = Alignment.CenterStart) {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.label_screen_create_label_placeholder),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colors.onSurface.copy(0.6f)
                        )
                    }
                    innerTextField()
                }
            }
        )
        if (textFieldValue.text.isNotEmpty()) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = {
                    onCreate(textFieldValue.text)
                    textFieldValue = textFieldValue.copy(text = "")
                },
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }
            )
        }
    }
}