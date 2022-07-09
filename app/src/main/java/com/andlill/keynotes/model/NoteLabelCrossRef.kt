package com.andlill.keynotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteLabelCrossRef(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteId: Int,
    val labelId: Int,
)
