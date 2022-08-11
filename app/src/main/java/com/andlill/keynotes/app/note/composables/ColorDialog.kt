package com.andlill.keynotes.app.note.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andlill.keynotes.R
import com.andlill.keynotes.ui.shared.text.DialogTitle
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors

@Composable
fun ThemeDropDown(state: MutableState<Boolean>, selectedColor: Int, onClick: (Int) -> Unit) {
    val colorList = if (isSystemInDarkTheme()) DarkNoteColors else LightNoteColors
    DropdownMenu(
        expanded = state.value,
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .fillMaxWidth(),
        onDismissRequest = { state.value = false }) {
        Column(modifier = Modifier.padding(PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp))) {
            DialogTitle(text = stringResource(R.string.note_screen_dialog_theme_title))
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                // First default color.
                ColorSelectButton(color = colorList[0], icon = Icons.Outlined.Close, selected = selectedColor == 0) {
                    onClick(0)
                }
                // First 8 colors
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorList.subList(1, 9).forEach { color ->
                        val index = colorList.indexOf(color)
                        ColorSelectButton(color = color, selected = selectedColor == index) {
                            onClick(index)
                        }
                    }
                }
                // Remaining 8 colors
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorList.subList(9, 17).forEach { color ->
                        val index = colorList.indexOf(color)
                        ColorSelectButton(color = color, selected = selectedColor == index) {
                            onClick(index)
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
            backgroundColor = color,
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colors.primary.copy(0.6f) else MaterialTheme.colors.onSurface.copy(0.1f)
        ),
        shape = CircleShape,
        onClick = { onClick() },
    ) {
        icon?.let {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface.copy(0.6f))
        }
    }
}