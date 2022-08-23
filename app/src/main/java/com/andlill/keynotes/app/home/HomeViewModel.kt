package com.andlill.keynotes.app.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.keynotes.R
import com.andlill.keynotes.data.repository.LabelRepository
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.model.Note
import com.andlill.keynotes.model.NoteLabelJoin
import com.andlill.keynotes.model.NoteWrapper
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
    var filterTrash by mutableStateOf(false)
        private set
    var filterLabel by mutableStateOf<Label?>(null)
        private set
    var query by mutableStateOf("")
        private set

    var drawerSelectedItem by mutableStateOf(application.resources.getString(R.string.drawer_item_notes))

    init {
        viewModelScope.launch {
            NoteRepository.getAllNotes(application).collectLatest { note ->
                _notes = note.sortedWith(compareByDescending<NoteWrapper> { it.note.pinned }.thenByDescending { it.note.created })
                filterNotes()
            }
        }
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest {
                labels = it.sortedBy { label -> label.value.lowercase() }
            }
        }
    }

    private fun filterNotes() {
        // Filter notes on modified value. (Modified 'null' notes are temporary created but unsaved notes.)
        var filterList = _notes.filter { it.note.modified != null }

        // Filter notes on deleted status.
        filterList = filterList.filter { (it.note.deletion != null) == filterTrash }

        // Filter notes on label id.
        filterLabel?.let { label ->
            filterList = filterList.filter { it.labels.contains(label) }
        }

        // Filter notes on query.
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
        filterNotes()
    }

    fun onFilterTrash(value: Boolean) = viewModelScope.launch {
        filterLabel = null
        filterTrash = value
        filterNotes()
    }

    fun onFilterLabel(label: Label?) = viewModelScope.launch {
        filterLabel = label
        filterTrash = false
        filterNotes()
    }
}