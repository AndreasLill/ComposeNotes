package com.andlill.keynotes.ui.shared.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.R
import com.andlill.keynotes.ui.shared.button.DialogButton
import com.andlill.keynotes.ui.shared.text.DialogTitle

@Composable
fun ConfirmDialog(state: Boolean, body: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    if (state) {
        Dialog(onDismissRequest = onDismiss) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)) {
                DialogTitle(text = stringResource(R.string.dialog_confirm_title))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = body,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                        DialogButton(
                            text = stringResource(R.string.button_cancel),
                            backgroundColor = Color.Transparent,
                            textColor = MaterialTheme.colors.primary,
                            onClick = onDismiss
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DialogButton(
                            text = stringResource(R.string.button_confirm),
                            backgroundColor = MaterialTheme.colors.primary,
                            textColor = MaterialTheme.colors.onPrimary,
                            onClick = onConfirm
                        )
                    }
                }
            }
        }
    }
}