package com.andlill.keynotes.app.note.composables

import android.text.format.DateFormat.is24HourFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
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
import com.andlill.keynotes.R
import com.andlill.keynotes.ui.shared.button.DialogButton
import com.andlill.keynotes.ui.shared.text.DialogTitle
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderDialog(state: MutableState<Boolean>, reminderTime: Long?, onClick: (Calendar?) -> Unit) {
    if (state.value) {
        val context = LocalContext.current
        val calendar by remember(state.value) {
            mutableStateOf(
                Calendar.getInstance().apply {
                    reminderTime?.let {
                        timeInMillis = it
                    }
                }
            )
        }
        // Current date and time as default value.
        val selectedDate = remember { mutableStateOf(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.time)) }
        val selectedTime = remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)) }

        // Create time picker dialog.
        val timeFormat = if (is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(timeFormat)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .build()
        timePickerDialog.addOnPositiveButtonClickListener {
            calendar.set(Calendar.HOUR_OF_DAY, timePickerDialog.hour)
            calendar.set(Calendar.MINUTE, timePickerDialog.minute)
            selectedTime.value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        }

        // Create date picker dialog.
        val datePickerDialog = MaterialDatePicker.Builder.datePicker()
            .setSelection(calendar.timeInMillis)
            .build()
        datePickerDialog.addOnPositiveButtonClickListener {
            val resultCalendar = Calendar.getInstance()
            resultCalendar.timeInMillis = it
            calendar.set(Calendar.YEAR, resultCalendar.get(Calendar.YEAR))
            calendar.set(Calendar.MONTH, resultCalendar.get(Calendar.MONTH))
            calendar.set(Calendar.DAY_OF_MONTH, resultCalendar.get(Calendar.DAY_OF_MONTH))
            selectedDate.value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(resultCalendar.time)
        }

        Dialog(
            onDismissRequest = { state.value = false }) {
            Column(modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)) {
                DialogTitle(text = stringResource(R.string.note_screen_dialog_reminder_title))
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            (context as AppCompatActivity).let {
                                datePickerDialog.show(it.supportFragmentManager, "DatePickerDialog")
                            }
                        },
                    enabled = false,
                    readOnly = true,
                    singleLine = true,
                    shape = RectangleShape,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        disabledTextColor = MaterialTheme.colors.onSurface,
                        disabledTrailingIconColor = MaterialTheme.colors.onSurface,
                    ),
                    textStyle = TextStyle(fontSize = 15.sp),
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                    value = selectedDate.value,
                    onValueChange = { }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            (context as AppCompatActivity).let {
                                timePickerDialog.show(it.supportFragmentManager, "TimePickerDialog")
                            }
                        },
                    enabled = false,
                    readOnly = true,
                    singleLine = true,
                    shape = RectangleShape,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        disabledTextColor = MaterialTheme.colors.onSurface,
                        disabledTrailingIconColor = MaterialTheme.colors.onSurface,
                    ),
                    textStyle = TextStyle(fontSize = 15.sp),
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                    value = selectedTime.value,
                    onValueChange = { }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Row {
                        if (reminderTime != null) {
                            DialogButton(
                                text = stringResource(R.string.button_cancel),
                                icon = Icons.Outlined.Close,
                                backgroundColor = Color.Transparent,
                                textColor = MaterialTheme.colors.error,
                                onClick = {
                                    onClick(null)
                                    state.value = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        DialogButton(
                            text = if (reminderTime != null) stringResource(R.string.button_save) else stringResource(R.string.button_create),
                            backgroundColor = MaterialTheme.colors.primary,
                            textColor = MaterialTheme.colors.onPrimary,
                            onClick = {
                                onClick(calendar)
                                state.value = false
                            }
                        )
                    }
                }
            }
        }
    }
}