package com.andlill.composenotes.app.note.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationManagerCompat
import com.andlill.composenotes.R
import com.andlill.composenotes.model.NoteReminderRepeat
import com.andlill.composenotes.ui.shared.button.DialogButton
import com.andlill.composenotes.ui.shared.text.DialogTitle
import com.andlill.composenotes.utils.TimeUtils.toDateString
import com.andlill.composenotes.utils.TimeUtils.toLocalDateTime
import com.andlill.composenotes.utils.TimeUtils.toMilliSeconds
import com.andlill.composenotes.utils.TimeUtils.toTimeString
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialog(
    state: MutableState<Boolean>,
    initialDateTime: LocalDateTime?,
    initialRepeat: String?,
    onClick: (LocalDateTime?, String?) -> Unit,
    onRequestPermission: () -> Unit
) {
    if (state.value) {
        val datePickerDialogState = remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateTime?.toMilliSeconds() ?: LocalDateTime.now().toMilliSeconds()
        )
        val timePickerDialogState = remember { mutableStateOf(false) }
        val timePickerState = rememberTimePickerState(
            initialHour = initialDateTime?.hour ?: LocalTime.now().hour,
            initialMinute = initialDateTime?.minute ?: LocalTime.now().minute,
        )

        val reminderRepeatOff = stringResource(R.string.note_screen_dialog_reminder_repeat_off)
        val reminderRepeatDaily = stringResource(R.string.note_screen_dialog_reminder_repeat_daily)
        val reminderRepeatWeekly = stringResource(R.string.note_screen_dialog_reminder_repeat_weekly)
        val reminderRepeatMonthly = stringResource(R.string.note_screen_dialog_reminder_repeat_monthly)
        val reminderRepeatYearly = stringResource(R.string.note_screen_dialog_reminder_repeat_yearly)
        val reminderRepeatOptions = remember {
            arrayOf(
                reminderRepeatOff,
                reminderRepeatDaily,
                reminderRepeatWeekly,
                reminderRepeatMonthly,
                reminderRepeatYearly
            )
        }
        val reminderRepeatDropDownState = remember { mutableStateOf(false) }
        val reminderRepeat = remember {
            mutableStateOf(
                when (initialRepeat) {
                    NoteReminderRepeat.REPEAT_DAILY -> reminderRepeatDaily
                    NoteReminderRepeat.REPEAT_WEEKLY -> reminderRepeatWeekly
                    NoteReminderRepeat.REPEAT_MONTHLY -> reminderRepeatMonthly
                    NoteReminderRepeat.REPEAT_YEARLY -> reminderRepeatYearly
                    else -> reminderRepeatOff
                }
            )
        }

        val context = LocalContext.current
        var selectedDate by remember { mutableStateOf(initialDateTime?.toLocalDate() ?: LocalDate.now()) }
        var selectedTime by remember { mutableStateOf(initialDateTime?.toLocalTime() ?: LocalTime.now()) }
        val selectedDateText by remember(selectedDate) { mutableStateOf(selectedDate.toDateString("d MMM, YYYY")) }
        val selectedTimeText by remember(selectedTime) { mutableStateOf(selectedTime.toTimeString("HH:mm")) }

        // Reminder Dialog
        Dialog(
            onDismissRequest = { state.value = false }) {
            Column(modifier = Modifier
                .background(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                )
                .padding(16.dp)) {
                DialogTitle(text = stringResource(R.string.note_screen_dialog_reminder_title))
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            datePickerDialogState.value = true
                        },
                    enabled = false,
                    readOnly = true,
                    singleLine = true,
                    shape = RectangleShape,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    label = {
                        Text(stringResource(R.string.note_screen_dialog_reminder_label_date))
                    },
                    textStyle = TextStyle(fontSize = 15.sp),
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                    value = selectedDateText,
                    onValueChange = { }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            timePickerDialogState.value = true
                        },
                    enabled = false,
                    readOnly = true,
                    singleLine = true,
                    shape = RectangleShape,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    label = {
                        Text(stringResource(R.string.note_screen_dialog_reminder_label_time))
                    },
                    textStyle = TextStyle(fontSize = 15.sp),
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                    value = selectedTimeText,
                    onValueChange = { }
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = reminderRepeatDropDownState.value,
                    onExpandedChange = {
                        reminderRepeatDropDownState.value = !reminderRepeatDropDownState.value
                    }
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable {},
                        enabled = false,
                        readOnly = true,
                        singleLine = true,
                        shape = RectangleShape,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        label = {
                            Text(stringResource(R.string.note_screen_dialog_reminder_label_repeat))
                        },
                        textStyle = TextStyle(fontSize = 15.sp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = reminderRepeatDropDownState.value
                            )
                        },
                        value = reminderRepeat.value,
                        onValueChange = { }
                    )
                    ExposedDropdownMenu(
                        expanded = reminderRepeatDropDownState.value,
                        onDismissRequest = { reminderRepeatDropDownState.value = false }
                    ) {
                        reminderRepeatOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(option)
                                },
                                onClick = {
                                    reminderRepeat.value = option
                                    reminderRepeatDropDownState.value = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Row {
                        if (initialDateTime != null) {
                            DialogButton(
                                text = stringResource(R.string.button_cancel),
                                icon = Icons.Outlined.Close,
                                backgroundColor = Color.Transparent,
                                textColor = MaterialTheme.colorScheme.error,
                                onClick = {
                                    onClick(null, null)
                                    state.value = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        DialogButton(
                            text = if (initialDateTime != null) stringResource(R.string.button_save) else stringResource(R.string.button_create),
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            onClick = {
                                if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                                    onClick(
                                        LocalDateTime.of(selectedDate, selectedTime),
                                        when(reminderRepeat.value) {
                                            reminderRepeatDaily -> NoteReminderRepeat.REPEAT_DAILY
                                            reminderRepeatWeekly -> NoteReminderRepeat.REPEAT_WEEKLY
                                            reminderRepeatMonthly -> NoteReminderRepeat.REPEAT_MONTHLY
                                            reminderRepeatYearly -> NoteReminderRepeat.REPEAT_YEARLY
                                            else -> null
                                        }
                                    )
                                    state.value = false
                                }
                                else {
                                    onRequestPermission()
                                }
                            }
                        )
                    }
                }
            }
        }

        if (datePickerDialogState.value) {
            DatePickerDialog(
                onDismissRequest = { datePickerDialogState.value = false },
                dismissButton = {
                    DialogButton(
                        text = stringResource(R.string.button_cancel),
                        backgroundColor = Color.Transparent,
                        textColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            datePickerDialogState.value = false
                        }
                    )
                },
                confirmButton = {
                    DialogButton(
                        text = stringResource(R.string.button_ok),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onPrimary,
                        onClick = {
                            selectedDate = datePickerState.selectedDateMillis?.toLocalDateTime()?.toLocalDate()
                            datePickerDialogState.value = false
                        }
                    )
                },
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

        if (timePickerDialogState.value) {
            Dialog(
                onDismissRequest = { timePickerDialogState.value = false }) {
                Column(modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface
                    )
                    .padding(16.dp)) {
                    DialogTitle(text = stringResource(R.string.note_screen_dialog_timepicker_title))
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        TimePicker(
                            state = timePickerState,
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Row {
                            DialogButton(
                                text = stringResource(R.string.button_cancel),
                                backgroundColor = Color.Transparent,
                                textColor = MaterialTheme.colorScheme.primary,
                                onClick = {
                                    timePickerDialogState.value = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DialogButton(
                                text = stringResource(R.string.button_ok),
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                textColor = MaterialTheme.colorScheme.onPrimary,
                                onClick = {
                                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                                    timePickerDialogState.value = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}