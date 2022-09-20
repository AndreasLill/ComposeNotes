package com.andlill.composenotes.app.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.composenotes.R
import com.andlill.composenotes.data.repository.LabelRepository
import com.andlill.composenotes.data.repository.NoteRepository
import com.andlill.composenotes.model.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val application: Application) : ViewModel() {
    class Factory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(application) as T
    }

    // Contains all notes unfiltered.
    private var _notes = emptyList<NoteWrapper>()

    var notes by mutableStateOf(emptyList<NoteWrapper>())
        private set
    var labels by mutableStateOf(emptyList<Label>())
        private set
    var filter by mutableStateOf(NoteFilter(application.resources.getString(R.string.drawer_item_notes), NoteFilter.Type.AllNotes))
        private set
    var query by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            NoteRepository.getAllNotes(application).collectLatest { note ->
                _notes = note.sortedWith(compareByDescending<NoteWrapper> { it.note.pinned }.thenByDescending { it.note.created })
                filterNotes(filter)
            }
        }
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest {
                labels = it.sortedBy { label -> label.value.lowercase() }
            }
        }
    }

    private fun filterNotes(filter: NoteFilter) {
        // Filter notes on modified value. (Modified 'null' notes are temporary created but unsaved notes.)
        var filterList = _notes.filter { it.note.modified != null }

        // Filter notes.
        filterList = when (filter.type) {
            NoteFilter.Type.AllNotes -> {
                filterList.filter { it.note.deletion == null }
            }
            NoteFilter.Type.Reminders -> {
                filterList.filter { it.note.reminder != null }.sortedBy { it.note.reminder }
            }
            NoteFilter.Type.Trash -> {
                filterList.filter { it.note.deletion != null }.sortedByDescending { it.note.deletion }
            }
            NoteFilter.Type.Label -> {
                filterList.filter { it.labels.contains(filter.label) }
            }
        }

        // Also filter notes on query.
        if (query.isNotEmpty()) {
            filterList = filterList.filter { it.note.title.contains(query, ignoreCase = true) || it.note.body.contains(query, ignoreCase = true) }
        }

        notes = filterList
    }

    fun onCreateNote(callback: (Int) -> Unit) = viewModelScope.launch {
        // Create a new note and callback the note id.
        val noteId = NoteRepository.insertNote(application, Note(
            created = System.currentTimeMillis(),
        ))
        callback(noteId)
    }

    fun onAddNoteLabel(noteId: Int, labelId: Int) = viewModelScope.launch {
        NoteRepository.insertNoteLabel(application, NoteLabelJoin(noteId = noteId, labelId = labelId))
    }

    fun onAddLabel(label: Label) = viewModelScope.launch {
        LabelRepository.insertLabel(application, label)
    }

    fun onQuery(value: String) = viewModelScope.launch {
        query = value
        filterNotes(filter)
    }

    fun onFilter(value: NoteFilter) = viewModelScope.launch {
        filter = value
        filterNotes(filter)
    }
}