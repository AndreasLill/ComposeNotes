package com.andlill.composenotes.app

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppState(
    val snackbarHostState: SnackbarHostState,
    val navigation: NavHostController,
) {
    fun showSnackbar(text: String, duration: SnackbarDuration) = CoroutineScope(Dispatchers.Default).launch {
        snackbarHostState.showSnackbar(text, null, true, duration)
    }
}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigationState: NavHostController = rememberNavController()
) = remember(snackbarHostState, navigationState) {
    AppState(
        snackbarHostState = snackbarHostState,
        navigation = navigationState
    )
}