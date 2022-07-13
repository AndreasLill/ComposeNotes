package com.andlill.keynotes.model

import androidx.room.*

// Class wrapper for Note and joined entities.
data class NoteWrapper(
    @Embedded
    val note: Note = Note(),
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Label::class,
        associateBy = Junction(NoteLabelJoin::class, parentColumn = "noteId", entityColumn = "labelId")
    )
    val labels: List<Label> = emptyList()
)

// "onDelete = ForeignKey.CASCADE" will not work correctly with "OnConflictStrategy.REPLACE".
@Entity(
    primaryKeys = ["noteId", "labelId"],
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Label::class,
            parentColumns = ["id"],
            childColumns = ["labelId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
data class NoteLabelJoin(
    val noteId: Int,
    val labelId: Int,
)