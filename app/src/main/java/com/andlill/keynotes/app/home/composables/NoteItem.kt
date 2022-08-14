package com.andlill.keynotes.app.home.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.keynotes.R
import com.andlill.keynotes.model.NoteWrapper
import com.andlill.keynotes.ui.shared.label.NoteLabel
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors
import com.andlill.keynotes.utils.TimeUtils.daysBetween
import com.andlill.keynotes.utils.TimeUtils.toDateString

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
            defaultElevation = 1.dp,
            pressedElevation = 1.dp,
            hoveredElevation = 1.dp,
            focusedElevation = 1.dp,
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (note.note.pinned) {
                        Icon(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.CenterEnd),
                            imageVector = Icons.Outlined.PushPin,
                            contentDescription = null,
                        )
                    }
                }
            }
            if (note.note.title.isNotEmpty() && note.note.body.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (note.note.body.isNotEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = note.note.body,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.sp,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis)
            }
            if (note.labels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(note.labels) { label ->
                        NoteLabel(
                            icon = Icons.Outlined.Label,
                            text = label.value,
                            color = if (note.note.color == 0) MaterialTheme.colors.onSurface.copy(0.08f) else MaterialTheme.colors.surface.copy(0.5f)
                        )
                    }
                }
            }
            note.note.reminder?.let {
                val height = if (note.labels.isEmpty()) 16.dp else 8.dp
                val daysBetween = note.note.reminder.daysBetween()
                val reminderText = when {
                    daysBetween == 0 -> String.format("%s, %s", stringResource(R.string.date_today), note.note.reminder.toDateString("HH:mm"))
                    daysBetween == 1 -> String.format("%s, %s", stringResource(R.string.date_tomorrow), note.note.reminder.toDateString("HH:mm"))
                    daysBetween > 365 -> note.note.reminder.toDateString("d MMM YYYY, HH:mm")
                    else -> note.note.reminder.toDateString("d MMM, HH:mm")
                }
                Spacer(modifier = Modifier.height(height))
                NoteLabel(
                    icon = Icons.Outlined.Alarm,
                    text = reminderText,
                    color = if (note.note.color == 0) MaterialTheme.colors.onSurface.copy(0.08f) else MaterialTheme.colors.surface.copy(0.5f)
                )
            }

            note.note.deletion?.let {
                val height = if (note.labels.isEmpty()) 16.dp else 8.dp
                val deletionText = when (val daysBetween = note.note.deletion.daysBetween()) {
                    0 -> stringResource(R.string.home_screen_note_text_deletion_today)
                    1 -> stringResource(R.string.home_screen_note_text_deletion_tomorrow)
                    else -> String.format(stringResource(R.string.home_screen_note_text_deletion_days), daysBetween)
                }
                Spacer(modifier = Modifier.height(height))
                NoteLabel(
                    icon = Icons.Outlined.AutoDelete,
                    text = deletionText,
                    color = if (note.note.color == 0) MaterialTheme.colors.onSurface.copy(0.08f) else MaterialTheme.colors.surface.copy(0.5f))
            }
        }
    }
}