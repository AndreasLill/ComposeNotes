package com.andlill.composenotes.model

import androidx.room.*

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val color: Int = 0,
    val title: String = "",
    val body: String = "",
    val reminderRepeat: String? = null,
    val created: Long? = null,
    val modified: Long? = null,
    val reminder: Long? = null,
    val deletion: Long? = null,
    val pinned: Boolean = false,
)