package com.andlill.keynotes.app.note

import android.app.Application
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlill.keynotes.R
import com.andlill.keynotes.app.AppState
import com.andlill.keynotes.app.note.composables.*
import com.andlill.keynotes.ui.shared.button.MenuIconButton
import com.andlill.keynotes.ui.shared.util.LifecycleEventHandler
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors
import com.andlill.keynotes.utils.DialogUtils

@Composable
fun NoteScreen(appState: AppState, noteId: Int) {
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(LocalContext.current.applicationContext as Application, noteId))

    val colorDialogState = remember { mutableStateOf(false) }
    val labelDialogState = remember { mutableStateOf(false) }
    val reminderDialogState = remember { mutableStateOf(false) }
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
                            appState.navigation.navigateUp()
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
                                appState.showSnackbar(context.resources.getString(R.string.note_screen_message_note_trash), SnackbarDuration.Short)
                                appState.navigation.navigateUp()
                            })
                            ThemeDropDown(state = colorDialogState, selectedColor = viewModel.color, onClick = {
                                viewModel.onChangeColor(it)
                            })
                        }
                        else {
                            MenuIconButton(icon = Icons.Outlined.Restore, color = MaterialTheme.colors.onSurface, onClick = {
                                viewModel.onRestore()
                                appState.showSnackbar(context.resources.getString(R.string.note_screen_message_note_restored), SnackbarDuration.Short)
                                appState.navigation.navigateUp()
                            })
                            MenuIconButton(icon = Icons.Outlined.DeleteForever, color = MaterialTheme.colors.onSurface, onClick = {
                                DialogUtils.showConfirmDialog(
                                    text = context.resources.getString(R.string.note_screen_dialog_confirm_note_delete),
                                    onConfirm = {
                                        viewModel.onDeletePermanently()
                                        appState.showSnackbar(context.resources.getString(R.string.note_screen_message_note_deleted), SnackbarDuration.Short)
                                        appState.navigation.navigateUp()
                                    }
                                )
                            })
                        }
                    }
                )
            }
        },
        content = { innerPadding ->
            Box(modifier = Modifier
                .padding(innerPadding)
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize()
                .clickable(interactionSource = interactionSource, indication = null) {
                    viewModel.setBodySelectionEnd()
                    focusRequester.requestFocus()
                }) {
                Column {
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
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp)
                    .align(Alignment.BottomCenter)) {
                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.BottomCenter),
                        color = MaterialTheme.colors.onSurface.copy(0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = viewModel.statusText,
                    )
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        enabled = viewModel.undoList.isNotEmpty(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.onSurface,
                            disabledContentColor = MaterialTheme.colors.onSurface.copy(0.4f)
                        ),
                        onClick = {
                            viewModel.onUndo()
                        }) {
                        Icon(
                            imageVector = Icons.Outlined.Undo,
                            contentDescription = null,
                        )
                    }
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        enabled = viewModel.redoList.isNotEmpty(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.onSurface,
                            disabledContentColor = MaterialTheme.colors.onSurface.copy(0.4f)
                        ),
                        onClick = {
                            viewModel.onRedo()
                        }) {
                        Icon(
                            imageVector = Icons.Outlined.Redo,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    )
}