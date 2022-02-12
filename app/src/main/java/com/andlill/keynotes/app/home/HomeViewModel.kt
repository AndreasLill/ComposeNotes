package com.andlill.keynotes.app.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.model.Note
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val application: Application) : ViewModel() {
    class Factory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(application) as T
    }

    private var notes by mutableStateOf<List<Note>>(emptyList())

    init {
        viewModelScope.launch {
            NoteRepository.getAllNotes(application).collectLatest {
                notes = it
            }
        }
    }

    // Get all notes and filter by query.
    fun getNotes(query: String): List<Note> {
        return when {
            query.isEmpty() -> {
                notes
            }
            else -> {
                notes.filter { note ->
                    note.title.contains(query, ignoreCase = true) || note.body.contains(query, ignoreCase = true)
                }
            }
        }
    }
}