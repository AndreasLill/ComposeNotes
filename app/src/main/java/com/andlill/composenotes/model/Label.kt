package com.andlill.composenotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Label(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String = "",
)