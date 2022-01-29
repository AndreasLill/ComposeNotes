package com.andlill.keynotes.app.note

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andlill.keynotes.ui.components.menu.MenuIconButton
import com.andlill.keynotes.ui.components.util.LifecycleEventHandler
import com.andlill.keynotes.ui.theme.LightNoteColors
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsHeight

@Composable
fun NoteScreen(navigation: NavController, viewModel: NoteViewModel = viewModel(), noteId: Int = -1) {

    val themeMenuState = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadNote(noteId)
    }
    // Handle lifecycle events.
    LifecycleEventHandler { event ->
        when (event) {
            // Save note on stop event.
            Lifecycle.Event.ON_STOP -> viewModel.saveNote()
            else -> {}
        }
    }
    Scaffold(
        backgroundColor = animateColorAsState(Color(viewModel.color)).value,
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
                        MenuTitleTextField(
                            placeholder = "No Title",
                            value = viewModel.title,
                            focusManager = LocalFocusManager.current,
                            onValueChange = {
                                viewModel.title = it
                            }
                        )
                    },
                    navigationIcon = {
                        MenuIconButton(icon = Icons.Filled.ArrowBack, color = MaterialTheme.colors.onSurface) {
                            // Save note on back navigation.
                            navigation.navigateUp()
                        }
                    },
                    actions = {
                        MenuIconButton(icon = Icons.Outlined.Palette, color = MaterialTheme.colors.onSurface) {
                            // TODO: Add selectable colors for font, background, etc.
                            //viewModel.randomColor()
                            themeMenuState.value = true
                        }
                        MenuIconButton(icon = Icons.Outlined.Delete, color = MaterialTheme.colors.onSurface) {
                            // TODO: Change delete to move to trash.
                            viewModel.deleteNote()
                            navigation.navigateUp()
                        }
                        // Note theme drop down menu.
                        ThemeDropDown(state = themeMenuState) {
                            viewModel.color = it
                        }
                    }
                )
            }
        },
        content = { innerPadding ->
            Column(modifier = Modifier
                .background(Color.Transparent)
                .navigationBarsWithImePadding()
                .padding(innerPadding)) {
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
                    placeholder = { Text("Empty") },
                    value = viewModel.body,
                    onValueChange = {
                        viewModel.body = it
                    }
                )
            }
        }
    )
}

@Composable
fun ThemeDropDown(state: MutableState<Boolean>, onClick: (Long) -> Unit) {
    DropdownMenu(
        expanded = state.value,
        modifier = Modifier
            .background(MaterialTheme.colors.surface),
        onDismissRequest = { state.value = false }) {
        Text(
            text = "Colors",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(PaddingValues(start = 8.dp, end = 8.dp)).alpha(0.6f))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.padding(PaddingValues(start = 8.dp, end = 8.dp)), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LightNoteColors.subList(0, 8).forEach { value ->
                ColorSelectButton(color = Color(value)) {
                    onClick(value)
                }
            }
        }
    }
}

@Composable
fun ColorSelectButton(color: Color, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier
            .width(32.dp)
            .height(32.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
        ),
        shape = CircleShape,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
        onClick = { onClick() },
    ) {}
}

@Composable
fun MenuTitleTextField(placeholder: String, value: String, focusManager: FocusManager, onValueChange: (String) -> Unit) {
    BasicTextField(
        modifier = Modifier
            .padding(PaddingValues(top = 8.dp, bottom = 8.dp))
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onSurface,
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Next)
            }
        ),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    modifier = Modifier.alpha(0.6f),
                    text = placeholder,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onSurface
                    )
                )
            }
            innerTextField()
        }
    )
}