package com.andlill.composenotes.app.home

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlill.composenotes.R
import com.andlill.composenotes.app.AppState
import com.andlill.composenotes.app.Screen
import com.andlill.composenotes.app.home.composables.Drawer
import com.andlill.composenotes.app.home.composables.NoteItem
import com.andlill.composenotes.app.home.composables.SearchBar
import com.andlill.composenotes.model.NoteFilter
import com.andlill.composenotes.model.NoteWrapper
import com.andlill.composenotes.ui.shared.button.MenuIconButton
import com.andlill.composenotes.ui.shared.label.NoteLabel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(appState: AppState) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(LocalContext.current.applicationContext as Application))
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val layoutDirection = LocalLayoutDirection.current

    val notesFiltered = remember {
        derivedStateOf {
            // First filter using the note filter.
            val notes = when(viewModel.filter.type) {
                NoteFilter.Type.AllNotes -> {
                    viewModel.notes
                        .filter { it.note.deletion == null && it.note.modified != null }
                        .sortedWith(compareByDescending<NoteWrapper> { it.note.pinned }.thenByDescending { it.note.created })
                }
                NoteFilter.Type.Reminders -> {
                    viewModel.notes
                        .filter { it.note.reminder != null && it.note.modified != null }
                        .sortedWith(compareBy { it.note.reminder })
                }
                NoteFilter.Type.Trash -> {
                    viewModel.notes
                        .filter { it.note.deletion != null && it.note.modified != null }
                        .sortedWith(compareByDescending { it.note.deletion })
                }
                NoteFilter.Type.Label -> {
                    viewModel.notes
                        .filter { it.labels.contains(viewModel.filter.label) && it.note.deletion == null && it.note.modified != null }
                        .sortedWith(compareByDescending<NoteWrapper> { it.note.pinned }.thenByDescending { it.note.created })
                }
            }
            // Then filter using query if any.
            notes.filter {
                if (viewModel.query.isNotBlank()) {
                    it.note.title.contains(viewModel.query, ignoreCase = true) ||
                    it.note.body.contains(viewModel.query, ignoreCase = true) ||
                    it.checkBoxes.any { checkBox -> checkBox.text.contains(viewModel.query, ignoreCase = true) }
                }
                else
                    true
            }
        }
    }

    val labelsSorted = remember {
        derivedStateOf {
            viewModel.labels.sortedBy { it.value.lowercase() }
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        focusManager.clearFocus()
    }

    BackHandler(drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                content = {
                    Drawer(
                        labels = labelsSorted.value,
                        onFilter = viewModel::onFilter,
                        onAddLabel = viewModel::onAddLabel,
                        onEditLabels = {
                            appState.navigation.navigate(Screen.LabelScreen.route()) {
                                // To avoid multiple copies of same destination in backstack.
                                launchSingleTop = true
                            }
                        },
                        onClose = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            )
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            SearchBar(
                                query = viewModel.query,
                                placeholder = viewModel.filter.name,
                                onValueChange = viewModel::onQuery
                            )
                        },
                        navigationIcon = {
                            MenuIconButton(
                                icon = Icons.Outlined.Menu,
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            )
                        },
                        actions = {
                            MenuIconButton(
                                icon = if (viewModel.userPreferences.isGridView) Icons.Outlined.GridView else Icons.Outlined.ViewAgenda,
                                onClick = viewModel::onChangeView
                            )
                        }
                    )
                },
                content = { innerPadding ->
                    Box(modifier = Modifier
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            start = innerPadding.calculateStartPadding(layoutDirection),
                            end = innerPadding.calculateEndPadding(layoutDirection)
                        )
                        .fillMaxSize())  {
                        if (viewModel.userPreferences.isGridView) {
                            LazyVerticalStaggeredGrid(
                                modifier = Modifier.fillMaxSize(),
                                verticalItemSpacing = 8.dp,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                columns = StaggeredGridCells.Fixed(2),
                                contentPadding = PaddingValues(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = innerPadding.calculateBottomPadding() + 8.dp
                                )) {
                                items(items = notesFiltered.value, key = { it.note.id }) { note ->
                                    NoteItem(note, viewModel.userPreferences.notePreviewMaxLines) {
                                        appState.navigation.navigate(Screen.NoteScreen.route(noteId = note.note.id)) {
                                            // To avoid multiple copies of same destination in backstack.
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(8.dp)) {
                                items(items = notesFiltered.value, key = { it.note.id }) { note ->
                                    NoteItem(note, viewModel.userPreferences.notePreviewMaxLines) {
                                        appState.navigation.navigate(Screen.NoteScreen.route(noteId = note.note.id)) {
                                            // To avoid multiple copies of same destination in backstack.
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            }
                        }
                        // Background hint.
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            if (notesFiltered.value.isEmpty()) {
                                when(viewModel.filter.type) {
                                    NoteFilter.Type.Reminders -> {
                                        Icon(
                                            modifier = Modifier.size(64.dp),
                                            imageVector = Icons.Outlined.Notifications,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface.copy(0.2f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(R.string.home_screen_status_text_empty_reminders),
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                        )
                                    }
                                    NoteFilter.Type.Trash -> {
                                        Icon(
                                            modifier = Modifier.size(64.dp),
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface.copy(0.2f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(R.string.home_screen_status_text_empty_trash),
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                        )
                                    }
                                    NoteFilter.Type.Label -> {
                                        viewModel.filter.label?.let { label ->
                                            NoteLabel(modifier = Modifier.scale(1.4f) ,icon = Icons.Outlined.Label, text = label.value, color = MaterialTheme.colorScheme.onSurface.copy(0.08f))
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = stringResource(R.string.home_screen_status_text_empty_label),
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                            )
                                        }
                                    }
                                    else -> Unit
                                }
                            }
                        }
                    }
                },
                floatingActionButton = {
                    if (viewModel.filter.type != NoteFilter.Type.Trash) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                viewModel.onCreateNote { noteId ->
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
                                    fontSize = 15.sp
                                )
                            }
                        )
                    }
                }
            )
        }
    )
}