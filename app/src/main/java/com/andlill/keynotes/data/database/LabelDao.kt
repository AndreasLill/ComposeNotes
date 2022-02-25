package com.andlill.keynotes.data.database

import androidx.room.*
import com.andlill.keynotes.model.Label
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: Label)

    @Delete
    suspend fun deleteLabel(label: Label)

    @Query("SELECT * FROM Label")
    fun getAllLabels(): Flow<List<Label>>
}