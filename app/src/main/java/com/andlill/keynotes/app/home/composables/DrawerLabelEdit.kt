package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.keynotes.ui.shared.modifier.focusIndicatorLine
import com.andlill.keynotes.ui.shared.util.clearFocusOnKeyboardDismiss
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawerLabelEdit(initialText: String, onUpdate: (String) -> Unit, onDelete: () -> Unit) {

    val textFieldValue = remember { mutableStateOf(TextFieldValue(initialText)) }
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp), contentAlignment = Alignment.CenterStart) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onDelete() }) {
                Icon(
                    modifier = Modifier.width(56.dp),
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colors.error
                )
            }
            BasicTextField(
                modifier = Modifier
                    .clearFocusOnKeyboardDismiss()
                    .padding(top = 3.dp, bottom = 3.dp, end = 16.dp)
                    .focusIndicatorLine(
                        interactionSource = interactionSource,
                        baseColor = MaterialTheme.colors.onSurface,
                        focusColor = MaterialTheme.colors.primary
                    )
                    .align(Alignment.CenterVertically),
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle.Default.copy(
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 15.sp,
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        scope.launch {
                            delay(50)
                            keyboardController?.hide()
                        }
                    }
                ),
                value = textFieldValue.value,
                onValueChange = {
                    textFieldValue.value = it
                    onUpdate(it.text)
                },
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                        Box (modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart)) {
                            innerTextField()
                        }
                    }
                }
            )
        }
    }
}