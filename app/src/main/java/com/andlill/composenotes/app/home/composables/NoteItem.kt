package com.andlill.composenotes.app.home.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import com.andlill.composenotes.model.NoteCheckBox
import com.andlill.composenotes.model.NoteWrapper
import com.andlill.composenotes.ui.shared.button.CheckBoxButton
import com.andlill.composenotes.ui.shared.label.NoteLabel
import com.andlill.composenotes.utils.ColorUtils.darken
import com.andlill.composenotes.utils.TimeUtils.daysBetween
import com.andlill.composenotes.utils.TimeUtils.toDateString
import com.andlill.composenotes.utils.TimeUtils.toLocalDateTime
import java.time.LocalDateTime

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
    val checkBoxesSorted = remember(note.checkBoxes) {
        note.checkBoxes.sortedWith(compareBy<NoteCheckBox> { it.checked }.thenBy { it.order })
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        color = animateColorAsState(noteColor).value,
        shadowElevation = 2.dp,
        border = if (note.note.color == 0) BorderStroke(width = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(0.2f)) else null,
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
                if (note.note.body.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = note.note.body,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp,
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (checkBoxesSorted.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        checkBoxesSorted.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically) {
                                CheckBoxButton(
                                    enabled = false,
                                    checkedBackgroundColor = Color.Transparent,
                                    checkedBorderColor = Color.Transparent,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                                    checkMarkColor = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                                    checkBoxSize = 16.dp,
                                    checkMarkSize = 16.dp,
                                    checked = item.checked,
                                    onClick = {  }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.text,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 16.sp,
                                    letterSpacing = 0.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                if (note.labels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
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
                    val height = if (note.labels.isEmpty()) 16.dp else 12.dp
                    val modified = it.toLocalDateTime()
                    val current = LocalDateTime.now()
                    val reminderText = when (modified.daysBetween(current)) {
                        0 -> String.format("%s, %s", stringResource(R.string.date_today), modified.toDateString("HH:mm"))
                        1 -> String.format("%s, %s", stringResource(R.string.date_yesterday), modified.toDateString("HH:mm"))
                        else -> {
                            if (modified.year != current.year) {
                                modified.toDateString("d MMM YYYY, HH:mm")
                            }
                            else {
                                modified.toDateString("d MMM, HH:mm")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(height))
                    NoteLabel(
                        icon = Icons.Outlined.Alarm,
                        text = reminderText,
                        color = if (note.note.color == 0) MaterialTheme.colorScheme.onSurface.copy(0.08f) else MaterialTheme.colorScheme.surface.copy(0.5f)
                    )
                }

                note.note.deletion?.let {
                    val height = if (note.labels.isEmpty()) 16.dp else 12.dp
                    val deletion = it.toLocalDateTime()
                    val current = LocalDateTime.now()
                    val deletionText = when (deletion.daysBetween(current)) {
                        0 -> stringResource(R.string.home_screen_note_text_deletion_today)
                        1 -> stringResource(R.string.home_screen_note_text_deletion_tomorrow)
                        else -> String.format(stringResource(R.string.home_screen_note_text_deletion_days), deletion.daysBetween(current))
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