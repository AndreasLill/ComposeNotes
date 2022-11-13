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

    suspend fun deleteNote(context: Context, note: Note) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.deleteNote(note)
    }

    fun getNote(context: Context, id: Int): Flow<NoteWrapper?> {
        return AppDatabase.get(context).noteDao.getNote(id).flowOn(Dispatchers.IO)
    }

    fun getAllNotes(context: Context): Flow<List<NoteWrapper>> {
        return AppDatabase.get(context).noteDao.getAllNotes().flowOn(Dispatchers.IO)
    }

    suspend fun insertNoteLabel(context: Context, item: NoteLabelJoin): Int = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.get(context).noteDao.insertNoteLabel(item).toInt()
    }

    suspend fun deleteNoteLabel(context: Context, item: NoteLabelJoin) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.deleteNoteLabel(item)
    }

    suspend fun insertNoteCheckBox(context: Context, item: NoteCheckBox) = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.get(context).noteDao.insertNoteCheckBox(item).toInt()
    }

    suspend fun insertNoteCheckBoxes(context: Context, items: List<NoteCheckBox>) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.insertNoteCheckBoxes(items)
    }

    suspend fun deleteNoteCheckBox(context: Context, item: NoteCheckBox) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.deleteNoteCheckBox(item)
    }

    suspend fun deleteAllNoteCheckBoxes(context: Context, noteId: Int) = withContext(Dispatchers.IO) {
        AppDatabase.get(context).noteDao.deleteAllNoteCheckBoxes(noteId)
    }
}