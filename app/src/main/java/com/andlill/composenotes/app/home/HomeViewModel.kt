package com.andlill.composenotes.app.home

import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.composenotes.R
import com.andlill.composenotes.data.preferences.PreferencesKeys
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

    private val Context.dataStore by preferencesDataStore("com.andlill.composenotes.preferences")

    var notes = mutableStateListOf<NoteWrapper>()
        private set
    var labels = mutableStateListOf<Label>()
        private set
    var filter by mutableStateOf(NoteFilter(application.resources.getString(R.string.drawer_item_notes), NoteFilter.Type.AllNotes))
        private set
    var query by mutableStateOf("")
        private set
    var userPreferences by mutableStateOf(UserPreferences())
        private set

    init {
        viewModelScope.launch {
            application.dataStore.data.collectLatest { preferences ->
                preferences[PreferencesKeys.IS_GRID_VIEW]?.let {
                    userPreferences = userPreferences.copy(isGridView = it)
                }
                preferences[PreferencesKeys.NOTE_PREVIEW_MAX_LINES]?.let {
                    userPreferences = userPreferences.copy(notePreviewMaxLines = it)
                }
            }
        }
        viewModelScope.launch {
            NoteRepository.getAllNotes(application).collectLatest { items ->
                if (notes.size > 0)
                    notes.clear()
                notes.addAll(items)
            }
        }
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest { items ->
                if (labels.size > 0)
                    labels.clear()
                labels.addAll(items)
            }
        }
    }

    fun onCreateNote(callback: (Int) -> Unit) = viewModelScope.launch {
        // Create a new note and callback the note id.
        val noteId = NoteRepository.insertNote(application, Note(created = System.currentTimeMillis()))
        filter.label?.let { label ->
            // Add label to new note if label is selected.
            NoteRepository.insertNoteLabel(application, NoteLabelJoin(noteId = noteId, labelId = label.id))
        }
        callback(noteId)
    }

    fun onAddLabel(value: String) = viewModelScope.launch {
        LabelRepository.insertLabel(application, Label(value = value))
    }

    fun onQuery(value: String) {
        query = value
    }

    fun onFilter(value: NoteFilter) {
        filter = value
    }

    fun onChangeView() = viewModelScope.launch {
        application.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_GRID_VIEW] = !userPreferences.isGridView
        }
    }
}