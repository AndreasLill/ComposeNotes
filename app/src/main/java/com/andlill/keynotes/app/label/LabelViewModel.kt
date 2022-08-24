package com.andlill.keynotes.app.label

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.keynotes.data.repository.LabelRepository
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.model.NoteLabelJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LabelViewModel(private val application: Application, private val noteId: Int?) : ViewModel() {
    class Factory(private val application: Application, private val noteId: Int?) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = LabelViewModel(application, noteId) as T
    }

    var labels by mutableStateOf(emptyList<Label>())
        private set
    var noteLabels by mutableStateOf(emptyList<Label>())
        private set

    init {
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest {
                labels = it.sortedBy { label -> label.value.lowercase() }
            }
        }
        viewModelScope.launch {
            noteId?.let {
                NoteRepository.getNote(application, noteId).collectLatest {
                    it?.let {
                        noteLabels = it.labels
                    }
                }
            }
        }
    }

    fun onUpdateLabel(label: Label) = viewModelScope.launch {
        LabelRepository.updateLabel(application, label)
    }

    fun onDeleteLabel(label: Label) = viewModelScope.launch {
        LabelRepository.deleteLabel(application, label)
    }

    fun onCreateLabel(label: Label) = viewModelScope.launch {
        LabelRepository.insertLabel(application, label)
    }

    fun onToggleNoteLabel(label: Label) = viewModelScope.launch {
        noteId?.let {
            if (noteLabels.contains(label))
                NoteRepository.deleteNoteLabel(application, NoteLabelJoin(noteId = noteId, labelId = label.id))
            else
                NoteRepository.insertNoteLabel(application, NoteLabelJoin(noteId = noteId, labelId = label.id))
        }
    }
}