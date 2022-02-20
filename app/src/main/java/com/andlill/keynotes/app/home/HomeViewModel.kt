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
import com.andlill.keynotes.model.NoteFilter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val application: Application) : ViewModel() {
    class Factory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(application) as T
    }

    // Contains all notes unfiltered.
    private var _notes: List<Note> = emptyList()
    // Contains filtered notes.
    var notes by mutableStateOf<List<Note>>(emptyList())
        private set
    var filter by mutableStateOf(NoteFilter())
        private set

    init {
        viewModelScope.launch {
            NoteRepository.getAllNotes(application).collectLatest {
                _notes = it
                filterNotes()
            }
        }
    }

    private fun filterNotes() {
        var filterList = _notes.filter { note -> note.deleted == filter.deleted }

        if (filter.label.isNotEmpty()) {
            filterList = filterList.filter { note -> note.labels.contains(filter.label) }
        }

        if (filter.query.isNotEmpty()) {
            filterList = filterList.filter { note -> note.title.contains(filter.query, ignoreCase = true) || note.body.contains(filter.query, ignoreCase = true) }
        }

        notes = filterList
    }

    fun onFilterQuery(value: String) {
        filter = filter.copy(
            query = value
        )
        filterNotes()
    }

    fun onFilterDeleted(value: Boolean) {
        filter = filter.copy(
            deleted = value
        )
        filterNotes()
    }
}