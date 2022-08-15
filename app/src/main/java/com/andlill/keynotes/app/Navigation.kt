package com.andlill.keynotes.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andlill.keynotes.app.home.HomeScreen
import com.andlill.keynotes.app.note.NoteScreen

@Composable
fun Navigation(appState: AppState) {
    NavHost(
        navController = appState.navigation,
        startDestination = Screen.HomeScreen.route) {
        // Root home screen
        composable(Screen.HomeScreen.route) {
            HomeScreen(
                appState = appState
            )
        }
        // Note screen with argument for note id.
        composable(
            route = Screen.NoteScreen.route + "/{noteId}",
            arguments = listOf(
                navArgument("noteId") { type = NavType.IntType },
            ),
            deepLinks = listOf(
                NavDeepLink(String.format(Screen.NoteScreen.uri, "{noteId}")),
            )
        ) {
            NoteScreen(
                appState = appState,
                noteId = it.arguments!!.getInt("noteId"),
            )
        }
    }
}

sealed class Screen(val route: String, val uri: String = "") {
    companion object {
        private const val baseUri = "https://com.andlill.keynotes"
    }
    object HomeScreen : Screen("home")
    object NoteScreen : Screen("note", "$baseUri/note/noteId=%s")
}