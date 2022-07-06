package com.andlill.keynotes.data.repository

import android.content.Context
import com.andlill.keynotes.data.database.AppDatabase
import com.andlill.keynotes.model.Note
import kotlinx.coroutines.flow.Flow

object NoteRepository {

    suspend fun insertNote(context: Context, note: Note): Long {
        return AppDatabase.get(context).noteDao.insertNote(note)
    }

    suspend fun deleteNote(context: Context, note: Note) {
        AppDatabase.get(context).noteDao.deleteNote(note)
    }

    fun getNote(context: Context, id: Int): Flow<Note?> {
        return AppDatabase.get(context).noteDao.getNote(id)
    }

    fun getAllNotes(context: Context): Flow<List<Note>> {
        return AppDatabase.get(context).noteDao.getAllNotes()
    }
}