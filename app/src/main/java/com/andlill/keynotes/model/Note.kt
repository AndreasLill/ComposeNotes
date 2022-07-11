package com.andlill.keynotes.model

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Label::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("label"),
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val color: Int = 0,
    val label: Int? = null,
    val title: String = "",
    val body: String = "",
    val created: Long? = null,
    val modified: Long? = null,
    val reminder: Long? = null,
    val deleted: Boolean = false,
    val pinned: Boolean = false,
)