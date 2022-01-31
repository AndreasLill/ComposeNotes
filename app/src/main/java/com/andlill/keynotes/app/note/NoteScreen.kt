package com.andlill.keynotes.app.note

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NotificationAdd
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andlill.keynotes.app.note.composables.ReminderDialog
import com.andlill.keynotes.app.note.composables.ThemeDropDown
import com.andlill.keynotes.app.shared.LifecycleEventHandler
import com.andlill.keynotes.app.shared.MenuIconButton
import com.andlill.keynotes.app.shared.clearFocusOnKeyboardDismiss
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsHeight

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