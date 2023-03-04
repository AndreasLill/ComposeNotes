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

    @Query("DELETE FROM Note WHERE id = :id")
    suspend fun deleteNote(id: Int)

    @Transaction
    @Query("SELECT * FROM Note WHERE id = :id")
    fun getNote(id: Int): Flow<NoteWrapper?>

    @Transaction
    @Query("SELECT * FROM Note")
    fun getAllNotes(): Flow<List<NoteWrapper>>

    @Query("UPDATE Note SET reminder = :reminder, reminderRepeat = :repeat WHERE id = :id")
    fun setNoteReminder(id: Int, reminder: Long?, repeat: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteLabel(item: NoteLabelJoin): Long

    @Delete
    suspend fun deleteNoteLabel(item: NoteLabelJoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteCheckBoxes(items: List<NoteCheckBox>)

    @Query("DELETE FROM NoteCheckBox WHERE noteId = :noteId")
    suspend fun deleteNoteCheckBoxes(noteId: Int)

    @Transaction
    suspend fun updateNoteCheckBoxes(noteId: Int, items: List<NoteCheckBox>) {
        deleteNoteCheckBoxes(noteId)
        insertNoteCheckBoxes(items)
    }
}