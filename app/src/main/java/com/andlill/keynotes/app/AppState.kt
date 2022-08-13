package com.andlill.keynotes.app

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppState(
    val scaffold: ScaffoldState,
    val navigation: NavHostController,
) {
    fun showSnackbar(text: String, duration: SnackbarDuration) = CoroutineScope(Dispatchers.Default).launch {
        scaffold.snackbarHostState.showSnackbar(text, null, duration)
    }
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigationState: NavHostController = rememberNavController()
) = remember(scaffoldState, navigationState) {
    AppState(
        scaffold = scaffoldState,
        navigation = navigationState
    )
}