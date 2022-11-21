package com.andlill.composenotes.model

data class UserPreferences(
    val isGridView: Boolean = true,
    val notePreviewMaxLines: Int = 10,
)