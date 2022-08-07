package com.andlill.keynotes.model

import androidx.room.*

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val color: Int = 0,
    val title: String = "",
    val body: String = "",
    val created: Long? = null,
    val modified: Long? = null,
    val reminder: Long? = null,
    val deletion: Long? = null,
    val pinned: Boolean = false,
)