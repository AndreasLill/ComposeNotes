package com.andlill.keynotes.ui.shared.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.R

@Composable
fun ConfirmDialog(state: MutableState<Boolean>, body: String, onConfirm: () -> Unit) {
    if (state.value) {
        Dialog(onDismissRequest = { state.value = false }) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)) {
                Text(
                    text = stringResource(R.string.dialog_confirm_text_title).uppercase(),
                    letterSpacing = 1.sp,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = body,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                        OutlinedButton(onClick = { state.value = false }) {
                            Text(
                                text = stringResource(R.string.dialog_confirm_button_cancel).uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colors.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { onConfirm(); state.value = false }) {
                            Text(
                                text = stringResource(R.string.dialog_confirm_button_confirm).uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}