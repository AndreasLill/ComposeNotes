package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Drawer(state: DrawerState, labels: List<String> = emptyList()) {
    val scope = rememberCoroutineScope()
    DrawerHeader()
    Column(modifier = Modifier
        .background(MaterialTheme.colors.surface)
        .fillMaxSize()) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            DrawerItem(icon = Icons.Outlined.Home, text = "Notes", onClick = {
                scope.launch {
                    state.close()
                }
            })
            DrawerItem(icon = Icons.Outlined.Delete, text = "Trash", onClick = {
                scope.launch {
                    state.close()
                }
            })
        }
        Divider()
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            labels.forEach { label ->
                DrawerItem(icon = Icons.Outlined.Label, text = label, onClick = {
                    scope.launch {
                        state.close()
                    }
                })
            }
            DrawerItem(icon = Icons.Outlined.Add, text = "Add Label", onClick = {

            })
        }
    }
}