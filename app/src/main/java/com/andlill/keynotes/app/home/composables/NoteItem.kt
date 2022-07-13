package com.andlill.keynotes.app.home.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andlill.keynotes.model.NoteWrapper
import com.andlill.keynotes.ui.label.NoteLabel
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors

@Composable
fun NoteItem(note: NoteWrapper, callback: () -> Unit) {
    val noteColor = when {
        isSystemInDarkTheme() -> DarkNoteColors[note.note.color]
        else -> LightNoteColors[note.note.color]
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
        Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
            if (note.note.title.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.padding(end = 24.dp),
                        text = note.note.title,
                        style = MaterialTheme.typography.h3,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (note.note.pinned) {
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
            if (note.note.title.isNotEmpty() && note.note.body.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (note.note.body.isNotEmpty()) {
                Text(text = note.note.body, style = MaterialTheme.typography.body2, maxLines = 10,overflow = TextOverflow.Ellipsis)
            }
            if (note.labels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(note.labels) { label ->
                    NoteLabel(text = label.value)
                }
            }
        }
    }
}