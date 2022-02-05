package com.andlill.keynotes.app.note.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.keynotes.R
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors

@Composable
fun ThemeDropDown(state: MutableState<Boolean>, onClick: (Int) -> Unit) {
    DropdownMenu(
        expanded = state.value,
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .fillMaxWidth(),
        onDismissRequest = { state.value = false }) {
        Column(modifier = Modifier.padding(PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp))) {
            val colorList = if (isSystemInDarkTheme()) DarkNoteColors else LightNoteColors
            Text(
                text = stringResource(R.string.note_screen_theme_dropdown_title).uppercase(),
                letterSpacing = 1.sp,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                // First default color.
                ColorSelectButton(colorList[0], Icons.Outlined.Close) {
                    onClick(0)
                }
                // First 8 colors
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorList.subList(1, 9).forEach { value ->
                        ColorSelectButton(color = value) {
                            onClick(colorList.indexOf(value))
                        }
                    }
                }
                // Remaining 8 colors
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorList.subList(9, 17).forEach { value ->
                        ColorSelectButton(color = value) {
                            onClick(colorList.indexOf(value))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSelectButton(color: Color, icon: ImageVector? = null, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier.size(32.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
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