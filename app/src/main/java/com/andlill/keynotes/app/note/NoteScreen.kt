package com.andlill.keynotes.app.note

import android.app.Application
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andlill.keynotes.R
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
fun NoteScreen(navigation: NavController, noteId: Int = -1) {
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(LocalContext.current.applicationContext as Application, noteId))

    val themeMenuState = remember { mutableStateOf(false) }
    val reminderDialogState = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val reminderIcon = when {
        viewModel.note.reminder != null -> Icons.Filled.NotificationsActive
        else -> Icons.Outlined.Notifications
    }
    val noteColor = when {
        isSystemInDarkTheme() -> DarkNoteColors[viewModel.note.color]
        else -> LightNoteColors[viewModel.note.color]
    }

    // Handle lifecycle events.
    LifecycleEventHandler { event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                focusManager.clearFocus()
            }
            Lifecycle.Event.ON_STOP -> {
                viewModel.onClose()
            }
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
                            viewModel.deleteNote()
                            navigation.navigateUp()
                        }
                        ThemeDropDown(state = themeMenuState) {
                            viewModel.note = viewModel.note.copy(color = it)
                        }
                        ReminderDialog(reminderTime = viewModel.note.reminder, state = reminderDialogState) {
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
                .padding(innerPadding)
                .fillMaxSize()
                .clickable(interactionSource = interactionSource, indication = null) { focusRequester.requestFocus() }) {
                NoteTitleTextField(
                    placeholder = stringResource(R.string.note_screen_title_placeholder),
                    value = viewModel.note.title,
                    onValueChange = {
                        viewModel.note = viewModel.note.copy(title = it)
                    }
                )
                NoteBodyTextField(
                    placeholder = stringResource(R.string.note_screen_body_placeholder),
                    value = viewModel.note.body,
                    focusRequester = focusRequester,
                    onValueChange = {
                        viewModel.note = viewModel.note.copy(body = it)
                    }
                )
            }
        }
    )
}

@Composable
fun NoteBodyTextField(placeholder: String, value: String, focusRequester: FocusRequester, onValueChange: (String) -> Unit) {
    BasicTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .clearFocusOnKeyboardDismiss()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = TextStyle(
            fontSize = 15.sp,
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
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
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