package com.andlill.composenotes.app.note

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.composenotes.R
import com.andlill.composenotes.data.repository.NoteRepository
import com.andlill.composenotes.model.Note
import com.andlill.composenotes.model.NoteCheckBox
import com.andlill.composenotes.utils.TimeUtils.daysBetween
import com.andlill.composenotes.utils.TimeUtils.toDateString
import com.andlill.composenotes.utils.TimeUtils.toMilliSeconds
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

class NoteViewModel(private val application: Application, private val noteId: Int) : ViewModel() {
    class Factory(private val application: Application, private val noteId: Int) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = NoteViewModel(application, noteId) as T
    }

    private var note = Note()
    private var deleteOnClose = false
    //private var undoLock = false

    var id by mutableStateOf(noteId)
        private set
    var titleText by mutableStateOf(TextFieldValue())
        private set
    var bodyText by mutableStateOf(TextFieldValue())
        private set
    var isPinned by mutableStateOf(false)
        private set
    var reminder by mutableStateOf<Long?>(null)
        private set
    var modified by mutableStateOf<Long?>(null)
        private set
    var deletion by mutableStateOf<Long?>(null)
        private set
    var color by mutableStateOf(0)
        private set
    var statusText by mutableStateOf("")
        private set
    var checkBoxes by mutableStateOf(emptyList<NoteCheckBox>())
        private set

    init {
        viewModelScope.launch {
            NoteRepository.getNote(application, noteId).collectLatest {
                it?.let {
                    note = it.note.copy()
                    isPinned = it.note.pinned
                    reminder = it.note.reminder
                    modified = it.note.modified
                    deletion = it.note.deletion
                    color = it.note.color
                    checkBoxes = it.checkBoxes.sortedBy { box -> box.checked }
                    titleText = titleText.copy(text = it.note.title)
                    bodyText = bodyText.copy(text = it.note.body)

                    if (deletion != null) {
                        // Set status text days until deletion.
                        deletion?.let { value ->
                            statusText = when (value.daysBetween()) {
                                0 -> application.resources.getString(R.string.note_screen_status_text_deletion_today)
                                1 -> application.resources.getString(R.string.note_screen_status_text_deletion_tomorrow)
                                else -> String.format(application.resources.getString(R.string.note_screen_status_text_deletion_days), value.daysBetween())
                            }
                        }
                    }
                    else {
                        // Set status text days since modified.
                        modified?.let { value ->
                            val days = value.daysBetween()
                            statusText = when {
                                days == 0 -> String.format("%s, %s", application.resources.getString(R.string.date_today), value.toDateString("HH:mm"))
                                days == -1 -> String.format("%s, %s", application.resources.getString(R.string.date_yesterday), value.toDateString("HH:mm"))
                                days < -365 -> value.toDateString("d MMM YYYY, HH:mm")
                                else -> value.toDateString("d MMM, HH:mm")
                            }
                        }
                    }
                }
            }
        }
    }

    fun onClose() = viewModelScope.launch {
        // Delete note with null created date.
        if (deleteOnClose) {
            NoteBroadcaster.cancelReminder(application, note.id)
            NoteRepository.deleteNote(application, note)
            return@launch
        }

        // Delete new note without any modifications.
        if (modified == null && titleText.text.trim().isEmpty() && bodyText.text.trim().isEmpty()) {
            NoteBroadcaster.cancelReminder(application, note.id)
            NoteRepository.deleteNote(application, note)
            return@launch
        }

        // Don't save note without changes.
        if (titleText.text.trim() == note.title.trim() &&
            bodyText.text.trim() == note.body.trim()) {
            return@launch
        }

        // Save note.
        NoteRepository.updateNote(application, note.copy(
            title = titleText.text.trim(),
            body = bodyText.text.trim(),
            modified = System.currentTimeMillis(),
        ))
    }

    fun onChangeTitle(value: TextFieldValue) {
        titleText = value
    }

    fun onChangeBody(value: TextFieldValue) {
        /*
        if (bodyText.text != value.text) {
            // Add to undo list if text was changed.
            if (!undoLock) {
                // Lock the undo for a short time to prevent saving too much undo data.
                undoLock = true
                undoList = undoList.plus(bodyText)
                // Unlock undo after a small delay.
                viewModelScope.launch {
                    delay(750)
                    undoLock = false
                }
            }
        }
        */
        bodyText = value
    }

    fun setBodySelectionEnd() {
        bodyText = bodyText.copy(selection = TextRange(bodyText.text.length))
    }

    fun onDeletePermanently() {
        deleteOnClose = true
    }

    fun onUpdateReminder(calendar: Calendar?) = viewModelScope.launch {
        if (calendar != null) {
            NoteRepository.updateNote(application, note.copy(reminder = calendar.timeInMillis))
            NoteBroadcaster.setReminder(application, calendar.timeInMillis, note.id)
        }
        else {
            NoteRepository.updateNote(application, note.copy(reminder = null))
            NoteBroadcaster.cancelReminder(application, note.id)
        }
    }

    fun onTogglePin() = viewModelScope.launch {
        NoteRepository.updateNote(application, note.copy(pinned = !note.pinned))
    }

    fun onChangeColor(value: Int) = viewModelScope.launch {
        NoteRepository.updateNote(application, note.copy(color = value))
    }

    fun onDeleteNote() = viewModelScope.launch {
        // Set deletion date to 7 days from now and move to "trash".
        val deletionTime = LocalDateTime.now().plusDays(7).toMilliSeconds()
        NoteRepository.updateNote(application, note.copy(reminder = null, deletion = deletionTime))
        NoteBroadcaster.cancelReminder(application, note.id)
    }

    fun onRestore() = viewModelScope.launch {
        NoteRepository.updateNote(application, note.copy(deletion = null))
    }

    fun onConvertCheckBoxes() = viewModelScope.launch {
        if (checkBoxes.isEmpty()) {
            // Convert body string to check boxes.
            val list = ArrayList<NoteCheckBox>()
            bodyText.text.lines().forEach { line ->
                if (line.isBlank())
                    return@forEach
                list.add(NoteCheckBox(noteId = note.id, text = line))
            }
            NoteRepository.insertNoteCheckBoxes(application, list)
            NoteRepository.updateNote(application, note.copy(body = "", modified = System.currentTimeMillis()))
        }
        else {
            // Convert check boxes to body string.
            var bodyStr = ""
            checkBoxes.forEach { checkBox ->
                if (checkBox.text.isBlank())
                    return@forEach
                bodyStr += checkBox.text.plus(System.lineSeparator())
            }
            NoteRepository.deleteAllNoteCheckBoxes(application, note.id)
            NoteRepository.updateNote(application, note.copy(body = bodyStr, modified = System.currentTimeMillis()))
        }
    }

    fun onCreateCheckBox(onDone: () -> Unit) = viewModelScope.launch {
        NoteRepository.insertNoteCheckBox(application, NoteCheckBox(noteId = note.id))
        onDone()
    }

    fun onDeleteCheckBox(item: NoteCheckBox) = viewModelScope.launch {
        NoteRepository.deleteNoteCheckBox(application, item)
    }

    fun onEditCheckBox(id: Int, checked: Boolean, text: String) = viewModelScope.launch {
        // Ignore if contents are the same.
        val checkBox = checkBoxes.find { it.id == id }
        checkBox?.let {
            if (checked == it.checked && text == it.text)
                return@launch
            NoteRepository.insertNoteCheckBox(application, it.copy(checked = checked, text = text))
        }
    }

    /*
    // TODO: Add undo/redo for body and check boxes.
    fun onUndo() {
        // Undo and move action to redo list.
        val undo = undoList.last()
        redoList = redoList.plus(bodyText)
        undoList = undoList.minus(undo)
        bodyText = undo
    }

    fun onRedo() {
        // Redo and move action to undo list.
        val redo = redoList.last()
        undoList = undoList.plus(bodyText)
        redoList = redoList.minus(redo)
        bodyText = redo
    }
     */
}