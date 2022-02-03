package com.andlill.keynotes.app.note

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.model.Note
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private var deleted = false
    var note by mutableStateOf(Note())

    fun loadNote(noteId: Int) = viewModelScope.launch {
        NoteRepository.getNote(getApplication(), noteId).collectLatest {
            it?.let {
                note = it.copy()
            }
        }
    }

    fun onClose() = viewModelScope.launch {
        // Don't save deleted note, cancel any reminder.
        if (deleted) {
            cancelReminder()
            return@launch
        }
        // Don't save new note with no content, cancel any reminder.
        if (note.title.isEmpty() && note.body.isEmpty() && note.created == null) {
            cancelReminder()
            return@launch
        }

        saveNote()
    }

    fun deleteNote() = viewModelScope.launch {
        // Set deleted to avoid saving on close.
        deleted = true
        NoteRepository.deleteNote(getApplication(), note)
    }

    private suspend fun saveNote() {
        NoteRepository.insertNote(getApplication(), note.copy(
            title = note.title.trim(),
            body = note.body.trim(),
            // Set created timestamp if this is a new note.
            created = note.created ?: Calendar.getInstance().timeInMillis,
            // Set modified timestamp.
            modified = Calendar.getInstance().timeInMillis,
        ))
    }

    fun setReminder(calendar: Calendar) {
        note = note.copy(reminder = calendar.timeInMillis)
        NoteBroadcaster.setAlarm(getApplication(), calendar, note)
    }

    fun cancelReminder() {
        note = note.copy(reminder = null)
        NoteBroadcaster.cancelAlarm(getApplication(), note)
    }
}