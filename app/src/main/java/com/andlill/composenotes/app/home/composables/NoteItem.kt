package com.andlill.composenotes.app.home.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlill.composenotes.R
import com.andlill.composenotes.model.NoteReminderRepeat
import com.andlill.composenotes.model.NoteWrapper
import com.andlill.composenotes.ui.shared.button.CheckBoxButton
import com.andlill.composenotes.utils.ColorUtils.darken
import com.andlill.composenotes.utils.TimeUtils.daysBetween
import com.andlill.composenotes.utils.TimeUtils.toLocalDateTime
import com.andlill.composenotes.utils.TimeUtils.toSimpleDateString
import com.google.accompanist.flowlayout.FlowRow
import java.time.LocalDateTime
import kotlin.text.Typography.ellipsis

@Composable
fun NoteItem(noteWrapper: NoteWrapper, maxLines: Int, onClick: () -> Unit) {
    val context = LocalContext.current
    val surfaceColor = MaterialTheme.colorScheme.surface
    val isDarkTheme = isSystemInDarkTheme()
    val noteColor = remember(noteWrapper.note.color) {
        when {
            noteWrapper.note.color == 0 -> surfaceColor
            isDarkTheme -> Color(noteWrapper.note.color).darken()
            else -> Color(noteWrapper.note.color)
        }
    }
    val reminderRepeatText = remember(noteWrapper.note.reminderRepeat) {
        mutableStateOf(
            when (noteWrapper.note.reminderRepeat) {
                NoteReminderRepeat.REPEAT_DAILY -> context.resources.getString(R.string.home_screen_note_reminder_repeat_daily)
                NoteReminderRepeat.REPEAT_WEEKLY -> context.resources.getString(R.string.home_screen_note_reminder_repeat_weekly)
                NoteReminderRepeat.REPEAT_MONTHLY -> context.resources.getString(R.string.home_screen_note_reminder_repeat_monthly)
                NoteReminderRepeat.REPEAT_YEARLY -> context.resources.getString(R.string.home_screen_note_reminder_repeat_yearly)
                else -> null
            }
        )
    }
    val uncheckedBoxes = remember(noteWrapper.checkBoxes) {
        noteWrapper.checkBoxes.filter { !it.checked }.sortedBy { it.order }
    }
    val checkedBoxesCount = remember(noteWrapper.checkBoxes) {
        noteWrapper.checkBoxes.filter { it.checked }.size
    }
    val labelsSorted = remember(noteWrapper.labels) {
        noteWrapper.labels.sortedBy { it.value.lowercase() }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        color = animateColorAsState(noteColor).value,
        shadowElevation = 1.dp,
        border = if (noteWrapper.note.color == 0) BorderStroke(width = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(0.2f)) else null,
        content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                if (noteWrapper.note.title.isBlank() && noteWrapper.note.body.isBlank() && noteWrapper.checkBoxes.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.home_screen_note_empty),
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 0.sp,
                    )
                }
                if (noteWrapper.note.title.isNotBlank()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.padding(end = 24.dp),
                            text = noteWrapper.note.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (noteWrapper.note.pinned) {
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

                if (noteWrapper.note.title.isNotBlank() && noteWrapper.note.body.isNotBlank())
                    Spacer(modifier = Modifier.height(12.dp))

                if (noteWrapper.note.body.isNotBlank()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = noteWrapper.note.body,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp,
                        maxLines = maxLines,
                        overflow = TextOverflow.Clip
                    )
                    if (noteWrapper.note.body.lines().size >= maxLines) {
                        Text(
                            text = ellipsis.toString(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 16.sp,
                            letterSpacing = 0.sp,
                        )
                    }
                }

                if (noteWrapper.note.title.isNotBlank() && uncheckedBoxes.isNotEmpty())
                    Spacer(modifier = Modifier.height(12.dp))

                if (uncheckedBoxes.isNotEmpty() || checkedBoxesCount > 0) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        uncheckedBoxes.forEachIndexed { index, item ->
                            if (index >= maxLines) {
                                if (index == uncheckedBoxes.size - 1)
                                    Text(
                                        text = ellipsis.toString(),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        lineHeight = 16.sp,
                                        letterSpacing = 0.sp,
                                    )
                                return@forEachIndexed
                            }
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                                    text = item.text.ifEmpty { stringResource(R.string.note_screen_note_placeholder_checkbox) },
                                    fontSize = 14.sp,
                                    color = if (item.text.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(0.6f) else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 16.sp,
                                    letterSpacing = 0.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (checkedBoxesCount > 0) {
                            if (uncheckedBoxes.isEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            Text(
                                text = stringResource(R.string.home_screen_note_checked_items, checkedBoxesCount),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                                fontWeight = FontWeight.Normal,
                                lineHeight = 16.sp,
                                letterSpacing = 0.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                if (labelsSorted.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        mainAxisSpacing = 4.dp,
                        crossAxisSpacing = 4.dp,
                    ) {
                        labelsSorted.forEach {
                            NoteLabel(
                                icon = Icons.Outlined.Label,
                                text = it.value,
                                noteColor = noteWrapper.note.color
                            )
                        }
                    }
                }
                noteWrapper.note.reminder?.let {
                    val height = if (noteWrapper.labels.isEmpty()) 16.dp else 4.dp
                    val reminderText = it.toLocalDateTime().toSimpleDateString(context)
                    Spacer(modifier = Modifier.height(height))
                    TimeLabel(
                        icon = Icons.Outlined.NotificationsActive,
                        text = reminderText,
                        repeat = reminderRepeatText.value,
                        noteColor = noteWrapper.note.color
                    )
                }
                noteWrapper.note.deletion?.let {
                    val height = if (noteWrapper.labels.isEmpty()) 16.dp else 4.dp
                    val deletion = it.toLocalDateTime()
                    val current = LocalDateTime.now()
                    val deletionText = when (deletion.daysBetween(current)) {
                        0 -> stringResource(R.string.home_screen_note_text_deletion_today)
                        1 -> stringResource(R.string.home_screen_note_text_deletion_tomorrow)
                        else -> stringResource(R.string.home_screen_note_text_deletion_days, deletion.daysBetween(current))
                    }
                    Spacer(modifier = Modifier.height(height))
                    TimeLabel(
                        icon = Icons.Outlined.AutoDelete,
                        text = deletionText,
                        repeat = null,
                        noteColor = noteWrapper.note.color
                    )
                }
            }
        }
    )
}