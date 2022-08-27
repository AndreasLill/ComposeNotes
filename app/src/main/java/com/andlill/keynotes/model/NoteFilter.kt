package com.andlill.keynotes.model

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