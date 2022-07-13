package com.andlill.keynotes.data.repository

import android.content.Context
import com.andlill.keynotes.data.database.AppDatabase
import com.andlill.keynotes.model.Note
import com.andlill.keynotes.model.NoteLabelJoin
import com.andlill.keynotes.model.NoteWrapper
import kotlinx.coroutines.flow.Flow

object NoteRepository {

    suspend fun insertNote(context: Context, note: Note): Int {
        return AppDatabase.get(context).noteDao.insertNote(note).toInt()
    }

    suspend fun updateNote(context: Context, note: Note) {
        AppDatabase.get(context).noteDao.updateNote(note)
    }

    suspend fun deleteNote(context: Context, note: Note) {
        AppDatabase.get(context).noteDao.deleteNote(note)
    }

    fun getNote(context: Context, id: Int): Flow<NoteWrapper?> {
        return AppDatabase.get(context).noteDao.getNote(id)
    }

    fun getAllNotes(context: Context): Flow<List<NoteWrapper>> {
        return AppDatabase.get(context).noteDao.getAllNotes()
    }

    suspend fun insertNoteLabel(context: Context, item: NoteLabelJoin): Int {
        return AppDatabase.get(context).noteDao.insertNoteLabel(item).toInt()
    }

    suspend fun deleteNoteLabel(context: Context, item: NoteLabelJoin) {
        AppDatabase.get(context).noteDao.deleteNoteLabel(item)
    }
}