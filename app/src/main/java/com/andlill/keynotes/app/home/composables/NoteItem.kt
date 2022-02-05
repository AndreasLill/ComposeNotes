package com.andlill.keynotes.app.home.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andlill.keynotes.model.Note
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors

@Composable
fun NoteItem(note: Note, callback: () -> Unit) {
    val noteColor = when {
        isSystemInDarkTheme() -> DarkNoteColors[note.color]
        else -> LightNoteColors[note.color]
    }

    OutlinedButton(
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = animateColorAsState(noteColor).value,
            contentColor = MaterialTheme.colors.onSurface,
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp,
        ),
        onClick = { callback() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (note.title.isNotEmpty())
                Text(text = note.title, style = MaterialTheme.typography.h3, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (note.title.isNotEmpty() && note.body.isNotEmpty())
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp))
            if (note.body.isNotEmpty()) {
                Text(text = note.body, style = MaterialTheme.typography.body2, maxLines = 10,overflow = TextOverflow.Ellipsis)
            }
        }
    }
}