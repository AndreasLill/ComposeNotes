package com.andlill.composenotes.app.note

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.composenotes.data.repository.NoteRepository
import com.andlill.composenotes.model.Label
import com.andlill.composenotes.model.NoteCheckBox
import com.andlill.composenotes.model.NoteWrapper
import com.andlill.composenotes.utils.TimeUtils.toMilliSeconds
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class NoteViewModel(private val application: Application, private val noteId: Int) : ViewModel() {
    class Factory(private val application: Application, private val noteId: Int) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = NoteViewModel(application, noteId) as T
    }

    // Original note before edits.
    private var noteOriginal: NoteWrapper? = null
    private var deleteOnClose = false

    var id by mutableStateOf(noteId)
        private set
    var color by mutableStateOf(0)
        private set
    var title by mutableStateOf(TextFieldValue())
        private set
    var body by mutableStateOf(TextFieldValue())
        private set
    var reminderRepeat by mutableStateOf<String?>(null)
        private set
    var modified by mutableStateOf<Long?>(null)
        private set
    var reminder by mutableStateOf<Long?>(null)
        private set
    var deletion by mutableStateOf<Long?>(null)
        private set
    var pinned by mutableStateOf(false)
        private set
    var checkBoxes = mutableStateListOf<NoteCheckBox>()
        private set
    var labels = mutableStateListOf<Label>()
        private set

    init {
        viewModelScope.launch {
            // Get note once and use as cache.
            NoteRepository.getNote(application, noteId).firstOrNull()?.let {
                title = title.copy(text = it.note.title)
                body = body.copy(text = it.note.body)
                checkBoxes.clear()
                checkBoxes.addAll(it.checkBoxes)
                noteOriginal = it
            }
        }
        viewModelScope.launch {
            // Get other note data as flow.
            NoteRepository.getNote(application, noteId).collectLatest {
                it?.let {
                    color = it.note.color
                    modified = it.note.modified
                    deletion = it.note.deletion
                    pinned = it.note.pinned
                    labels.clear()
                    labels.addAll(it.labels)
                    reminder = it.note.reminder
                    reminderRepeat = it.note.reminderRepeat
                }
            }
        }
    }

    fun onClose() = viewModelScope.launch {
        if (noteOriginal == null)
            return@launch

        if (deleteOnClose) {
            NoteBroadcaster.cancelReminder(application, id)
            NoteRepository.deleteNote(application, id)
            return@launch
        }
        else {
            if (isModified()) {
                NoteRepository.setNoteContent(
                    application,
                    id,
                    title.text.trim(),
                    body.text.trim(),
                    System.currentTimeMillis()
                )
                NoteRepository.updateNoteCheckBoxes(application, id, checkBoxes.map {
                    // Set id to 0 if using a temp id (under 0).
                    if (it.id < 0)
                        it.copy(id = 0, text = it.text.trim())
                    else
                        it.copy(text = it.text.trim())
                })
            }
        }
    }

    private fun isModified(): Boolean {
        noteOriginal?.let {
            if (title.text.trim() != it.note.title.trim())
                return true
            if (body.text.trim() != it.note.body.trim())
                return true
            if (it.checkBoxes != checkBoxes)
                return true
        }

        return false
    }

    fun onChangeTitle(value: TextFieldValue) {
        title = value
    }

    fun onChangeBody(value: TextFieldValue) {
        body = value
    }

    fun setBodySelectionEnd() {
        body = body.copy(selection = TextRange(body.text.length))
    }

    fun onDeletePermanently() {
        deleteOnClose = true
    }

    fun onSetReminder(dateTime: LocalDateTime, repeat: String?) = viewModelScope.launch {
        NoteRepository.setNoteReminderWithRepeat(application, id, dateTime.toMilliSeconds(), repeat)
        NoteBroadcaster.setReminder(application, id, dateTime.toMilliSeconds())
    }

    fun onCancelReminder() = viewModelScope.launch {
        NoteRepository.setNoteReminderWithRepeat(application, id, null, null)
        NoteBroadcaster.cancelReminder(application, id)
    }

    fun onTogglePin() = viewModelScope.launch {
        NoteRepository.setNotePinned(application, id, !pinned)
    }

    fun onChangeColor(value: Int) = viewModelScope.launch {
        NoteRepository.setNoteColor(application, id, value)
    }

    fun onDeleteNote() = viewModelScope.launch {
        // Set deletion date to 7 days from now and move to "trash".
        val deletionTime = LocalDateTime.now().plusDays(7).toMilliSeconds()
        NoteRepository.setNoteReminderWithRepeat(application, id, null, null)
        NoteRepository.setNoteDeletion(application, id, deletionTime)
        NoteBroadcaster.cancelReminder(application, id)
    }

    fun onRestore() = viewModelScope.launch {
        NoteRepository.setNoteDeletion(application, id, null)
    }

    fun onConvertCheckBoxes() = viewModelScope.launch {
        if (checkBoxes.isEmpty()) {
            // Convert body string to check boxes.
            val list = ArrayList<NoteCheckBox>()
            body.text.lines().forEachIndexed { index, line ->
                if (line.isBlank())
                    return@forEachIndexed
                list.add(NoteCheckBox(id = Random.nextInt(Int.MIN_VALUE, -1), noteId = id, text = line, order = index + 1))
            }
            // Add blank checkbox if list is empty.
            if (list.isEmpty())
                list.add(NoteCheckBox(id = Random.nextInt(Int.MIN_VALUE, -1), noteId = id, order = 1))

            body = body.copy(text = "")
            checkBoxes.addAll(list)
        }
        else {
            // Convert check boxes to body string.
            var bodyStr = ""
            checkBoxes.sortedBy { it.order }.forEach { checkBox ->
                if (checkBox.text.isBlank())
                    return@forEach
                bodyStr += checkBox.text.plus(System.lineSeparator())
            }
            body = body.copy(text = bodyStr)
            checkBoxes.clear()
        }
    }

    fun onCreateCheckBox(position: Int, checked: Boolean) = viewModelScope.launch {
        // Increment order on subsequent items in list.
        val list = checkBoxes.map {
            if (it.order >= position)
                it.copy(order = it.order + 1)
            else
                it
        }
        checkBoxes.clear()
        checkBoxes.addAll(list)

        // Assign random temporary id to use as stable key in lazy column.
        checkBoxes.add(NoteCheckBox(id = Random.nextInt(Int.MIN_VALUE, -1), noteId = id, order = position, checked = checked))
    }

    fun onDeleteCheckBox(id: Int) = viewModelScope.launch {
        val item = checkBoxes.firstOrNull { it.id == id }
        item?.let { checkBox ->
            checkBoxes.remove(checkBox)

            // Reduce order on subsequent after removing.
            val list = checkBoxes.map {
                if (it.order >= checkBox.order)
                    it.copy(order = it.order - 1)
                else
                    it
            }
            checkBoxes.clear()
            checkBoxes.addAll(list)
        }
    }

    fun onEditCheckBox(id: Int, checked: Boolean, text: String) = viewModelScope.launch {
        val item = checkBoxes.firstOrNull { it.id == id }
        item?.let {
            val index = checkBoxes.indexOf(it)
            checkBoxes[index] = checkBoxes[index].copy(checked = checked, text = text)
        }
    }
}