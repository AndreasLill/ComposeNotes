package com.andlill.keynotes.app.note.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors

@Composable
fun ThemeDropDown(state: MutableState<Boolean>, onClick: (Int) -> Unit) {
    DropdownMenu(
        expanded = state.value,
        modifier = Modifier
            .background(MaterialTheme.colors.surface),
        onDismissRequest = { state.value = false }) {
        Column(modifier = Modifier.padding(PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp))) {
            Text(
                text = "Colors",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.alpha(0.6f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())) {
                val colorList = if (isSystemInDarkTheme()) DarkNoteColors else LightNoteColors
                colorList.forEachIndexed { index, value ->
                    ColorSelectButton(color = value) {
                        onClick(index)
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSelectButton(color: Color, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier
            .width(32.dp)
            .height(32.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
        ),
        shape = CircleShape,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
        onClick = { onClick() },
    ) {}
}