package com.andlill.keynotes.app.note

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andlill.keynotes.ui.components.MenuIconButton
import com.andlill.keynotes.ui.components.util.LifecycleEventHandler
import com.andlill.keynotes.ui.components.util.clearFocusOnKeyboardDismiss
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsHeight
import java.util.*

@Composable
fun NoteScreen(navigation: NavController, viewModel: NoteViewModel = viewModel(), noteId: Int = -1) {

    val themeMenuState = remember { mutableStateOf(false) }
    val reminderDialogState = remember { mutableStateOf(false) }

    val reminderIcon = when {
        viewModel.isReminderActive() -> Icons.Filled.NotificationsActive
        else -> Icons.Outlined.NotificationAdd
    }
    val noteColor = when {
        isSystemInDarkTheme() -> DarkNoteColors[viewModel.color]
        else -> LightNoteColors[viewModel.color]
    }

    LaunchedEffect(Unit) {
        // Set default note color to surface.
        viewModel.loadNote(noteId)
    }
    // Handle lifecycle events.
    LifecycleEventHandler { event ->
        when (event) {
            // Save note on stop event.
            Lifecycle.Event.ON_STOP -> viewModel.saveNote()
            else -> {}
        }
    }
    Scaffold(
        backgroundColor = animateColorAsState(noteColor).value,
        topBar = {
            Column {
                Spacer(modifier = Modifier
                    .statusBarsHeight()
                    .fillMaxWidth()
                )
                TopAppBar(
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    title = {
                        NoteTitleTextField(
                            placeholder = "No Title",
                            value = viewModel.title,
                            onValueChange = {
                                viewModel.title = it
                            }
                        )
                    },
                    navigationIcon = {
                        MenuIconButton(icon = Icons.Filled.ArrowBack, color = MaterialTheme.colors.onSurface) {
                            navigation.navigateUp()
                        }
                    },
                    actions = {
                        MenuIconButton(icon = reminderIcon, color = MaterialTheme.colors.onSurface) {
                            reminderDialogState.value = true
                        }
                        MenuIconButton(icon = Icons.Outlined.Palette, color = MaterialTheme.colors.onSurface) {
                            themeMenuState.value = true
                        }
                        MenuIconButton(icon = Icons.Outlined.Delete, color = MaterialTheme.colors.onSurface) {
                            // TODO: Change delete to move to trash.
                            viewModel.deleteNote()
                            navigation.navigateUp()
                        }
                        // Note theme drop down menu.
                        ThemeDropDown(state = themeMenuState) {
                            viewModel.color = it
                        }
                        ReminderDialog(active = viewModel.isReminderActive(), state = reminderDialogState) {
                            if (it == null) {
                                viewModel.cancelReminder()
                            }
                            else {
                                viewModel.setReminder(it)
                            }
                        }
                    }
                )
            }
        },
        content = { innerPadding ->
            Column(modifier = Modifier
                .background(Color.Transparent)
                .navigationBarsWithImePadding()
                .padding(innerPadding)) {
                NoteBodyTextField(
                    placeholder = "Empty",
                    value = viewModel.body,
                    onValueChange = {
                        viewModel.body = it
                    }
                )
            }
        }
    )
}

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

@Composable
fun ThemeDropDown(state: MutableState<Boolean>, onClick: (Int) -> Unit) {
    DropdownMenu(
        expanded = state.value,
        modifier = Modifier
            .background(MaterialTheme.colors.surface),
        onDismissRequest = { state.value = false }) {
        Column(modifier = Modifier.padding(PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp))) {
            Text(
                text = "Colors",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.alpha(0.6f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())) {
                val colorList = if (isSystemInDarkTheme()) DarkNoteColors else LightNoteColors
                colorList.forEachIndexed { index, value ->
                    ColorSelectButton(color = value) {
                        onClick(index)
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSelectButton(color: Color, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier
            .width(32.dp)
            .height(32.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
        ),
        shape = CircleShape,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
        onClick = { onClick() },
    ) {}
}

@Composable
fun NoteBodyTextField(placeholder: String, value: String, onValueChange: (String) -> Unit) {
    BasicTextField(
        modifier = Modifier
            .clearFocusOnKeyboardDismiss()
            .padding(20.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        value = value,
        onValueChange = onValueChange,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colors.onSurface,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        ),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onSurface.copy(0.6f)
                    )
                )
            }
            innerTextField()
        }
    )
}

@Composable
fun NoteTitleTextField(placeholder: String, value: String, onValueChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        modifier = Modifier
            .clearFocusOnKeyboardDismiss()
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onSurface,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Next)
            }
        ),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onSurface.copy(0.6f)
                    )
                )
            }
            innerTextField()
        }
    )
}