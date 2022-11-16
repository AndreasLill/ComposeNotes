package com.andlill.composenotes.app.note

import android.app.Application
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlill.composenotes.R
import com.andlill.composenotes.app.AppState
import com.andlill.composenotes.app.Screen
import com.andlill.composenotes.app.note.composables.*
import com.andlill.composenotes.ui.shared.button.MenuIconButton
import com.andlill.composenotes.ui.shared.util.LifecycleEventHandler
import com.andlill.composenotes.utils.ColorUtils.darken
import com.andlill.composenotes.utils.DialogUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun NoteScreen(appState: AppState, noteId: Int) {
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(LocalContext.current.applicationContext as Application, noteId))

    val colorDialogState = remember { mutableStateOf(false) }
    val reminderDialogState = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
            TopAppBar(
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
                        MenuIconButton(icon = Icons.Outlined.CheckBox, color = MaterialTheme.colorScheme.onSurface, onClick = {
                            viewModel.onConvertCheckBoxes()
                        })
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
                .consumedWindowInsets(innerPadding)
                .fillMaxSize()
                .clickable(interactionSource = interactionSource, indication = null) {
                    if (viewModel.checkBoxes.isEmpty()) {
                        viewModel.setBodySelectionEnd()
                        focusRequester.requestFocus()
                    }
                    else {
                        // TODO: Focus last checkbox text field.
                    }
                }) {
                Column(modifier = Modifier.imePadding()) {
                    NoteTitleTextField(
                        state = viewModel.titleText,
                        readOnly = (viewModel.deletion != null),
                        onValueChange = viewModel::onChangeTitle
                    )
                    if (viewModel.checkBoxes.isEmpty()) {
                        NoteBodyTextField(
                            state = viewModel.bodyText,
                            readOnly = (viewModel.deletion != null),
                            focusRequester = focusRequester,
                            onValueChange = viewModel::onChangeBody
                        )
                    }
                    else {
                        // Bugged with no work around yet: https://issuetracker.google.com/issues/179203700
                        LazyColumn(modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)) {
                            itemsIndexed(items = viewModel.checkBoxes, key = { _, checkBox -> checkBox.id }) { index, checkBox ->
                                NoteCheckBoxItem(
                                    modifier = Modifier.animateItemPlacement(),
                                    checkBox = checkBox,
                                    onUpdate = viewModel::onEditCheckBox,
                                    onDelete = {
                                        if (index > 0) {
                                            focusManager.moveFocus(FocusDirection.Up)
                                            viewModel.onDeleteCheckBox(checkBox)
                                        }
                                    },
                                    onKeyboardNext = {
                                        // Create a new checkbox if this is the last item.
                                        if (index == viewModel.checkBoxes.size - 1) {
                                            viewModel.onCreateCheckBox(onDone = {
                                                scope.launch {
                                                    delay(50)
                                                    focusManager.moveFocus(FocusDirection.Down)
                                                }
                                            })
                                        }
                                        else {
                                            focusManager.moveFocus(FocusDirection.Down)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .align(Alignment.BottomCenter)) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = viewModel.statusText,
                    )
                    // TODO: Add undo/redo for body and check boxes.
                    /*
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
                     */
                }
            }
        }
    )
}