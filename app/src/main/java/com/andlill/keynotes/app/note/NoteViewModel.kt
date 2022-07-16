package com.andlill.keynotes.app.note

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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

    private var note = NoteWrapper()
    private var deleteOnClose = false

    var allLabels by mutableStateOf(emptyList<Label>())
        private set
    var noteLabels by mutableStateOf(emptyList<Label>())
        private set
    var modifiedDate by mutableStateOf("")
        private set
    var titleText by mutableStateOf(TextFieldValue())
        private set
    var bodyText by mutableStateOf(TextFieldValue())
        private set
    var isPinned by mutableStateOf(false)
        private set
    var isDeleted by mutableStateOf(false)
        private set
    var reminder by mutableStateOf<Long?>(null)
        private set
    var color by mutableStateOf(0)
        private set

    init {
        viewModelScope.launch {
            NoteRepository.getNote(application, noteId).collectLatest {
                it?.let {
                    note = it.copy()
                    noteLabels = it.labels
                    isPinned = it.note.pinned
                    isDeleted = it.note.deleted
                    reminder = it.note.reminder
                    color = it.note.color

                    if (titleText.text.isEmpty())
                        titleText = titleText.copy(text = it.note.title)
                    if (bodyText.text.isEmpty())
                        bodyText = bodyText.copy(text = it.note.body)
                    it.note.modified?.let { modified ->
                        modifiedDate = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault()).format(modified)
                    }
                }
            }
        }
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest {
                allLabels = it.sortedBy { label -> label.value.lowercase(Locale.ROOT) }
            }
        }
    }

    fun onClose() = viewModelScope.launch {
        // Delete note with null created date.
        if (deleteOnClose) {
            onCancelReminder()
            NoteRepository.deleteNote(application, note.note)
            return@launch
        }

        // Delete new note without any modifications.
        if (modifiedDate.isEmpty() && titleText.text.trim().isEmpty() && bodyText.text.trim().isEmpty()) {
            onCancelReminder()
            NoteRepository.deleteNote(application, note.note)
            return@launch
        }

        // Don't save note without changes.
        if (titleText.text.trim() == note.note.title.trim() &&
            bodyText.text.trim() == note.note.body.trim()) {
            return@launch
        }

        // Save note.
        NoteRepository.updateNote(application, note.note.copy(
            title = titleText.text.trim(),
            body = bodyText.text.trim(),
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

    fun onChangeTitle(value: TextFieldValue) {
        titleText = value
    }

    fun onChangeBody(value: TextFieldValue) {
        bodyText = value
    }

    fun setBodySelectionEnd() {
        bodyText = bodyText.copy(selection = TextRange(bodyText.text.length))
    }

    fun onDeletePermanently() {
        deleteOnClose = true
    }

    fun onTogglePin() = viewModelScope.launch {
        NoteRepository.updateNote(application, note.note.copy(pinned = !note.note.pinned))
    }

    fun onChangeColor(value: Int) = viewModelScope.launch {
        NoteRepository.updateNote(application, note.note.copy(color = value))
    }

    fun onDeleteNote() = viewModelScope.launch {
        NoteRepository.updateNote(application, note.note.copy(deleted = true))
        onCancelReminder()
    }

    fun onRestore() = viewModelScope.launch {
        NoteRepository.updateNote(application, note.note.copy(deleted = false))
    }

    private fun onSetReminder(calendar: Calendar) = viewModelScope.launch {
        NoteBroadcaster.setReminder(application, calendar, note.note.id)
        NoteRepository.updateNote(application, note.note.copy(reminder = calendar.timeInMillis))
    }

    private fun onCancelReminder() = viewModelScope.launch {
        NoteBroadcaster.cancelReminder(application, note.note.id)
        NoteRepository.updateNote(application, note.note.copy(reminder = null))
    }
}