package com.andlill.keynotes.app.note.composables

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
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
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderDialog(reminderTime: Long, state: MutableState<Boolean>, onClick: (Calendar?) -> Unit) {
    if (state.value) {

        // Load calendar with active reminder time if exists.
        val calendar = Calendar.getInstance()
        if (reminderTime > 0) {
            calendar.timeInMillis = reminderTime
        }

        val time = listOf("Morning (9:00)", "Noon (12:00)", "Afternoon (15:00)", "Evening (18:00)", "Night (21:00)", "Midnight (00:00)")

        // Current date and time as default value.
        val selectedDate = remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)) }
        val selectedTime = remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)) }

        val context = LocalContext.current
        val datePickerDialog = DatePickerDialog(
            context, R.style.AppDatePicker,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                selectedDate.value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
        val timePickerDialog = TimePickerDialog(
            context, R.style.AppTimePicker,
            { _: TimePicker, hour: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                selectedTime.value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        )

        Dialog(
            onDismissRequest = { state.value = false }) {
            Column(modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)) {
                Text(
                    text = "Reminder",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onSurface)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    enabled = false,
                    readOnly = true,
                    singleLine = true,
                    shape = RectangleShape,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        textColor = MaterialTheme.colors.onSurface,
                        disabledTextColor = MaterialTheme.colors.onSurface,
                        trailingIconColor = MaterialTheme.colors.primary,
                        disabledTrailingIconColor = MaterialTheme.colors.primary,
                    ),
                    textStyle = TextStyle(fontSize = 15.sp),
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                    value = selectedDate.value,
                    onValueChange = { }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { timePickerDialog.show() },
                    enabled = false,
                    readOnly = true,
                    singleLine = true,
                    shape = RectangleShape,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        textColor = MaterialTheme.colors.onSurface,
                        disabledTextColor = MaterialTheme.colors.onSurface,
                        trailingIconColor = MaterialTheme.colors.primary,
                        disabledTrailingIconColor = MaterialTheme.colors.primary,
                    ),
                    textStyle = TextStyle(fontSize = 15.sp),
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                    value = selectedTime.value,
                    onValueChange = {
                        selectedTime.value = time[time.indexOf(it)]
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (reminderTime > 0) {
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.onSurface
                        ),
                        onClick = {
                            Log.d("ReminderDialog", "Reminder cancelled.")
                            onClick(null)
                        }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Outlined.NotificationsOff,
                            contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cancel",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
                else {
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.onSurface
                        ),
                        onClick = {
                            Log.d("ReminderDialog", "Reminder Set: ${selectedDate.value}, ${selectedTime.value}.")
                            onClick(calendar)
                        }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}