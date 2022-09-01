package com.andlill.keynotes.ui.shared.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.keynotes.R
import com.andlill.keynotes.ui.shared.button.DialogButton
import com.andlill.keynotes.ui.shared.text.AnnotatedText
import com.andlill.keynotes.ui.shared.text.DialogTitle

@Composable
fun ConfirmDialog(state: Boolean, body: String, annotation: String, annotationStyle: SpanStyle, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    if (state) {
        Dialog(onDismissRequest = onDismiss) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)) {
                DialogTitle(text = stringResource(R.string.dialog_confirm_title))
                Spacer(modifier = Modifier.height(16.dp))
                AnnotatedText(
                    text = body,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    annotation = annotation,
                    annotationStyle = annotationStyle
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                        DialogButton(
                            text = stringResource(R.string.button_cancel),
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            textColor = MaterialTheme.colorScheme.primary,
                            onClick = onDismiss
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DialogButton(
                            text = stringResource(R.string.button_confirm),
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            onClick = onConfirm
                        )
                    }
                }
            }
        }
    }
}