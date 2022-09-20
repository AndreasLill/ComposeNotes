package com.andlill.composenotes.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle

object DialogUtils {
    var confirmDialogState by mutableStateOf(false)
        private set
    var confirmDialogBody by mutableStateOf("")
        private set
    var confirmDialogAnnotation by mutableStateOf("")
        private set
    var confirmDialogAnnotationStyle by mutableStateOf(SpanStyle())
        private set
    var confirmDialogListener: (() -> Unit)? = null
        private set
    var inputDialogState by mutableStateOf(false)
        private set
    var inputDialogTitle by mutableStateOf("")
        private set
    var inputDialogPlaceholder by mutableStateOf("")
        private set
    var inputDialogListener: ((String) -> Unit)? = null
        private set


    // Show attached composable confirm dialog and delegate listener callback.
    fun showConfirmDialog(text: String, annotation: String = "", annotationStyle: SpanStyle = SpanStyle(), onConfirm: () -> Unit) {
        confirmDialogState = true
        confirmDialogBody = text
        confirmDialogAnnotation = annotation
        confirmDialogAnnotationStyle = annotationStyle
        confirmDialogListener = onConfirm
    }

    fun closeConfirmDialog() {
        confirmDialogState = false
    }

    // Show attached composable input dialog and delegate listener callback.
    fun showInputDialog(title: String, placeholder: String, onConfirm: (String) -> Unit) {
        inputDialogState = true
        inputDialogTitle = title
        inputDialogPlaceholder = placeholder
        inputDialogListener = onConfirm
    }

    fun closeInputDialog() {
        inputDialogState = false
    }
}