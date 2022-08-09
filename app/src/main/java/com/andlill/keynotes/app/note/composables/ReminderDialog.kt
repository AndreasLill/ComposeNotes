package com.andlill.keynotes.app.note.composables

import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.R
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

        // Load calendar with active reminder time if exists.
        val calendar = Calendar.getInstance()
        reminderTime?.let {
            calendar.timeInMillis = reminderTime
        }

        // Current date and time as default value.
        val selectedDate = remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)) }
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
            selectedDate.value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(resultCalendar.time)
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
                if (reminderTime != null) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colors.error),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.onSurface
                        ),
                        onClick = {
                            Log.d("ReminderDialog", "Reminder Cancelled.")
                            onClick(null)
                            state.value = false
                        }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Outlined.NotificationsOff,
                            contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.note_screen_dialog_reminder_button_cancel),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.sp)
                    }
                }
                else {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.onSurface
                        ),
                        onClick = {
                            Log.d("ReminderDialog", "Reminder Set: ${selectedDate.value}, ${selectedTime.value}.")
                            onClick(calendar)
                            state.value = false
                        }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.note_screen_dialog_reminder_button_add),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.sp)
                    }
                }
            }
        }
    }
}