package com.andlill.keynotes.app.note

import android.app.Application
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andlill.keynotes.R
import com.andlill.keynotes.app.note.composables.*
import com.andlill.keynotes.ui.shared.util.LifecycleEventHandler
import com.andlill.keynotes.ui.shared.button.MenuIconButton
import com.andlill.keynotes.ui.shared.dialog.ConfirmDialog
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors
import com.andlill.keynotes.utils.TimeUtils.daysBetween
import com.andlill.keynotes.utils.TimeUtils.toDateString

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteScreen(navigation: NavController, noteId: Int) {
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(LocalContext.current.applicationContext as Application, noteId))

    val themeMenuState = remember { mutableStateOf(false) }
    val labelDialogState = remember { mutableStateOf(false) }
    val reminderDialogState = remember { mutableStateOf(false) }
    val confirmDialogState = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val pinIcon = if (viewModel.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin
    val reminderIcon = if (viewModel.reminder != null) Icons.Filled.Notifications else Icons.Outlined.Notifications
    val noteColor = if (isSystemInDarkTheme()) DarkNoteColors[viewModel.color] else LightNoteColors[viewModel.color]

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
            Column(modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()) {
                TopAppBar(
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    title = {
                    },
                    navigationIcon = {
                        MenuIconButton(icon = Icons.Filled.ArrowBack, color = MaterialTheme.colors.onSurface, onClick = {
                            navigation.navigateUp()
                        })
                    },
                    actions = {
                        if (!viewModel.isDeleted) {
                            MenuIconButton(icon = pinIcon, color = MaterialTheme.colors.onSurface, onClick = {
                                viewModel.onTogglePin()
                            })
                            MenuIconButton(icon = reminderIcon, color = MaterialTheme.colors.onSurface, onClick = {
                                reminderDialogState.value = true
                            })
                            ReminderDialog(state = reminderDialogState, reminderTime = viewModel.reminder, onClick = {
                                viewModel.onToggleReminder(it)
                            })
                            MenuIconButton(icon = Icons.Outlined.Label, color = MaterialTheme.colors.onSurface, onClick = {
                                labelDialogState.value = true
                            })
                            LabelDialog(state = labelDialogState, noteLabels = viewModel.noteLabels, labels = viewModel.allLabels, onClick = {
                                viewModel.onToggleLabel(it)
                            })
                            MenuIconButton(icon = Icons.Outlined.Palette, color = MaterialTheme.colors.onSurface, onClick = {
                                themeMenuState.value = true
                            })
                            MenuIconButton(icon = Icons.Outlined.Delete, color = MaterialTheme.colors.onSurface, onClick = {
                                viewModel.onDeleteNote()
                                navigation.navigateUp()
                            })
                            ThemeDropDown(state = themeMenuState, onClick = {
                                viewModel.onChangeColor(it)
                            })
                        }
                        else {
                            MenuIconButton(icon = Icons.Outlined.Restore, color = MaterialTheme.colors.onSurface, onClick = {
                                viewModel.onRestore()
                            })
                            MenuIconButton(icon = Icons.Outlined.DeleteForever, color = MaterialTheme.colors.onSurface, onClick = {
                                confirmDialogState.value = true
                            })
                            ConfirmDialog(state = confirmDialogState, body = stringResource(R.string.note_screen_dialog_confirm_text_body), onConfirm = {
                                viewModel.onDeletePermanently()
                                navigation.navigateUp()
                            })
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!WindowInsets.isImeVisible) {
                viewModel.modified?.let { modified ->
                    val daysBetween = modified.daysBetween()
                    val modifiedText = when {
                        daysBetween == 0 -> String.format("%s, %s", stringResource(R.string.date_today), modified.toDateString("HH:mm"))
                        daysBetween == -1 -> String.format("%s, %s", stringResource(R.string.date_yesterday), modified.toDateString("HH:mm"))
                        daysBetween < -365 -> modified.toDateString("d MMMM YYYY, HH:mm")
                        else -> modified.toDateString("d MMMM, HH:mm")
                    }
                    Column(modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.onSurface.copy(0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            text = modifiedText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    viewModel.setBodySelectionEnd()
                    focusRequester.requestFocus()
                }) {
                NoteTitleTextField(
                    placeholder = stringResource(R.string.note_screen_placeholder_title),
                    state = viewModel.titleText,
                    readOnly = viewModel.isDeleted,
                    onValueChange = {
                        viewModel.onChangeTitle(it)
                    }
                )
                NoteBodyTextField(
                    placeholder = stringResource(R.string.note_screen_placeholder_body),
                    state = viewModel.bodyText,
                    readOnly = viewModel.isDeleted,
                    focusRequester = focusRequester,
                    onValueChange = {
                        viewModel.onChangeBody(it)
                    }
                )
            }
        }
    )
}