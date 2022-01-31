package com.andlill.keynotes.data.database

import androidx.room.*
import com.andlill.keynotes.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM Note WHERE id = :id")
    suspend fun getNote(id: Int): Note?

    @Query("SELECT * FROM Note WHERE id = :id")
    fun getNoteAsFlow(id: Int): Flow<Note?>

    @Query("SELECT * FROM Note ORDER BY created DESC")
    fun getAllNotes(): Flow<List<Note>>
}