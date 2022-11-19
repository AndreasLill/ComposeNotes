package com.andlill.composenotes.app.label.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.R
import com.andlill.composenotes.model.Label
import com.andlill.composenotes.ui.shared.util.clearFocusOnKeyboardDismiss
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditLabel(label: Label, onUpdate: (Label, String) -> Unit, onDelete: () -> Unit) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(label.value)) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .background(color = if (isFocused.value) MaterialTheme.colorScheme.primary.copy(0.1f) else Color.Transparent)) {
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
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        )
        BasicTextField(
            modifier = Modifier
                .align(Alignment.Center)
                .clearFocusOnKeyboardDismiss()
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyUp && event.key == Key.Enter && textFieldValue.text.isNotBlank()) {
                        onUpdate(label, textFieldValue.text)
                        scope.launch {
                            delay(50)
                            keyboardController?.hide()
                        }
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                }
                .onFocusChanged {
                    if (!it.isFocused) {
                        textFieldValue = textFieldValue.copy(text = label.value)
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
                    onUpdate(label, textFieldValue.text)
                    scope.launch {
                        delay(50)
                        keyboardController?.hide()
                    }
                }
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            value = textFieldValue,
            onValueChange = {
                if (it.text.endsWith("\n"))
                    return@BasicTextField
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
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                        )
                    }
                    innerTextField()
                }
            }
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            onClick = {
                when (isFocused.value) {
                    true -> {
                        onUpdate(label, textFieldValue.text)
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
                    tint = if (isFocused.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}