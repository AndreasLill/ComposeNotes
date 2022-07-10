package com.andlill.keynotes.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NoteWrapper(
    @Embedded
    val note: Note = Note(),
    @Relation(parentColumn = "id", entityColumn = "id", associateBy = Junction(NoteLabelJoin::class))
    val labels: List<Label> = emptyList()
)
