package com.andlill.keynotes.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("noteId"),
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = Label::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("labelId"),
        onDelete = ForeignKey.CASCADE
    ),
])
data class NoteLabelJoin(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteId: Int,
    val labelId: Int,
)
