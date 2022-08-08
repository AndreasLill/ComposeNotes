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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andlill.keynotes.R
import com.andlill.keynotes.app.Screen
import com.andlill.keynotes.app.home.composables.Drawer
import com.andlill.keynotes.app.home.composables.NoteItem
import com.andlill.keynotes.app.home.composables.SearchBar
import com.andlill.keynotes.ui.shared.button.MenuIconButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(navigation: NavController) {
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
                state = state.drawerState,
                viewModel = viewModel,
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
                            placeholder = viewModel.drawerSelectedItemName,
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
        bottomBar = {
            // Hide bottom bar if IME is showing.
            if (!WindowInsets.isImeVisible) {
                Box(modifier = Modifier
                    .navigationBarsPadding()
                    .padding(8.dp)
                    .fillMaxWidth()) {
                    if (!viewModel.filterTrash) {
                        OutlinedButton(modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.onSurface.copy(0.1f),
                                contentColor = MaterialTheme.colors.onSurface,
                            ),
                            shape = RoundedCornerShape(32.dp),
                            onClick = {
                                viewModel.onCreateNote { id ->
                                    viewModel.filterLabel?.let { label ->
                                        // Add label to new note if label is selected.
                                        viewModel.onAddNoteLabel(id, label.id)
                                    }
                                    navigation.navigate("${Screen.NoteScreen.route}/$id") {
                                        // To avoid multiple copies of same destination in backstack.
                                        launchSingleTop = true
                                    }
                                }
                            }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                    else {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(R.string.home_screen_text_trash),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colors.onSurface.copy(0.6f)
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            LazyColumn(modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .background(MaterialTheme.colors.surface)
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.notes) { note ->
                    NoteItem(note) {
                        navigation.navigate("${Screen.NoteScreen.route}/${note.note.id}") {
                            // To avoid multiple copies of same destination in backstack.
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    )
}