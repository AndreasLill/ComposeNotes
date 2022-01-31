package com.andlill.keynotes.app.note.composables

import android.util.Log
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.*

@Composable
fun ReminderDialog(active: Boolean, state: MutableState<Boolean>, onClick: (Calendar?) -> Unit) {
    val date = listOf("Today", "Tomorrow")
    val time = listOf("Morning (9:00)", "Noon (12:00)", "Afternoon (15:00)", "Evening (18:00)", "Night (21:00)", "Midnight (00:00)")
    val dateDropDownState = remember { mutableStateOf(false) }
    val timeDropDownState = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(date[0]) }
    val selectedTime = remember { mutableStateOf(time[0]) }
    if (state.value) {
        Dialog(
            onDismissRequest = { state.value = false }) {
            Column(modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)) {
                Text("Reminder", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dateDropDownState.value = true },
                        enabled = false,
                        readOnly = true,
                        singleLine = true,
                        shape = RectangleShape,
                        textStyle = TextStyle(fontSize = 14.sp),
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                        value = selectedDate.value,
                        onValueChange = {
                            selectedDate.value = date[date.indexOf(it)]
                        }
                    )
                    DropdownMenu(
                        expanded = dateDropDownState.value,
                        onDismissRequest = { dateDropDownState.value = false }) {
                        date.forEachIndexed { index, value ->
                            DropdownMenuItem(onClick = {
                                selectedDate.value = date[index]
                                dateDropDownState.value = false
                            }) {
                                Text(value)
                            }
                        }

                    }
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { timeDropDownState.value = true },
                        enabled = false,
                        readOnly = true,
                        singleLine = true,
                        shape = RectangleShape,
                        textStyle = TextStyle(fontSize = 14.sp),
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                        value = selectedTime.value,
                        onValueChange = {
                            selectedTime.value = time[time.indexOf(it)]
                        }
                    )
                    DropdownMenu(
                        expanded = timeDropDownState.value,
                        onDismissRequest = { timeDropDownState.value = false }) {
                        time.forEachIndexed { index, value ->
                            DropdownMenuItem(onClick = {
                                selectedTime.value = time[index]
                                timeDropDownState.value = false
                            }) {
                                Text(value)
                            }
                        }

                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (active) {
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.onSurface
                        ),
                        onClick = {
                            onClick(null)
                            Log.d("ReminderDialog", "Reminder cancelled.")
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
                            val calendar = Calendar.getInstance()
                            when (selectedDate.value) {
                                date[0] -> calendar.add(Calendar.DATE, 0)
                                date[1] -> calendar.add(Calendar.DATE, 1)
                            }
                            when (selectedTime.value) {
                                time[0] -> calendar.set(Calendar.HOUR_OF_DAY, 9)
                                time[1] -> calendar.set(Calendar.HOUR_OF_DAY, 12)
                                time[2] -> calendar.set(Calendar.HOUR_OF_DAY, 15)
                                time[3] -> calendar.set(Calendar.HOUR_OF_DAY, 18)
                                time[4] -> calendar.set(Calendar.HOUR_OF_DAY, 21)
                                time[5] -> calendar.set(Calendar.HOUR_OF_DAY, 0)
                            }
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