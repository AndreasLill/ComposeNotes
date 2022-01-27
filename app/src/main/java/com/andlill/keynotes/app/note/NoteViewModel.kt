package com.andlill.keynotes.app.note

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.model.Note
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    var id by mutableStateOf(0)
    var title by mutableStateOf("")
    var body by mutableStateOf("")
    var created by mutableStateOf(0L)
    var modified by mutableStateOf(0L)
    var color by mutableStateOf(0xFFFFFFFF)

    fun loadNote(noteId: Int) = viewModelScope.launch {
        NoteRepository.getNote(getApplication(), noteId).collectLatest {
            it?.let { note ->
                id = note.id
                title = note.title
                body = note.body
                created = note.created
                modified = note.modified
                color = note.color
            }
        }
    }

    fun deleteNote() = viewModelScope.launch {
        val note = Note(
            id = id,
            title = title,
            body = body,
            created = created,
            modified = modified,
            color = color,
        )
        NoteRepository.deleteNote(getApplication(), note)
    }

    fun saveNote() = viewModelScope.launch {
        // Don't save if a new note with no content.
        if (title.isEmpty() && body.isEmpty() && created == 0L)
            return@launch

        val note = Note(
            id = id,
            title = title,
            body = body,
            // Set created timestamp if this is a new note.
            created = if (created == 0L) Calendar.getInstance().timeInMillis else created,
            // Set modified timestamp.
            modified = Calendar.getInstance().timeInMillis,
            color = color,
        )
        NoteRepository.insertNote(getApplication(), note)
    }

    fun randomColor() {
        val colors = listOf(0xFFEF9A9A, 0xFFF48FB1, 0xFFCE93D8, 0xFFB39DDB, 0xFF9FA8DA, 0xFF90CAF9, 0xFF81D4FA, 0xFF80DEEA, 0xFF80CBC4, 0xFFA5D6A7, 0xFFC5E1A5, 0xFFE6EE9C, 0xFFFFF59D, 0xFFFFE082, 0xFFFFCC80, 0xFFFFAB91)
        color = colors.random()
    }
}