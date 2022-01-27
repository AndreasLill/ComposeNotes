package com.andlill.keynotes.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
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
        // Root "Home" Screen
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        // Note screen for new note.
        composable(Screen.NoteScreen.route) {
            NoteScreen(navController)
        }
        // Note screen with argument for loading existing note.
        composable(
            route = Screen.NoteScreen.route + "/{noteId}",
            arguments = listOf(
                navArgument("noteId") { type = NavType.IntType }
            )
        ) {
            NoteScreen(navigation = navController, noteId = it.arguments!!.getInt("noteId"))
        }
    }
}

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object NoteScreen : Screen("note")
}