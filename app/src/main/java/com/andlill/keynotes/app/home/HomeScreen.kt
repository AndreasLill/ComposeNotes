package com.andlill.keynotes.app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andlill.keynotes.R
import com.andlill.keynotes.app.Screen
import com.andlill.keynotes.app.home.composables.NoteItem
import com.andlill.keynotes.app.home.composables.SearchBar
import com.andlill.keynotes.app.shared.MenuIconButton
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navigation: NavController, viewModel: HomeViewModel = viewModel()) {

    val searchQuery = rememberSaveable { mutableStateOf("") }
    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val notes = viewModel.getNotes(searchQuery.value).collectAsState(initial = emptyList())

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
            OutlinedButton(modifier = Modifier
                .navigationBarsPadding()
                .padding(8.dp)
                .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.onSurface,
                ),
                onClick = {
                    navigation.navigate(Screen.NoteScreen.route) {
                        // To avoid multiple copies of same destination in backstack.
                        launchSingleTop = true
                    }
                }) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.home_Screen_button_add_note),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold)
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