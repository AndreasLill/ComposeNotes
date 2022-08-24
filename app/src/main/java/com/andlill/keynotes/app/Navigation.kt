package com.andlill.keynotes.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andlill.keynotes.app.home.HomeScreen
import com.andlill.keynotes.app.label.LabelScreen
import com.andlill.keynotes.app.note.NoteScreen

@Composable
fun Navigation(appState: AppState) {
    NavHost(
        navController = appState.navigation,
        startDestination = Screen.HomeScreen.route()) {

        composable(Screen.HomeScreen.Declaration.ROUTE) {
            HomeScreen(appState = appState)
        }

        composable(Screen.LabelScreen.Declaration.ROUTE) {
            LabelScreen(
                appState = appState,
                noteId = null)
        }

        composable(
            route = String.format(Screen.LabelScreen.Declaration.ROUTE_ARGUMENT_NOTE_ID, "{${Screen.LabelScreen.Declaration.ARGUMENT_NOTE_ID}}"),
            arguments = listOf(
                navArgument(Screen.LabelScreen.Declaration.ARGUMENT_NOTE_ID) { type = NavType.IntType }
            ),
        ) {
            LabelScreen(
                appState = appState,
                noteId = it.arguments?.getInt(Screen.LabelScreen.Declaration.ARGUMENT_NOTE_ID)
            )
        }

        composable(
            route = String.format(Screen.NoteScreen.Declaration.ROUTE_ARGUMENT_NOTE_ID, "{${Screen.NoteScreen.Declaration.ARGUMENT_NOTE_ID}}"),
            arguments = listOf(
                navArgument(Screen.NoteScreen.Declaration.ARGUMENT_NOTE_ID) { type = NavType.IntType },
            ),
            deepLinks = listOf(
                NavDeepLink(String.format(Screen.NoteScreen.Declaration.DEEP_LINK, "{${Screen.NoteScreen.Declaration.ARGUMENT_NOTE_ID}}")),
            )
        ) {
            NoteScreen(
                appState = appState,
                noteId = it.arguments!!.getInt(Screen.NoteScreen.Declaration.ARGUMENT_NOTE_ID),
            )
        }
    }
}

sealed class Screen {
    companion object {
        private const val BASE_URI = "https://com.andlill.keynotes"
    }

    object HomeScreen {
        object Declaration {
            const val ROUTE = "home"
        }

        fun route(): String {
            return Declaration.ROUTE
        }
    }

    object LabelScreen {
        object Declaration {
            const val ARGUMENT_NOTE_ID = "noteId"
            const val ROUTE = "label"
            const val ROUTE_ARGUMENT_NOTE_ID = "$ROUTE/$ARGUMENT_NOTE_ID=%s"
        }

        fun route(): String {
            return Declaration.ROUTE
        }

        fun route(noteId: Int): String {
            return String.format(Declaration.ROUTE_ARGUMENT_NOTE_ID, noteId)
        }
    }

    object NoteScreen {
        object Declaration {
            const val ARGUMENT_NOTE_ID = "noteId"
            private const val ROUTE = "note"
            const val ROUTE_ARGUMENT_NOTE_ID = "$ROUTE/$ARGUMENT_NOTE_ID=%s"
            const val DEEP_LINK = "$BASE_URI/$ROUTE/$ARGUMENT_NOTE_ID=%s"
        }

        fun route(noteId: Int): String {
            return String.format(Declaration.ROUTE_ARGUMENT_NOTE_ID, noteId)
        }

        fun deepLink(noteId: Int): String {
            return String.format(Declaration.DEEP_LINK, noteId)
        }
    }
}