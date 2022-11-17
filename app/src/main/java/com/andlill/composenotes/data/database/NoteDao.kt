package com.andlill.composenotes.data.database

import androidx.room.*
import com.andlill.composenotes.model.Note
import com.andlill.composenotes.model.NoteCheckBox
import com.andlill.composenotes.model.NoteLabelJoin
import com.andlill.composenotes.model.NoteWrapper
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
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteCheckBox(item: NoteCheckBox): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteCheckBoxes(items: List<NoteCheckBox>)

    @Delete
    suspend fun deleteNoteCheckBox(item: NoteCheckBox)

    @Transaction
    @Query("DELETE FROM NoteCheckBox WHERE noteId = :noteId")
    suspend fun deleteAllNoteCheckBoxes(noteId: Int)
}