package com.andlill.keynotes.model

data class NoteFilter(
    val deleted: Boolean = false,
    val label: Label = Label(),
    val query: String = "",
)
