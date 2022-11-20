package com.andlill.composenotes.app.note.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andlill.composenotes.R
import com.andlill.composenotes.ui.shared.text.DialogTitle
import com.andlill.composenotes.ui.theme.NoteColors
import com.andlill.composenotes.utils.ColorUtils.darken

@Composable
fun ColorDialog(state: MutableState<Boolean>, selectedColor: Int, onClick: (Int) -> Unit) {
    DropdownMenu(
        expanded = state.value,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth(),
        onDismissRequest = { state.value = false }) {
        Column(modifier = Modifier.padding(PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp))) {
            DialogTitle(text = stringResource(R.string.note_screen_dialog_color_title))
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                // Default color.
                ColorSelectButton(color = Color.Transparent, icon = Icons.Outlined.Close, selected = selectedColor == 0) {
                    onClick(0)
                }
                // First 8 colors
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NoteColors.subList(0, 8).forEach { color ->
                        val displayColor = when {
                            isSystemInDarkTheme() -> color.darken()
                            else -> color
                        }
                        ColorSelectButton(color = displayColor, selected = selectedColor == color.toArgb()) {
                            onClick(color.toArgb())
                        }
                    }
                }
                // Remaining 8 colors
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NoteColors.subList(8, 16).forEach { color ->
                        val displayColor = when {
                            isSystemInDarkTheme() -> color.darken()
                            else -> color
                        }
                        ColorSelectButton(color = displayColor, selected = selectedColor == color.toArgb()) {
                            onClick(color.toArgb())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSelectButton(color: Color, icon: ImageVector? = null, selected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier.size(32.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary.copy(0.6f) else MaterialTheme.colorScheme.onSurface.copy(0.1f)
        ),
        shape = CircleShape,
        onClick = { onClick() },
    ) {
        icon?.let {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        }
    }
}