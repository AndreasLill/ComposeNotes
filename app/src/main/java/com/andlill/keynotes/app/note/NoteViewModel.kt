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
import com.andlill.keynotes.model.Note
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
    private var _note = Note()

    // Cached copy of note to edit to prevent too many database calls.
    var note by mutableStateOf(Note())
        private set
    // All labels.
    var labels by mutableStateOf(emptyList<Label>())
        private set

    var modifiedDate by mutableStateOf("-")

    init {
        viewModelScope.launch {
            NoteRepository.getNote(application, noteId).collectLatest {
                it?.let {
                    _note = it.copy()
                    note = it.copy()
                    it.modified?.let { modified ->
                        modifiedDate = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault()).format(modified)
                    }
                }
            }
        }
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest {
                labels = it
            }
        }
    }

    fun onClose() = viewModelScope.launch {
        // Delete note with null created date.
        if (note.created == null) {
            NoteRepository.deleteNote(application, note)
            return@launch
        }

        // Delete new note without any modifications.
        if (note.modified == null && note.title.isEmpty() && note.body.isEmpty()) {
            NoteRepository.deleteNote(application, note)
            return@launch
        }

        // Don't save note without changes.
        if (note.title.trim() == _note.title.trim() &&
            note.body.trim() == _note.body.trim() &&
            note.color == _note.color &&
            note.deleted == _note.deleted &&
            note.pinned == _note.pinned &&
            note.reminder == _note.reminder &&
            note.label == _note.label) {
            return@launch
        }

        // Save note.
        NoteRepository.insertNote(application, note.copy(
            title = note.title.trim(),
            body = note.body.trim(),
            modified = Calendar.getInstance().timeInMillis,
        ))
    }

    fun onToggleLabel(label: Label) {
        if (note.label == label.id) {
            // Remove label if same was selected.
            note = note.copy(label = null)
        }
        else {
            // Change label if a different was selected.
            note = note.copy(label = label.id)
        }
    }

    fun onToggleReminder(calendar: Calendar?) {
        if (calendar != null)
            onSetReminder(calendar)
        else
            onCancelReminder()
    }

    fun onTogglePin() {
        note = note.copy(pinned = !note.pinned)
    }

    fun onChangeColor(value: Int) {
        note = note.copy(color = value)
    }

    fun onChangeTitle(value: String) {
        note = note.copy(title = value)
    }

    fun onChangeBody(value: String) {
        note = note.copy(body = value)
    }

    fun onDeleteNote() {
        // Set the note to deleted status.
        note = note.copy(deleted = true)
        onCancelReminder()
    }

    fun onDeletePermanently() {
        // Delete the note forever.
        note = note.copy(created = null)
        onCancelReminder()
    }

    fun onRestore() {
        // Restore the deleted note.
        note = note.copy(deleted = false)
    }

    private fun onSetReminder(calendar: Calendar) = viewModelScope.launch {
        note = note.copy(reminder = calendar.timeInMillis)
        NoteBroadcaster.setReminder(application, calendar, note)
    }

    private fun onCancelReminder() = viewModelScope.launch {
        note = note.copy(reminder = null)
        NoteBroadcaster.cancelReminder(application, note)
    }
}