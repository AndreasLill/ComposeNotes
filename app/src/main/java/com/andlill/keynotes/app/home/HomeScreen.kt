package com.andlill.keynotes.app.home

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlill.keynotes.app.AppState
import com.andlill.keynotes.app.Screen
import com.andlill.keynotes.app.home.composables.Drawer
import com.andlill.keynotes.app.home.composables.NoteItem
import com.andlill.keynotes.app.home.composables.SearchBar
import com.andlill.keynotes.ui.shared.button.MenuIconButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(appState: AppState) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(LocalContext.current.applicationContext as Application))
    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.drawerState.isClosed) {
        viewModel.labelEditMode = false
    }
    BackHandler(state.drawerState.isOpen) {
        if (viewModel.labelEditMode) {
            viewModel.labelEditMode = false
        }
        else {
            scope.launch {
                state.drawerState.close()
            }
        }
    }

    Scaffold(
        scaffoldState = state,
        snackbarHost = { state.snackbarHostState },
        drawerScrimColor = Color.Black.copy(0.32f),
        drawerContent = {
            Drawer(
                labels = viewModel.labels,
                labelEditMode = viewModel.labelEditMode,
                onLabelEditMode = {
                    viewModel.labelEditMode = it
                },
                onSelectItem = {
                    viewModel.selectedName = it
                },
                onAddLabel = {
                    viewModel.onAddLabel(it)
                },
                onUpdateLabel = {
                    viewModel.onUpdateLabel(it)
                },
                onDeleteLabel = {
                    viewModel.onDeleteLabel(it)
                },
                onFilterTrash = {
                    viewModel.onFilterTrash(it)
                },
                onFilterLabel = {
                    viewModel.onFilterLabel(it)
                },
                onClose = {
                    scope.launch {
                        state.drawerState.close()
                    }
                }
            )
        },
        topBar = {
            Column {
                Spacer(modifier = Modifier.statusBarsPadding())
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 0.dp,
                    title = {
                        SearchBar(
                            query = viewModel.query,
                            placeholder = viewModel.selectedName,
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
            }
        },
        content = { innerPadding ->
            LazyColumn(modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .background(MaterialTheme.colors.surface)
                .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 56.dp)) {
                items(items = viewModel.notes, key = { it.note.id }) { note ->
                    NoteItem(note) {
                        appState.navigation.navigate("${Screen.NoteScreen.route}/${note.note.id}") {
                            // To avoid multiple copies of same destination in backstack.
                            launchSingleTop = true
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (!WindowInsets.isImeVisible && !viewModel.filterTrash) {
                FloatingActionButton(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .size(64.dp),
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
                        viewModel.onCreateNote { id ->
                            viewModel.filterLabel?.let { label ->
                                // Add label to new note if label is selected.
                                viewModel.onAddNoteLabel(id, label.id)
                            }
                            appState.navigation.navigate("${Screen.NoteScreen.route}/$id") {
                                // To avoid multiple copies of same destination in backstack.
                                launchSingleTop = true
                            }
                        }
                    },
                    content = {
                        Icon(modifier = Modifier.size(28.dp), imageVector = Icons.Outlined.Add, contentDescription = null)
                    }
                )
            }
        }
    )
}