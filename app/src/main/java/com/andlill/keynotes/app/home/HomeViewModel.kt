package com.andlill.keynotes.app.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.andlill.keynotes.data.repository.NoteRepository
import com.andlill.keynotes.model.Note
import kotlinx.coroutines.flow.Flow

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    fun getNotes(): Flow<List<Note>> {
        return NoteRepository.getAllNotes(getApplication())
    }
}