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
import androidx.compose.runtime.getValue
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
import com.andlill.keynotes.ui.shared.button.MenuIconButton
import com.andlill.keynotes.ui.shared.dialog.ConfirmDialog
import com.andlill.keynotes.ui.shared.util.LifecycleEventHandler
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteScreen(navController: NavController, noteId: Int) {
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(LocalContext.current.applicationContext as Application, noteId))

    val colorDialogState = remember { mutableStateOf(false) }
    val labelDialogState = remember { mutableStateOf(false) }
    val reminderDialogState = remember { mutableStateOf(false) }
    val confirmDialogState = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val pinIcon = if (viewModel.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin
    val reminderIcon = if (viewModel.reminder != null) Icons.Filled.Notifications else Icons.Outlined.Notifications
    val noteColor by animateColorAsState(
        if (isSystemInDarkTheme())
            DarkNoteColors[viewModel.color]
        else
            LightNoteColors[viewModel.color]
    )

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
        backgroundColor = noteColor,
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
                            navController.navigateUp()
                        })
                    },
                    actions = {
                        if (viewModel.deletion == null) {
                            MenuIconButton(icon = pinIcon, color = MaterialTheme.colors.onSurface, onClick = {
                                viewModel.onTogglePin()
                            })
                            MenuIconButton(icon = reminderIcon, color = MaterialTheme.colors.onSurface, onClick = {
                                reminderDialogState.value = true
                            })
                            ReminderDialog(state = reminderDialogState, reminderTime = viewModel.reminder, onClick = {
                                viewModel.onUpdateReminder(it)
                            })
                            MenuIconButton(icon = Icons.Outlined.Label, color = MaterialTheme.colors.onSurface, onClick = {
                                labelDialogState.value = true
                            })
                            LabelDialog(state = labelDialogState, noteLabels = viewModel.noteLabels, labels = viewModel.allLabels, onClick = {
                                viewModel.onToggleLabel(it)
                            })
                            MenuIconButton(icon = Icons.Outlined.Palette, color = MaterialTheme.colors.onSurface, onClick = {
                                colorDialogState.value = true
                            })
                            MenuIconButton(icon = Icons.Outlined.Delete, color = MaterialTheme.colors.onSurface, onClick = {
                                viewModel.onDeleteNote()
                                navController.previousBackStackEntry?.savedStateHandle?.set("KEY_MESSAGE", context.resources.getString(R.string.note_screen_message_note_trash))
                                navController.navigateUp()
                            })
                            ThemeDropDown(state = colorDialogState, selectedColor = viewModel.color, onClick = {
                                viewModel.onChangeColor(it)
                            })
                        }
                        else {
                            MenuIconButton(icon = Icons.Outlined.Restore, color = MaterialTheme.colors.onSurface, onClick = {
                                viewModel.onRestore()
                                navController.previousBackStackEntry?.savedStateHandle?.set("KEY_MESSAGE", context.resources.getString(R.string.note_screen_message_note_restored))
                                navController.navigateUp()
                            })
                            MenuIconButton(icon = Icons.Outlined.DeleteForever, color = MaterialTheme.colors.onSurface, onClick = {
                                confirmDialogState.value = true
                            })
                            ConfirmDialog(state = confirmDialogState, body = stringResource(R.string.note_screen_dialog_confirm_text_body), onConfirm = {
                                viewModel.onDeletePermanently()
                                navController.previousBackStackEntry?.savedStateHandle?.set("KEY_MESSAGE", context.resources.getString(R.string.note_screen_message_note_deleted))
                                navController.navigateUp()
                            })
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!WindowInsets.isImeVisible) {
                Column(modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colors.onSurface.copy(0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = viewModel.statusText,
                        textAlign = TextAlign.Center
                    )
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
                    readOnly = (viewModel.deletion != null),
                    onValueChange = {
                        viewModel.onChangeTitle(it)
                    }
                )
                NoteBodyTextField(
                    placeholder = stringResource(R.string.note_screen_placeholder_body),
                    state = viewModel.bodyText,
                    readOnly = (viewModel.deletion != null),
                    focusRequester = focusRequester,
                    onValueChange = {
                        viewModel.onChangeBody(it)
                    }
                )
            }
        }
    )
}