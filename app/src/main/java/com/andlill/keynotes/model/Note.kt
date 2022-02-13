package com.andlill.keynotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.andlill.keynotes.data.database.DataConverter

@Entity
@TypeConverters(DataConverter::class)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val color: Int = 0,
    val title: String = "",
    val body: String = "",
    val created: Long? = null,
    val modified: Long? = null,
    val reminder: Long? = null,
    val deleted: Boolean = false,
    val labels: List<String> = emptyList(),
)