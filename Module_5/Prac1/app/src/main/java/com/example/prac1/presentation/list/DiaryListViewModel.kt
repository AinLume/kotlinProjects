package com.example.prac1.presentation.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.prac1.data.model.DiaryEntry
import com.example.prac1.data.repository.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DiaryRepository(application)

    private val _entries = MutableStateFlow<List<DiaryEntry>>(emptyList())
    val entries: StateFlow<List<DiaryEntry>> = _entries.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {

            val loaded = withContext(Dispatchers.IO) {
                repository.loadAllEntries()
            }

            _entries.value = loaded
            _isLoading.value = false
        }
    }

    fun onEntryAdded(entry: DiaryEntry) {
        _entries.value = listOf(entry) + _entries.value
    }

    fun onEntryUpdated(updatedEntry: DiaryEntry) {
        _entries.value = _entries.value.map {
            if (it.fileName == updatedEntry.fileName) updatedEntry else it
        }
    }

    fun deleteEntry(entry: DiaryEntry) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                repository.deleteEntry(entry.fileName)
            }

            _entries.value = _entries.value.filter {
                it.fileName != entry.fileName
            }
        }
    }
}