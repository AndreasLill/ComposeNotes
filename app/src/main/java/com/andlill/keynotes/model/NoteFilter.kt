package com.andlill.keynotes.model

data class NoteFilter(
    val deleted: Boolean = false,
    val label: String = "",
    val query: String = "",
)
