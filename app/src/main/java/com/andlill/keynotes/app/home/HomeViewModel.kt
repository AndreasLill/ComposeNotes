package com.andlill.keynotes.app.home

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Get all notes and filter by query.
    fun getNotes(query: String): Flow<List<Note>> {
        val notes = NoteRepository.getAllNotes(getApplication())
        return when {
            query.isEmpty() -> notes
            else -> notes.map { list ->
                list.filter { note ->
                    note.title.contains(query, ignoreCase = true) || note.body.contains(query, ignoreCase = true)
                }
            }
        }
    }
}