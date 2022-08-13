package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerLabel(selectedItem: Int, id: Int, text: String, editMode: Boolean, onClick: () -> Unit, onUpdate: (String) -> Unit, onDelete: () -> Unit) {

    // Color depends on if this item is selected or not.
    val backgroundColor = if (selectedItem == id && !editMode) MaterialTheme.colors.primary.copy(0.1f) else Color.Transparent
    val contentColor = if (selectedItem == id && !editMode) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface

    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .padding(end = 16.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        onClick = {
            if (!editMode) {
                onClick()
            }
        }
    ) {
        Box(modifier = Modifier.padding(start = 16.dp)) {
            if (!editMode) {
                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        imageVector = Icons.Outlined.Label,
                        contentDescription = null,
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = text,
                        fontSize = 15.sp,
                        color = contentColor
                    )
                }
            }
            else {
                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        imageVector = Icons.Outlined.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    BasicTextField(
                        modifier = Modifier
                            .padding(top = 3.dp, bottom = 3.dp, end = 48.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(
                                color = MaterialTheme.colors.onSurface.copy(0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .align(Alignment.CenterVertically),
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                        value = text,
                        onValueChange = { onUpdate(it) },
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxSize()) {
                                Box (modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 8.dp)) {
                                    innerTextField()
                                }
                            }
                        }
                    )
                }
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { onDelete() }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}