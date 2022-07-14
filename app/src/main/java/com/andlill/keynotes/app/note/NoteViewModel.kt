package com.andlill.keynotes.app.note

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
import com.andlill.keynotes.model.NoteWrapper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(private val application: Application, private val noteId: Int) : ViewModel() {
    class Factory(private val application: Application, private val noteId: Int) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = NoteViewModel(application, noteId) as T
    }

    // Original note before changes.
    private var _note = NoteWrapper()

    // Cached copy of note to edit to prevent too many database calls.
    var note by mutableStateOf(NoteWrapper())
        private set
    // All labels.
    var labels by mutableStateOf(emptyList<Label>())
        private set

    var modifiedDate by mutableStateOf("")

    init {
        viewModelScope.launch {
            NoteRepository.getNote(application, noteId).collectLatest {
                it?.let {
                    // Check if cached note title or body was edited and apply edit to copy.
                    if (note.note.title.trim() != _note.note.title.trim() || note.note.body.trim() != _note.note.body.trim()) {
                        note = it.copy(note = it.note.copy(title = note.note.title, body = note.note.body))
                    }
                    else {
                        note = it.copy()
                    }
                    _note = it.copy()
                    it.note.modified?.let { modified ->
                        modifiedDate = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault()).format(modified)
                    }
                }
            }
        }
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest {
                labels = it.sortedBy { label -> label.value.lowercase(Locale.ROOT) }
            }
        }
    }

    fun onClose() = viewModelScope.launch {
        // Delete note with null created date.
        if (note.note.created == null) {
            onCancelReminder()
            NoteRepository.deleteNote(application, note.note)
            return@launch
        }

        // Delete new note without any modifications.
        if (note.note.modified == null && note.note.title.isEmpty() && note.note.body.isEmpty()) {
            onCancelReminder()
            NoteRepository.deleteNote(application, note.note)
            return@launch
        }

        // Don't save note without changes.
        if (note.note.title.trim() == _note.note.title.trim() &&
            note.note.body.trim() == _note.note.body.trim()) {
            return@launch
        }

        // Save note.
        NoteRepository.updateNote(application, note.note.copy(
            title = note.note.title.trim(),
            body = note.note.body.trim(),
            modified = Calendar.getInstance().timeInMillis,
        ))
    }

    fun onToggleLabel(label: Label) = viewModelScope.launch {
        if (note.labels.contains(label))
            NoteRepository.deleteNoteLabel(application, NoteLabelJoin(noteId = note.note.id, labelId = label.id))
        else
            NoteRepository.insertNoteLabel(application, NoteLabelJoin(noteId = note.note.id, labelId = label.id))
    }

    fun onToggleReminder(calendar: Calendar?) {
        if (calendar != null)
            onSetReminder(calendar)
        else
            onCancelReminder()
    }

    fun onChangeTitle(value: String) {
        note = note.copy(note = note.note.copy(title = value))
    }

    fun onChangeBody(value: String) {
        note = note.copy(note = note.note.copy(body = value))
    }

    fun onDeletePermanently() {
        note = note.copy(note = note.note.copy(created = null))
    }

    fun onTogglePin() = viewModelScope.launch {
        NoteRepository.updateNote(application, _note.note.copy(pinned = !_note.note.pinned))
    }

    fun onChangeColor(value: Int) = viewModelScope.launch {
        NoteRepository.updateNote(application, _note.note.copy(color = value))
    }

    fun onDeleteNote() = viewModelScope.launch {
        NoteRepository.updateNote(application, _note.note.copy(deleted = true))
        onCancelReminder()
    }

    fun onRestore() = viewModelScope.launch {
        NoteRepository.updateNote(application, _note.note.copy(deleted = false))
    }

    private fun onSetReminder(calendar: Calendar) = viewModelScope.launch {
        NoteBroadcaster.setReminder(application, calendar, _note.note.id)
        NoteRepository.updateNote(application, _note.note.copy(reminder = calendar.timeInMillis))
    }

    private fun onCancelReminder() = viewModelScope.launch {
        NoteBroadcaster.cancelReminder(application, _note.note.id)
        NoteRepository.updateNote(application, _note.note.copy(reminder = null))
    }
}