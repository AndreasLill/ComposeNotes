package com.andlill.keynotes.ui.shared.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.ui.shared.text.DialogTitle

@Composable
fun BaseDialog(
    state: MutableState<Boolean>,
    title: String,
    onDismiss: () -> Unit = {},
    buttons: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    if (state.value) {
        Dialog(
            onDismissRequest = {
                state.value = false
                onDismiss()
            },
            content = {
                Surface(modifier = Modifier.heightIn(0.dp, 640.dp), color = MaterialTheme.colors.surface) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DialogTitle(text = title)
                        Spacer(modifier = Modifier.height(16.dp))
                        content()
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.CenterEnd) {
                            buttons()
                        }
                    }
                }
            }
        )
    }
}