package com.andlill.keynotes.ui.shared

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppSnackbar(state: SnackbarHostState, modifier: Modifier) {
    SnackbarHost(
        modifier = modifier.padding(16.dp),
        hostState = state,
        snackbar =  { data ->
            Snackbar(
                content = {
                    Text(
                        text = data.message,
                        fontSize = 14.sp,
                    )
                },
                action = {
                    data.actionLabel?.let { actionLabel ->
                        TextButton(onClick = { data.performAction() }) {
                            Text(
                                text = actionLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            )
        }
    )
}