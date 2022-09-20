package com.andlill.composenotes.app.note

import android.app.Application
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.andlill.composenotes.R
import com.andlill.composenotes.app.AppState
import com.andlill.composenotes.app.Screen
import com.andlill.composenotes.app.note.composables.NoteBodyTextField
import com.andlill.composenotes.app.note.composables.NoteTitleTextField
import com.andlill.composenotes.app.note.composables.ReminderDialog
import com.andlill.composenotes.app.note.composables.ThemeDropDown
import com.andlill.composenotes.ui.shared.button.MenuIconButton
import com.andlill.composenotes.ui.shared.util.LifecycleEventHandler
import com.andlill.composenotes.utils.ColorUtils.darken
import com.andlill.composenotes.utils.DialogUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(appState: AppState, noteId: Int) {
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(LocalContext.current.applicationContext as Application, noteId))

    val colorDialogState = remember { mutableStateOf(false) }
    val reminderDialogState = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val surfaceColor = MaterialTheme.colorScheme.surface
    val isDarkTheme = isSystemInDarkTheme()

    val noteColor = remember(viewModel.color) {
        when {
            viewModel.color == 0 -> surfaceColor
            isDarkTheme -> Color(viewModel.color).darken()
            else -> Color(viewModel.color)
        }
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
        containerColor = animateColorAsState(noteColor).value,
        topBar = {
            SmallTopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {},
                navigationIcon = {
                    MenuIconButton(icon = Icons.Filled.ArrowBack, color = MaterialTheme.colorScheme.onSurface, onClick = {
                        appState.navigation.navigateUp()
                    })
                },
                actions = {
                    if (viewModel.deletion == null) {
                        MenuIconButton(icon = if (viewModel.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin, color = MaterialTheme.colorScheme.onSurface, onClick = {
                            viewModel.onTogglePin()
                        })
                        MenuIconButton(icon = if (viewModel.reminder != null) Icons.Filled.Notifications else Icons.Outlined.Notifications, color = MaterialTheme.colorScheme.onSurface, onClick = {
                            reminderDialogState.value = true
                        })
                        ReminderDialog(state = reminderDialogState, reminderTime = viewModel.reminder, onClick = {
                            viewModel.onUpdateReminder(it)
                        })
                        MenuIconButton(icon = Icons.Outlined.Label, color = MaterialTheme.colorScheme.onSurface, onClick = {
                            appState.navigation.navigate(Screen.LabelScreen.route(noteId = viewModel.id)) {
                                // To avoid multiple copies of same destination in backstack.
                                launchSingleTop = true
                            }
                        })
                        MenuIconButton(icon = Icons.Outlined.Palette, color = MaterialTheme.colorScheme.onSurface, onClick = {
                            colorDialogState.value = true
                        })
                        MenuIconButton(icon = Icons.Outlined.Delete, color = MaterialTheme.colorScheme.onSurface, onClick = {
                            viewModel.onDeleteNote()
                            appState.showSnackbar(context.resources.getString(R.string.note_screen_message_note_trash), SnackbarDuration.Short)
                            appState.navigation.navigateUp()
                        })
                        ThemeDropDown(state = colorDialogState, selectedColor = viewModel.color, onClick = {
                            viewModel.onChangeColor(it)
                        })
                    }
                    else {
                        MenuIconButton(icon = Icons.Outlined.Restore, color = MaterialTheme.colorScheme.onSurface, onClick = {
                            viewModel.onRestore()
                            appState.showSnackbar(context.resources.getString(R.string.note_screen_message_note_restored), SnackbarDuration.Short)
                            appState.navigation.navigateUp()
                        })
                        MenuIconButton(icon = Icons.Outlined.DeleteForever, color = MaterialTheme.colorScheme.onSurface, onClick = {
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
        },
        content = { innerPadding ->
            Box(modifier = Modifier
                .padding(innerPadding)
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
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = viewModel.statusText,
                    )
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        enabled = viewModel.undoList.isNotEmpty(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(0.4f)
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
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(0.4f)
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