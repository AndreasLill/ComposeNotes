package com.andlill.keynotes.data.database

import androidx.room.*
import com.andlill.keynotes.model.Note
import com.andlill.keynotes.model.NoteWrapper
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM Note WHERE id = :id")
    fun getNote(id: Int): Flow<Note?>

    @Transaction
    @Query("SELECT * FROM Note ORDER BY created DESC")
    fun getAllNotes(): Flow<List<NoteWrapper>>
}