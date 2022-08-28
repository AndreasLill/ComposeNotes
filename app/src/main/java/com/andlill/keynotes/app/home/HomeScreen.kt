package com.andlill.keynotes.app.home

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlill.keynotes.R
import com.andlill.keynotes.app.AppState
import com.andlill.keynotes.app.Screen
import com.andlill.keynotes.app.home.composables.Drawer
import com.andlill.keynotes.app.home.composables.NoteItem
import com.andlill.keynotes.app.home.composables.SearchBar
import com.andlill.keynotes.model.NoteFilter
import com.andlill.keynotes.ui.shared.button.MenuIconButton
import com.andlill.keynotes.ui.shared.label.NoteLabel
import com.andlill.keynotes.ui.shared.modifier.orientationModifiers
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(appState: AppState) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(LocalContext.current.applicationContext as Application))
    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    BackHandler(state.drawerState.isOpen) {
        scope.launch {
            state.drawerState.close()
        }
    }

    Scaffold(
        scaffoldState = state,
        drawerScrimColor = Color.Black.copy(0.32f),
        drawerContent = {
            Drawer(
                labels = viewModel.labels,
                onFilter = {
                    viewModel.onFilter(it)
                },
                onAddLabel = {
                    viewModel.onAddLabel(it)
                },
                onEditLabels = {
                    appState.navigation.navigate(Screen.LabelScreen.route()) {
                        // To avoid multiple copies of same destination in backstack.
                        launchSingleTop = true
                    }
                },
                onClose = {
                    scope.launch {
                        state.drawerState.close()
                    }
                }
            )
        },
        topBar = {
            TopAppBar(
                modifier = Modifier.orientationModifiers(
                    portrait = Modifier.statusBarsPadding(),
                    landscape = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
                title = {
                    SearchBar(
                        query = viewModel.query,
                        placeholder = viewModel.filter.name,
                        onValueChange = {
                            viewModel.onQuery(it)
                    })
                },
                navigationIcon = {
                    MenuIconButton(icon = Icons.Filled.Menu, color = MaterialTheme.colors.onSurface) {
                        scope.launch {
                            state.drawerState.open()
                        }
                    }
                },
                actions = {
                }
            )
        },
        content = { innerPadding ->
            Box(modifier = Modifier
                .padding(innerPadding)
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize())  {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)) {
                    items(items = viewModel.notes, key = { it.note.id }) { note ->
                        NoteItem(note) {
                            appState.navigation.navigate(Screen.NoteScreen.route(noteId = note.note.id)) {
                                // To avoid multiple copies of same destination in backstack.
                                launchSingleTop = true
                            }
                        }
                    }
                }
                // Background hint.
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (viewModel.notes.isEmpty()) {
                        when(viewModel.filter.type) {
                            NoteFilter.Type.Reminders -> {
                                Icon(
                                    modifier = Modifier.size(64.dp),
                                    imageVector = Icons.Outlined.Alarm,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.onSurface.copy(0.2f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.home_screen_status_text_empty_reminders),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colors.onSurface.copy(0.7f)
                                )
                            }
                            NoteFilter.Type.Trash -> {
                                Icon(
                                    modifier = Modifier.size(64.dp),
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.onSurface.copy(0.2f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.home_screen_status_text_empty_trash),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colors.onSurface.copy(0.7f)
                                )
                            }
                            NoteFilter.Type.Label -> {
                                viewModel.filter.label?.let { label ->
                                    NoteLabel(modifier = Modifier.scale(1.4f) ,icon = Icons.Outlined.Label, text = label.value, color = MaterialTheme.colors.onSurface.copy(0.08f))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = stringResource(R.string.home_screen_status_text_empty_label),
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colors.onSurface.copy(0.7f)
                                    )
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (!WindowInsets.isImeVisible && viewModel.filter.type != NoteFilter.Type.Trash) {
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 4.dp,
                        hoveredElevation = 4.dp,
                        focusedElevation = 4.dp
                    ),
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.primary,
                    onClick = {
                        viewModel.onCreateNote { noteId ->
                            viewModel.filter.label?.let { label ->
                                // Add label to new note if label is selected.
                                viewModel.onAddNoteLabel(noteId, label.id)
                            }
                            appState.navigation.navigate(Screen.NoteScreen.route(noteId = noteId)) {
                                // To avoid multiple copies of same destination in backstack.
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.home_Screen_button_add_note),
                            fontSize = 15.sp,
                            letterSpacing = 0.sp
                        )
                    }
                )
            }
        }
    )
}