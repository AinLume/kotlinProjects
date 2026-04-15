package com.example.prac1.presentation.detail

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

class DiaryDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DiaryRepository(application)

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    private val _savedEntry = MutableStateFlow<DiaryEntry?>(null)
    val savedEntry: StateFlow<DiaryEntry?> = _savedEntry.asStateFlow()

    private var editingFileName: String? = null

    fun loadEntry(fileName: String) {
        editingFileName = fileName
        viewModelScope.launch {

            val (t, body) = withContext(Dispatchers.IO) {
                repository.readEntry(fileName)
            }

            _title.value = t
            _text.value = body
        }
    }

    fun onTitleChanged(value: String) { _title.value = value }
    fun onTextChanged(value: String) { _text.value = value }

    fun save() {
        val currentTitle = _title.value
        val currentText = _text.value
        if (currentText.isBlank()) return

        viewModelScope.launch {

            val entry = withContext(Dispatchers.IO) {

                val existingFile = editingFileName

                if (existingFile != null) {
                    repository.updateEntry(existingFile, currentTitle, currentText)
                } else {
                    repository.saveEntry(currentTitle, currentText)
                }
            }
            _savedEntry.value = entry
        }
    }
}