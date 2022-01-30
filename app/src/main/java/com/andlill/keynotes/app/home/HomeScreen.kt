package com.andlill.keynotes.app.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andlill.keynotes.app.Screen
import com.andlill.keynotes.model.Note
import com.andlill.keynotes.ui.components.MenuIconButton
import com.andlill.keynotes.ui.components.util.clearFocusOnKeyboardDismiss
import com.andlill.keynotes.ui.theme.DarkNoteColors
import com.andlill.keynotes.ui.theme.LightNoteColors
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun HomeScreen(navigation: NavController, viewModel: HomeViewModel = viewModel()) {

    val searchQuery = rememberSaveable { mutableStateOf("") }
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
                        SearchBar(searchQuery)
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
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface.copy(0.6f))
                    Text(
                        text = "NEW NOTE",
                        color = MaterialTheme.colors.onSurface.copy(0.6f),
                        modifier = Modifier.padding(start = 4.dp))
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
                // Filter list by query if necessary.
                val list = when {
                    searchQuery.value.isEmpty() -> notes.value
                    else -> notes.value.filter { it.title.contains(searchQuery.value, ignoreCase = true) || it.body.contains(searchQuery.value, ignoreCase = true) }
                }
                items(list) { note ->
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
fun SearchBar(query: MutableState<String>) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        modifier = Modifier
            .clearFocusOnKeyboardDismiss()
            .padding(end = 4.dp)
            .background(
                color = MaterialTheme.colors.onSurface.copy(0.05f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = Dp.Hairline,
                color = MaterialTheme.colors.onSurface.copy(0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .fillMaxWidth()
            .height(24.dp),
        value = query.value,
        onValueChange = {
            query.value = it
        },
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colors.onSurface
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        decorationBox = { innerTextField ->
            Box {
                if (query.value.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart),
                        text = "Search Notes",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colors.onSurface.copy(0.6f)
                        )
                    )
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterEnd),
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface.copy(0.6f)
                    )
                }
                if (query.value.isNotEmpty()) {
                    IconButton(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterEnd),
                        onClick = {
                            query.value = ""
                            focusManager.clearFocus()
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                }
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun NoteItem(note: Note, callback: () -> Unit) {
    val noteColor = when {
        isSystemInDarkTheme() -> DarkNoteColors[note.color]
        else -> LightNoteColors[note.color]
    }

    OutlinedButton(
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = animateColorAsState(noteColor).value,
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
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp))
            if (note.body.isNotEmpty()) {
                Text(text = note.body, style = MaterialTheme.typography.body2, maxLines = 10,overflow = TextOverflow.Ellipsis)
            }
        }
    }
}