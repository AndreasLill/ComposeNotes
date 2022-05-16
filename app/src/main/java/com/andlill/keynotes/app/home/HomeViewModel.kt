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

    var notes by mutableStateOf<List<Note>>(emptyList())
        private set
    var labels by mutableStateOf<List<Label>>(emptyList())
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
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest {
                labels = it.sortedBy { label -> label.value.toLowerCase(Locale.current) }
            }
        }
    }

    private fun filterNotes() {
        var filterList = _notes.filter { note -> note.deleted == filter.deleted }

        if (filter.label.value.isNotEmpty()) {
            filterList = filterList.filter { note -> note.labels.contains(filter.label) }
        }

        if (filter.query.isNotEmpty()) {
            filterList = filterList.filter { note -> note.title.contains(filter.query, ignoreCase = true) || note.body.contains(filter.query, ignoreCase = true) }
        }

        notes = filterList
    }

    fun onAddLabel(label: Label) = viewModelScope.launch {
        LabelRepository.insertLabel(application, label)
    }

    fun onDeleteLabel(label: Label) = viewModelScope.launch {
        LabelRepository.deleteLabel(application, label)
    }

    fun onFilterQuery(value: String) = viewModelScope.launch {
        filter = filter.copy(
            query = value
        )
        filterNotes()
    }

    fun onFilterDeleted(value: Boolean) = viewModelScope.launch {
        filter = filter.copy(
            deleted = value
        )
        filterNotes()
    }
}