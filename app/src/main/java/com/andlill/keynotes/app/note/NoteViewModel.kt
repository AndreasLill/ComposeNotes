package com.andlill.keynotes.app.note

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.model.Note
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class NoteViewModel(private val application: Application, private val noteId: Int) : ViewModel() {
    class Factory(private val application: Application, private val noteId: Int) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = NoteViewModel(application, noteId) as T
    }

    var note by mutableStateOf(Note())
        private set

    init {
        viewModelScope.launch {
            NoteRepository.getNote(application, noteId).collectLatest {
                it?.let {
                    note = it.copy()
                }
            }
        }
    }

    fun onClose() = viewModelScope.launch {
        // Don't save new note with no content, cancel any reminder.
        if (note.title.isEmpty() && note.body.isEmpty() && note.modified == null) {
            onCancelReminder()
            return@launch
        }

        // Cancel reminder if note was labeled as deleted.
        if (note.deleted)
            onCancelReminder()

        // Save note on close.
        NoteRepository.insertNote(application, note.copy(
            title = note.title.trim(),
            body = note.body.trim(),
            modified = Calendar.getInstance().timeInMillis,
        ))
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

    fun onDeleteNote() = viewModelScope.launch {
        note = note.copy(deleted = true)
    }

    fun onSetReminder(calendar: Calendar) {
        note = note.copy(reminder = calendar.timeInMillis)
        NoteBroadcaster.setReminder(application, calendar, note)
    }

    fun onCancelReminder() {
        note = note.copy(reminder = null)
        NoteBroadcaster.cancelReminder(application, note)
    }
}