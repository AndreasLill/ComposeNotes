package com.andlill.composenotes.app.home.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.R
import com.andlill.composenotes.model.NoteWrapper
import com.andlill.composenotes.ui.shared.label.NoteLabel
import com.andlill.composenotes.utils.ColorUtils.darken
import com.andlill.composenotes.utils.TimeUtils.daysBetween
import com.andlill.composenotes.utils.TimeUtils.toDateString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(note: NoteWrapper, onClick: () -> Unit) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val isDarkTheme = isSystemInDarkTheme()
    val noteColor = remember(note.note.color) {
        when {
            note.note.color == 0 -> surfaceColor
            isDarkTheme -> Color(note.note.color).darken()
            else -> Color(note.note.color)
        }
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
        color = animateColorAsState(noteColor).value,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.1f)),
        content = {
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
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp,
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
                                color = if (note.note.color == 0) MaterialTheme.colorScheme.onSurface.copy(0.08f) else MaterialTheme.colorScheme.surface.copy(0.5f)
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
                        color = if (note.note.color == 0) MaterialTheme.colorScheme.onSurface.copy(0.08f) else MaterialTheme.colorScheme.surface.copy(0.5f)
                    )
                }

                note.note.deletion?.let {
                    val height = if (note.labels.isEmpty()) 16.dp else 8.dp
                    val daysBetween = note.note.deletion.daysBetween()
                    val deletionText = when {
                        daysBetween <= 0 -> stringResource(R.string.home_screen_note_text_deletion_today)
                        daysBetween == 1 -> stringResource(R.string.home_screen_note_text_deletion_tomorrow)
                        else -> String.format(stringResource(R.string.home_screen_note_text_deletion_days), daysBetween)
                    }
                    Spacer(modifier = Modifier.height(height))
                    NoteLabel(
                        icon = Icons.Outlined.AutoDelete,
                        text = deletionText,
                        color = if (note.note.color == 0) MaterialTheme.colorScheme.onSurface.copy(0.08f) else MaterialTheme.colorScheme.surface.copy(0.5f))
                }
            }
        }
    )
}