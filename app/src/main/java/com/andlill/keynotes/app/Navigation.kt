package com.andlill.keynotes.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavDeepLink
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.andlill.keynotes.app.home.HomeScreen
import com.andlill.keynotes.app.note.NoteScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route) {
        // Root home screen
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        // Note screen with argument for note id.
        composable(
            route = Screen.NoteScreen.route + "/{noteId}",
            arguments = listOf(
                navArgument("noteId") { defaultValue = -1 },
            ),
            deepLinks = listOf(
                NavDeepLink(uri = String.format(Screen.NoteScreen.uri[0], "{noteId}")),
            )
        ) {
            NoteScreen(
                navigation = navController,
                noteId = it.arguments!!.getInt("noteId"),
            )
        }
    }
}

sealed class Screen(val route: String, vararg val uri: String) {
    companion object {
        private const val baseUri = "https://com.andlill.keynotes"
    }
    object HomeScreen : Screen("home")
    object NoteScreen : Screen("note", "$baseUri/noteId=%s")
}