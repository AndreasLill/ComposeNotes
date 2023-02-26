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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
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
import com.andlill.composenotes.model.NoteCheckBox
import com.andlill.composenotes.ui.shared.button.MenuIconButton
import com.andlill.composenotes.ui.shared.util.LifecycleEventHandler
import com.andlill.composenotes.utils.ColorUtils.darken
import com.andlill.composenotes.utils.DialogUtils
import com.andlill.composenotes.utils.TimeUtils.daysBetween
import com.andlill.composenotes.utils.TimeUtils.toLocalDateTime
import com.andlill.composenotes.utils.TimeUtils.toSimpleDateString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun NoteScreen(appState: AppState, noteId: Int) {
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(LocalContext.current.applicationContext as Application, noteId))

    val optionsDropDownState = remember { mutableStateOf(false) }
    val colorDialogState = remember { mutableStateOf(false) }
    val reminderDialogState = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequesterBody = remember { FocusRequester() }
    val focusRequesterCheckBox = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val surfaceColor = MaterialTheme.colorScheme.surface
    val isDarkTheme = isSystemInDarkTheme()

    val checkBoxesSortedList = remember {
        derivedStateOf {
            viewModel.checkBoxes.sortedWith(compareBy<NoteCheckBox> { it.checked }.thenBy { it.order })
        }
    }

    val statusText = remember {
        derivedStateOf {
            var text = ""
            viewModel.modified?.let {
                text = it.toLocalDateTime().toSimpleDateString(context)
            }
            viewModel.deletion?.let {
                val deletion = it.toLocalDateTime()
                val current = LocalDateTime.now()
                text = when (deletion.daysBetween(current)) {
                    0 -> {
                        context.getString(R.string.note_screen_status_text_deletion_today)
                    }
                    1 -> {
                        context.getString(R.string.note_screen_status_text_deletion_tomorrow)
                    }
                    else -> {
                        String.format(context.getString(R.string.note_screen_status_text_deletion_days), deletion.daysBetween(current))
                    }
                }
            }
            text
        }
    }

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
                colors = topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {},
                navigationIcon = {
                    MenuIconButton(
                        icon = Icons.Filled.ArrowBack,
                        onClick = appState.navigation::navigateUp
                    )
                },
                actions = {
                    if (viewModel.deletion == null) {
                        Box {
                            MenuIconButton(
                                icon = if (viewModel.pinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                onClick = viewModel::onTogglePin
                            )
                        }
                        Box {
                            MenuIconButton(
                                icon = if (viewModel.reminder != null) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                onClick = {
                                    reminderDialogState.value = true
                                }
                            )
                            ReminderDialog(
                                state = reminderDialogState,
                                initialDateTime = viewModel.reminder?.toLocalDateTime(),
                                onClick = {
                                    if (it != null) {
                                        val dateStr = it.toSimpleDateString(context)
                                        viewModel.onSetReminder(it)
                                        appState.showSnackbar(String.format(context.resources.getString(R.string.note_screen_message_reminder_set), dateStr), SnackbarDuration.Short)
                                    }
                                    else {
                                        viewModel.onCancelReminder()
                                        appState.showSnackbar(context.resources.getString(R.string.note_screen_message_reminder_cancel), SnackbarDuration.Short)
                                    }
                                }
                            )
                        }
                        Box {
                            MenuIconButton(
                                icon = Icons.Outlined.Palette,
                                onClick = {
                                    colorDialogState.value = true
                                }
                            )
                            ColorDialog(
                                state = colorDialogState,
                                selectedColor = viewModel.color,
                                onClick = viewModel::onChangeColor
                            )
                        }
                        Box {
                            MenuIconButton(
                                icon = Icons.Filled.MoreVert,
                                onClick = {
                                    optionsDropDownState.value = true
                                }
                            )
                            OptionsDropDownMenu(
                                state = optionsDropDownState,
                                isCheckBoxesEmpty = checkBoxesSortedList.value.isEmpty(),
                                onClick = {
                                    optionsDropDownState.value = false
                                    when (it) {
                                        NoteOption.Checkboxes -> {
                                            viewModel.onConvertCheckBoxes()
                                        }
                                        NoteOption.Labels -> {
                                            appState.navigation.navigate(Screen.LabelScreen.route(noteId = viewModel.id)) {
                                                // To avoid multiple copies of same destination in backstack.
                                                launchSingleTop = true
                                            }
                                        }
                                        NoteOption.Delete -> {
                                            viewModel.onDeleteNote()
                                            appState.showSnackbar(context.resources.getString(R.string.note_screen_message_note_trash), SnackbarDuration.Short)
                                            appState.navigation.navigateUp()
                                        }
                                    }
                                }
                            )
                        }
                    }
                    else {
                        MenuIconButton(
                            icon = Icons.Outlined.Restore,
                            onClick = {
                                viewModel.onRestore()
                                appState.showSnackbar(context.resources.getString(R.string.note_screen_message_note_restored), SnackbarDuration.Short)
                                appState.navigation.navigateUp()
                            }
                        )
                        MenuIconButton(
                            icon = Icons.Outlined.DeleteForever,
                            onClick = {
                                DialogUtils.showConfirmDialog(
                                    text = context.resources.getString(R.string.note_screen_dialog_confirm_note_delete),
                                    onConfirm = {
                                        viewModel.onDeletePermanently()
                                        appState.showSnackbar(context.resources.getString(R.string.note_screen_message_note_deleted), SnackbarDuration.Short)
                                        appState.navigation.navigateUp()
                                    }
                                )
                            }
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Box(modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
                .clickable(interactionSource = interactionSource, indication = null) {
                    if (viewModel.checkBoxes.isEmpty()) {
                        // Request focus on text body.
                        viewModel.setBodySelectionEnd()
                        focusRequesterBody.requestFocus()
                    } else {
                        // Request focus on check box.
                        focusRequesterCheckBox.requestFocus()
                    }
                }) {
                Column(modifier = Modifier.imePadding()) {
                    NoteTitleTextField(
                        state = viewModel.title,
                        readOnly = (viewModel.deletion != null),
                        onValueChange = viewModel::onChangeTitle
                    )
                    if (viewModel.checkBoxes.isEmpty()) {
                        NoteBodyTextField(
                            state = viewModel.body,
                            readOnly = (viewModel.deletion != null),
                            focusRequester = focusRequesterBody,
                            onValueChange = viewModel::onChangeBody
                        )
                    }
                    else {
                        // Bugged with no work around yet: https://issuetracker.google.com/issues/179203700
                        LazyColumn(modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)) {
                            itemsIndexed(items = checkBoxesSortedList.value, key = { _, checkBox -> checkBox.id }) { index, checkBox ->
                                NoteCheckBoxItem(
                                    modifier = Modifier.animateItemPlacement(),
                                    focusRequester = if (checkBoxesSortedList.value.size == index + 1) focusRequesterCheckBox else null,
                                    checkBox = checkBox,
                                    onUpdate = viewModel::onEditCheckBox,
                                    onDelete = {
                                        if (index > 0)
                                            focusManager.moveFocus(FocusDirection.Up)
                                        viewModel.onDeleteCheckBox(checkBox.id)
                                    },
                                    onKeyboardNext = {
                                        // Create a new checkbox if this is the last item.
                                        if (checkBoxesSortedList.value.size == index + 1) {
                                            viewModel.onCreateCheckBox()
                                            scope.launch {
                                                delay(50)
                                                focusManager.moveFocus(FocusDirection.Down)
                                            }
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
                        text = statusText.value,
                    )
                }
            }
        }
    )
}