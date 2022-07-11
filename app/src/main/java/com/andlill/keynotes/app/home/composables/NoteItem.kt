package com.andlill.keynotes.app.home.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.model.Note
import com.andlill.keynotes.ui.label.NoteLabel
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors

@Composable
fun NoteItem(note: Note, labels: List<Label>, callback: () -> Unit) {
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
            if (note.title.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.padding(end = 24.dp),
                        text = note.title,
                        style = MaterialTheme.typography.h3,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (note.pinned) {
                        Icon(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.CenterEnd),
                            imageVector = Icons.Outlined.PushPin,
                            contentDescription = "Pinned",
                        )
                    }
                }
            }
            if (note.title.isNotEmpty() && note.body.isNotEmpty())
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp))
            if (note.body.isNotEmpty()) {
                Text(text = note.body, style = MaterialTheme.typography.body2, maxLines = 10,overflow = TextOverflow.Ellipsis)
            }
            if (note.label != null) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    val label = labels.firstOrNull { label -> label.id == note.label }
                    label?.let {
                        NoteLabel(text = label.value)
                    }
                }
            }
        }
    }
}