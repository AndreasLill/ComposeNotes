package com.andlill.keynotes.app.label

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andlill.keynotes.data.repository.LabelRepository
import com.andlill.keynotes.model.Label
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LabelViewModel(private val application: Application) : ViewModel() {
    class Factory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = LabelViewModel(application) as T
    }

    var labels by mutableStateOf(emptyList<Label>())
        private set

    init {
        viewModelScope.launch {
            LabelRepository.getAllLabels(application).collectLatest {
                labels = it.sortedBy { label -> label.value.lowercase() }
            }
        }
    }

    fun onUpdateLabel(label: Label) = viewModelScope.launch {
        LabelRepository.updateLabel(application, label)
    }

    fun onDeleteLabel(label: Label) = viewModelScope.launch {
        LabelRepository.deleteLabel(application, label)
    }

    fun onCreateLabel(label: Label) = viewModelScope.launch {
        LabelRepository.insertLabel(application, label)
    }
}