package com.andlill.keynotes.app.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.keynotes.data.repository.LabelRepository
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.model.Note
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(private val application: Application) : ViewModel() {
    class Factory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(application) as T
    }

    // Contains all notes unfiltered.
    private var _notes: List<Note> = emptyList()

    var notes by mutableStateOf<List<Note>>(emptyList())
        private set
    var labels by mutableStateOf<List<Label>>(emptyList())
        private set
    var filterDeleted by mutableStateOf(false)
        private set
    var filterLabel by mutableStateOf<Label?>(null)
        private set
    var query by mutableStateOf("")
        private set

    var drawerSelectedItem by mutableStateOf(0)
    var drawerSelectedItemName by mutableStateOf("Notes")
    var drawerSelectedLabel by mutableStateOf(Label())

    init {
        viewModelScope.launch {
            NoteRepository.getAllNotes(application).collectLatest {
                _notes = it
                filterNotes()
            }
        }
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest {
                labels = it.sortedBy { label -> label.value.toLowerCase(Locale.current) }
            }
        }
    }

    private fun filterNotes() {
        // Filter notes on modified value. (Modified 'null' notes are temporary created but unsaved notes.)
        var filterList = _notes.filter { note -> note.modified != null }

        // Filter notes on deleted boolean status.
        filterList = filterList.filter { note -> note.deleted == filterDeleted }

        // Filter notes on label.
        if (filterLabel != null)
            filterList = filterList.filter { note -> note.labels.contains(filterLabel) }

        // Filter notes on query.
        if (query.isNotEmpty()) {
            filterList = filterList.filter { note -> note.title.contains(query, ignoreCase = true) || note.body.contains(query, ignoreCase = true) }
        }

        notes = filterList
    }

    fun onCreateNote(callback: (Int) -> Unit) = viewModelScope.launch {
        // Create a new note and callback the note id.
        val noteId = NoteRepository.insertNote(application, Note(
            created = Calendar.getInstance().timeInMillis,
        ))
        callback(noteId)
    }

    fun onAddLabel(label: Label) = viewModelScope.launch {
        LabelRepository.insertLabel(application, label)
    }

    fun onDeleteLabel(label: Label) = viewModelScope.launch {
        LabelRepository.deleteLabel(application, label)
    }

    fun onQuery(value: String) = viewModelScope.launch {
        query = value
        filterNotes()
    }

    fun onFilterDeleted(value: Boolean) = viewModelScope.launch {
        filterLabel = null
        filterDeleted = value
        filterNotes()
    }

    fun onFilterLabel(value: Label?) = viewModelScope.launch {
        filterLabel = value
        filterNotes()
    }
}