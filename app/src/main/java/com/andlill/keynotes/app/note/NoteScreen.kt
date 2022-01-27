package com.andlill.keynotes.app.note

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight

@Composable
fun NoteScreen(navigation: NavController, viewModel: NoteViewModel = viewModel(), noteId: Int = -1) {

    //val focusRequester = remember { FocusRequester() }
    val localFocusManager = LocalFocusManager.current
    val bgColor = animateColorAsState(Color(viewModel.color))

    LaunchedEffect(Unit) {
        viewModel.loadNote(noteId)
    //focusRequester.requestFocus()
    }
    BackHandler {
        // Save note on back navigation.
        viewModel.saveNote()
        navigation.navigateUp()
    }

    Scaffold(
        backgroundColor = bgColor.value,
        topBar = {
            Column {
                Spacer(modifier = Modifier
                    .statusBarsHeight()
                    .fillMaxWidth()
                )
                TopAppBar(
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    title = {
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            // Save note on back navigation.
                            viewModel.saveNote()
                            navigation.navigateUp()
                        }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            // TODO: Add selectable colors for font, background, etc.
                            viewModel.randomColor()
                        }) {
                            Icon(Icons.Default.Palette, null)
                        }
                        IconButton(onClick = {
                            // TODO: Change delete to move to trash.
                            viewModel.deleteNote()
                            navigation.navigateUp()
                        }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                )
            }
        },
        bottomBar = {
            Spacer(modifier = Modifier
                .navigationBarsHeight()
                .fillMaxWidth()
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .background(Color.Transparent)
                .fillMaxSize()) {
                // Note Title
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    shape = RectangleShape,
                    textStyle = MaterialTheme.typography.h2,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        textColor = MaterialTheme.colors.onSurface,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text("Title") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            localFocusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    value = viewModel.title,
                    onValueChange = {
                        viewModel.title = it
                    }
                )
                // Note Body
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.Transparent),
                    shape = RectangleShape,
                    textStyle = MaterialTheme.typography.body1,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        textColor = MaterialTheme.colors.onSurface,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    placeholder = { Text("Body") },
                    value = viewModel.body,
                    onValueChange = {
                        viewModel.body = it
                    }
                )
            }
        }
    )
}