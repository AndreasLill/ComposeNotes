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

    var id by mutableStateOf(0)
    var title by mutableStateOf("")
    var body by mutableStateOf("")
    var created by mutableStateOf(0L)
    var modified by mutableStateOf(0L)
    var color by mutableStateOf(0)
    var reminder by mutableStateOf(0L)

    fun loadNote(noteId: Int) = viewModelScope.launch {
        NoteRepository.getNoteAsFlow(getApplication(), noteId).collectLatest {
            it?.let { note ->
                id = note.id
                title = note.title
                body = note.body
                created = note.created
                modified = note.modified
                color = note.color
                reminder = note.reminder
            }
        }
    }

    fun deleteNote() = viewModelScope.launch {
        // Set deleted to avoid saving.
        deleted = true

        val note = Note(
            id = id,
            title = title,
            body = body,
            created = created,
            modified = modified,
            color = color,
            reminder = reminder
        )
        NoteRepository.deleteNote(getApplication(), note)
        cancelReminder()
    }

    fun saveNote() = viewModelScope.launch {
        // Don't save deleted note.
        if (deleted)
            return@launch
        // Don't save if a new note with no content.
        if (title.isEmpty() && body.isEmpty() && created == 0L)
            return@launch

        val note = Note(
            id = id,
            title = title.trim(),
            body = body.trim(),
            // Set created timestamp if this is a new note.
            created = if (created == 0L) Calendar.getInstance().timeInMillis else created,
            // Set modified timestamp.
            modified = Calendar.getInstance().timeInMillis,
            color = color,
            reminder = reminder,
        )
        NoteRepository.insertNote(getApplication(), note)
    }

    fun setReminder(calendar: Calendar) {
        reminder = calendar.timeInMillis
        NoteBroadcaster.setAlarm(getApplication(), calendar, id, title + System.lineSeparator() + body, color)
    }

    fun cancelReminder() {
        reminder = 0
        NoteBroadcaster.cancelAlarm(getApplication(), id)
    }
}