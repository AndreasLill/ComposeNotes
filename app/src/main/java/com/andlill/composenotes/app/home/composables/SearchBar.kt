package com.andlill.composenotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.R
import com.andlill.composenotes.ui.shared.util.clearFocusOnKeyboardDismiss

@Composable
fun SearchBar(query: String, placeholder: String, onValueChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        modifier = Modifier
            .clearFocusOnKeyboardDismiss()
            .padding(end = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .height(24.dp),
        value = query,
        onValueChange = onValueChange,
        maxLines = 1,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        decorationBox = { innerTextField ->
            Box {
                if (query.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart),
                        text = String.format(stringResource(R.string.home_screen_placeholder_search), placeholder),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                        )
                    )
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterEnd),
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                }
                if (query.isNotEmpty()) {
                    IconButton(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterEnd),
                        onClick = {
                            onValueChange("")
                            focusManager.clearFocus()
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    innerTextField()
                }
            }
        }
    )
}