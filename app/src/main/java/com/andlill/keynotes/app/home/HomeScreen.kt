package com.andlill.keynotes.app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andlill.keynotes.app.Screen
import com.andlill.keynotes.model.Note
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navigation: NavController, viewModel: HomeViewModel = viewModel()) {

    // lazy list state too?
    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val notes = viewModel.getNotes().collectAsState(initial = emptyList())

    Scaffold(
        scaffoldState = state,
        drawerContent = {
            Spacer(modifier = Modifier
                .background(Color.Transparent)
                .statusBarsHeight()
                .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DrawerContent")
            }
        },
        topBar = {
            Column {
                Spacer(modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .statusBarsHeight()
                    .fillMaxWidth()
                )
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 0.dp,
                    title = {
                        Text(text = "Notes", style = MaterialTheme.typography.h3)
                    },
                    navigationIcon = {
                         IconButton(onClick = {
                            scope.launch {
                                state.drawerState.open()
                            }
                         }) {
                             Icon(Icons.Default.Menu, null)
                         }
                    },
                    actions = {
                        IconButton(onClick = {
                            /*TODO*/
                        }) {
                            Icon(Icons.Default.Search, null)
                        }
                    }
                )
            }
        },
        bottomBar = {
            Column {
                OutlinedButton(modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .fillMaxWidth()
                    .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        contentColor = MaterialTheme.colors.onSurface,
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        hoveredElevation = 0.dp,
                        focusedElevation = 0.dp,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        navigation.navigate(Screen.NoteScreen.route) {
                            // To avoid multiple copies of same destination in backstack.
                            launchSingleTop = true
                        }
                    }) {
                    Icon(Icons.Default.Add, null)
                    Text(modifier = Modifier.padding(start = 4.dp), text = "NEW NOTE")
                }
                Spacer(
                    modifier = Modifier
                        .navigationBarsHeight()
                        .fillMaxWidth()
                )
            }
        },
        content = { innerPadding ->
            LazyColumn(modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colors.surface)
                .fillMaxSize()
                .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notes.value) { note ->
                    NoteItem(note) {
                        navigation.navigate(Screen.NoteScreen.route + "/${note.id}") {
                            // To avoid multiple copies of same destination in backstack.
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun NoteItem(note: Note, callback: () -> Unit) {
    OutlinedButton(
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(note.color),
            contentColor = MaterialTheme.colors.onSurface,
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp,
        ),
        onClick = { callback() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (note.title.isNotEmpty())
                Text(text = note.title, style = MaterialTheme.typography.h3, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (note.title.isNotEmpty() && note.body.isNotEmpty())
                Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
            if (note.body.isNotEmpty()) {
                Text(text = note.body, style = MaterialTheme.typography.body2, maxLines = 6,overflow = TextOverflow.Ellipsis)
            }
        }
    }
}