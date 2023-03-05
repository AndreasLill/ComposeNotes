package com.andlill.composenotes.data.repository

import android.content.Context
import com.andlill.composenotes.data.database.AppDatabase
import com.andlill.composenotes.model.Note
import com.andlill.composenotes.model.NoteCheckBox
import com.andlill.composenotes.model.NoteLabelJoin
import com.andlill.composenotes.model.NoteWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

object NoteRepository {

    suspend fun insertNote(context: Context, note: Note): Int = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.get(context).noteDao.insertNote(note).toInt()
    }

    suspend fun updateNote(context: Context, note: Note) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.updateNote(note)
    }

    suspend fun deleteNote(context: Context, id: Int) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.deleteNote(id)
    }

    fun getNote(context: Context, id: Int): Flow<NoteWrapper?> {
        return AppDatabase.get(context).noteDao.getNote(id).flowOn(Dispatchers.IO)
    }

    fun getAllNotes(context: Context): Flow<List<NoteWrapper>> {
        return AppDatabase.get(context).noteDao.getAllNotes().flowOn(Dispatchers.IO)
    }

    suspend fun setNoteContent(context: Context, id: Int, title: String, body: String, modified: Long) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.setNoteContent(id, title, body, modified)
    }

    suspend fun setNoteColor(context: Context, id: Int, color: Int) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.setNoteColor(id, color)
    }

    suspend fun setNotePinned(context: Context, id: Int, pinned: Boolean) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.setNotePinned(id, pinned)
    }

    suspend fun setNoteDeletion(context: Context, id: Int, deletion: Long?) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.setNoteDeletion(id, deletion)
    }

    suspend fun setNoteReminder(context: Context, id: Int, reminder: Long?) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.setNoteReminder(id, reminder)
    }

    suspend fun setNoteReminderWithRepeat(context: Context, id: Int, reminder: Long?, repeat: String?) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.setNoteReminderWithRepeat(id, reminder, repeat)
    }

    suspend fun insertNoteLabel(context: Context, item: NoteLabelJoin): Int = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.get(context).noteDao.insertNoteLabel(item).toInt()
    }

    suspend fun deleteNoteLabel(context: Context, item: NoteLabelJoin) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.deleteNoteLabel(item)
    }

    suspend fun updateNoteCheckBoxes(context: Context, noteId: Int, items: List<NoteCheckBox>) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.updateNoteCheckBoxes(noteId, items)
    }
}