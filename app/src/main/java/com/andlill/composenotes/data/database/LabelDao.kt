package com.andlill.composenotes.data.database

import androidx.room.*
import com.andlill.composenotes.model.Label
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {

    @Insert
    suspend fun insertLabel(label: Label): Long

    @Update
    suspend fun updateLabel(label: Label)

    @Delete
    suspend fun deleteLabel(label: Label)

    @Query("SELECT * FROM Label")
    fun getAllLabels(): Flow<List<Label>>
}