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

    @Query("UPDATE Note SET title = :title, body = :body, modified = :modified WHERE id = :id")
    fun setNoteContent(id: Int, title: String, body: String, modified: Long)

    @Query("UPDATE Note SET color = :color WHERE id = :id")
    fun setNoteColor(id: Int, color: Int)

    @Query("UPDATE Note SET pinned = :pinned WHERE id = :id")
    fun setNotePinned(id: Int, pinned: Boolean)

    @Query("UPDATE Note SET deletion = :deletion WHERE id = :id")
    fun setNoteDeletion(id: Int, deletion: Long?)

    @Query("UPDATE Note SET reminder = :reminder WHERE id = :id")
    fun setNoteReminder(id: Int, reminder: Long?)

    @Query("UPDATE Note SET reminder = :reminder, reminderSelection = :reminder, reminderRepeat = :repeat WHERE id = :id")
    fun setNoteReminderWithRepeat(id: Int, reminder: Long?, repeat: String?)

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