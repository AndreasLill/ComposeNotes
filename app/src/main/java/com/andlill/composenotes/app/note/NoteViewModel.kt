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
import com.andlill.composenotes.model.Note
import com.andlill.composenotes.model.NoteCheckBox
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

    private var loaded = false
    private var deleteOnClose = false

    var id by mutableStateOf(noteId)
        private set
    var color by mutableStateOf(0)
        private set
    var title by mutableStateOf(TextFieldValue())
        private set
    var body by mutableStateOf(TextFieldValue())
        private set
    var created by mutableStateOf<Long?>(null)
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
                color = it.note.color
                title = title.copy(text = it.note.title)
                body = body.copy(text = it.note.body)
                created = it.note.created
                modified = it.note.modified
                deletion = it.note.deletion
                pinned = it.note.pinned

                checkBoxes.clear()
                checkBoxes.addAll(it.checkBoxes)
                loaded = true
            }
        }
        viewModelScope.launch {
            // Get all note labels as flow.
            NoteRepository.getNote(application, noteId).collectLatest {
                it?.let {
                    labels.clear()
                    labels.addAll(it.labels)
                }
            }
        }
        viewModelScope.launch {
            // Get note reminder as flow.
            NoteRepository.getNoteReminder(application, noteId).collectLatest {
                reminder = it
            }
        }
    }

    fun onClose() = viewModelScope.launch {
        if (!loaded)
            return@launch

        if (deleteOnClose) {
            NoteBroadcaster.cancelReminder(application, id)
            NoteRepository.deleteNote(application, id)
            return@launch
        }
        else {
            NoteRepository.updateNote(application, Note(
                id = id,
                color = color,
                title = title.text.trim(),
                body = body.text.trim(),
                created = created,
                modified = System.currentTimeMillis(),
                reminder = reminder,
                deletion = deletion,
                pinned = pinned
            ))
            NoteRepository.updateNoteCheckBoxes(application, id, checkBoxes.map {
                // Set id to 0 if using a temp id (under 0).
                if (it.id < 0)
                    it.copy(id = 0, text = it.text.trim())
                else
                    it.copy(text = it.text.trim())
            })
        }
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

    fun onSetReminder(dateTime: LocalDateTime) = viewModelScope.launch {
        NoteRepository.setNoteReminder(application, noteId, dateTime.toMilliSeconds())
        NoteBroadcaster.setReminder(application, dateTime.toMilliSeconds(), id)
    }

    fun onCancelReminder() = viewModelScope.launch {
        NoteRepository.setNoteReminder(application, noteId, null)
        NoteBroadcaster.cancelReminder(application, id)
    }

    fun onTogglePin() {
        pinned = !pinned
    }

    fun onChangeColor(value: Int) {
        color = value
    }

    fun onDeleteNote() {
        // Set deletion date to 7 days from now and move to "trash".
        val deletionTime = LocalDateTime.now().plusDays(7).toMilliSeconds()
        reminder = null
        deletion = deletionTime
        NoteBroadcaster.cancelReminder(application, id)
    }

    fun onRestore() {
        deletion = null
    }

    fun onConvertCheckBoxes() = viewModelScope.launch {
        if (checkBoxes.isEmpty()) {
            // Convert body string to check boxes.
            val list = ArrayList<NoteCheckBox>()
            body.text.lines().forEachIndexed { index, line ->
                if (line.isBlank())
                    return@forEachIndexed
                list.add(NoteCheckBox(id = Random.nextInt(), noteId = id, text = line, order = index + 1))
            }
            // Add blank checkbox if list is empty.
            if (list.isEmpty())
                list.add(NoteCheckBox(id = Random.nextInt(), noteId = id, order = 1))

            body = body.copy(text = "")
            checkBoxes.addAll(list)
        }
        else {
            // Convert check boxes to body string.
            var bodyStr = ""
            checkBoxes.forEach { checkBox ->
                if (checkBox.text.isBlank())
                    return@forEach
                bodyStr += checkBox.text.plus(System.lineSeparator())
            }
            body = body.copy(text = bodyStr)
            checkBoxes.clear()
        }
    }

    fun onCreateCheckBox(position: Int, checked: Boolean) {
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
        checkBoxes.add(NoteCheckBox(id = -Random.nextInt(), noteId = id, order = position, checked = checked))
    }

    fun onDeleteCheckBox(id: Int) {
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

    fun onEditCheckBox(id: Int, checked: Boolean, text: String) {
        val item = checkBoxes.firstOrNull { it.id == id }
        item?.let {
            val index = checkBoxes.indexOf(it)
            checkBoxes[index] = checkBoxes[index].copy(checked = checked, text = text)
        }
    }
}