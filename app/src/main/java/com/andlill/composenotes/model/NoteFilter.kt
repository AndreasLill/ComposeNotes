package com.andlill.composenotes.model

data class NoteFilter(
    val name: String,
    val type: Type,
    val label: Label? = null,
) {
    enum class Type {
        AllNotes,
        Reminders,
        Trash,
        Label
    }
}