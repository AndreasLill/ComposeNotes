package com.andlill.composenotes.app.note.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DisabledByDefault
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.R
import com.andlill.composenotes.app.note.NoteOption

@Composable
fun OptionsDropDownMenu(
    state: MutableState<Boolean>,
    checkBoxes: Boolean,
    onClick: (NoteOption) -> Unit
) {
    DropdownMenu(
        expanded = state.value,
        modifier = Modifier
            .widthIn(min = 200.dp)
            .background(MaterialTheme.colorScheme.surface),
        onDismissRequest = { state.value = false },
        content = {
            NoteMenuItem(
                icon = if (checkBoxes) Icons.Outlined.DisabledByDefault else Icons.Outlined.CheckBoxOutlineBlank,
                text = if (checkBoxes) stringResource(R.string.note_screen_menu_checkboxes_hide) else stringResource(R.string.note_screen_menu_checkboxes_show),
                noteOption = NoteOption.Checkboxes,
                onClick = onClick
            )
            Divider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
            NoteMenuItem(
                icon = Icons.Outlined.Delete,
                text = stringResource(R.string.note_screen_menu_trash),
                noteOption = NoteOption.Delete,
                onClick = onClick
            )
        }
    )
}

@Composable
internal fun NoteMenuItem(
    icon: ImageVector,
    text: String,
    noteOption: NoteOption,
    onClick: (NoteOption) -> Unit
) {
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        onClick = {
            onClick(noteOption)
        }
    )
}