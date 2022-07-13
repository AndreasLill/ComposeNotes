package com.andlill.keynotes.data.database

import androidx.room.*
import com.andlill.keynotes.model.Note
import com.andlill.keynotes.model.NoteLabelJoin
import com.andlill.keynotes.model.NoteWrapper
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Transaction
    @Query("SELECT * FROM Note WHERE id = :id")
    fun getNote(id: Int): Flow<NoteWrapper?>

    @Transaction
    @Query("SELECT * FROM Note ORDER BY created DESC")
    fun getAllNotes(): Flow<List<NoteWrapper>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteLabel(item: NoteLabelJoin): Long

    @Delete
    suspend fun deleteNoteLabel(item: NoteLabelJoin)
}