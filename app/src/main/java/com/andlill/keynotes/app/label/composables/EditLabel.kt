package com.andlill.keynotes.app.label.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.keynotes.R
import com.andlill.keynotes.ui.shared.modifier.focusIndicatorLine
import com.andlill.keynotes.ui.shared.util.clearFocusOnKeyboardDismiss
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditLabel(initialText: String, onUpdate: (String) -> Unit, onDelete: () -> Unit) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(initialText)) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .focusIndicatorLine(
            interactionSource = interactionSource,
            baseColor = Color.Transparent,
            focusColor = MaterialTheme.colors.primary,
            focusWidth = 1.dp
        )) {
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp),
            enabled = false,
            onClick = {},
            content = {
                Icon(
                    imageVector = Icons.Outlined.Label,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface
                )
            }
        )
        BasicTextField(
            modifier = Modifier
                .align(Alignment.Center)
                .clearFocusOnKeyboardDismiss()
                .onFocusChanged {
                    if (!it.isFocused) {
                        textFieldValue = textFieldValue.copy(text = initialText)
                    }
                }
                .fillMaxWidth()
                .padding(start = 56.dp, end = 56.dp),
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onUpdate(textFieldValue.text)
                    scope.launch {
                        delay(50)
                        keyboardController?.hide()
                    }
                }
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colors.onSurface,
                fontSize = 15.sp
            ),
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
            },
            decorationBox = { innerTextField ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, bottom = 4.dp),
                    contentAlignment = Alignment.CenterStart) {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.label_screen_edit_label_placeholder),
                            fontSize = 15.sp,
                            color = MaterialTheme.colors.onSurface.copy(0.6f)
                        )
                    }
                    innerTextField()
                }
            }
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            onClick = {
                when (isFocused.value) {
                    true -> {
                        onUpdate(textFieldValue.text)
                        scope.launch {
                            delay(50)
                            keyboardController?.hide()
                        }
                    }
                    false -> {
                        onDelete()
                    }
                }
            },
            content = {
                Icon(
                    imageVector = if (isFocused.value) Icons.Outlined.Check else Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = if (isFocused.value) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                )
            }
        )
    }
}