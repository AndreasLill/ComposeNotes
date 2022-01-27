package com.andlill.keynotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val created: Long = 0,
    val modified: Long = 0,
    val color: Long = 0xFFFFFFFF,
)