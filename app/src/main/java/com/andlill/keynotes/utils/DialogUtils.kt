package com.andlill.keynotes.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object DialogUtils {
    var confirmDialogState by mutableStateOf(false)
        private set
    var confirmDialogBody by mutableStateOf("")
        private set
    var confirmDialogListener: (() -> Unit)? = null
        private set

    // Show attached composable confirm dialog and delegate listener callback.
    fun showConfirmDialog(text: String, onConfirm: () -> Unit) {
        confirmDialogBody = text
        confirmDialogState = true
        confirmDialogListener = onConfirm
    }

    fun closeConfirmDialog() {
        confirmDialogState = false
    }
}