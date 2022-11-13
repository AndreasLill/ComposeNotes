package com.andlill.composenotes.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("id"),
        Index("noteId")
    ]
)
data class NoteCheckBox(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val noteId: Int = 0,
    val text: String = "",
    val checked: Boolean = false,
)