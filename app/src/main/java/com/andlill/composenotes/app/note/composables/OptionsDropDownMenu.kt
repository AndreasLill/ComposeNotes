package com.andlill.composenotes.app.note.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.R
import com.andlill.composenotes.app.note.NoteOption

@Composable
fun OptionsDropDownMenu(state: MutableState<Boolean>, isCheckBoxesEmpty: Boolean, onClick: (NoteOption) -> Unit) {
    DropdownMenu(
        expanded = state.value,
        modifier = Modifier
            .widthIn(min = 200.dp)
            .background(MaterialTheme.colorScheme.surface),
        onDismissRequest = { state.value = false },
        content = {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.CheckBox,
                        contentDescription = if (isCheckBoxesEmpty) stringResource(R.string.note_screen_menu_checkboxes) else stringResource(R.string.note_screen_menu_checkboxes_hide),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                },
                text = {
                    Text(
                        text = if (isCheckBoxesEmpty) stringResource(R.string.note_screen_menu_checkboxes) else stringResource(R.string.note_screen_menu_checkboxes_hide),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    onClick(NoteOption.Checkboxes)
                }
            )
            Divider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.note_screen_menu_trash),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.note_screen_menu_trash),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    onClick(NoteOption.Delete)
                }
            )
        }
    )
}