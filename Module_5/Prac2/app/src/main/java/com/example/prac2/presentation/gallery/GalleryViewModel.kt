package com.example.prac2.presentation.gallery

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.prac2.data.model.PhotoEntry
import com.example.prac2.data.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PhotoRepository(application)

    private val _photos = MutableStateFlow<List<PhotoEntry>>(emptyList())
    val photos: StateFlow<List<PhotoEntry>> = _photos.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var pendingPhotoFile: File? = null

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) { repository.loadAllPhotos() }
            _photos.value = loaded
            _isLoading.value = false
        }
    }

    fun createPhotoUri(): Uri {
        val (uri, file) = repository.createPhotoUri()
        pendingPhotoFile = file
        return uri
    }

    fun onPhotoCaptured() {
        val file = pendingPhotoFile ?: return
        if (file.exists() && file.length() > 0) {
            val entry = PhotoEntry(file = file, name = file.name, dateModified = file.lastModified())
            _photos.value = listOf(entry) + _photos.value
        }
        pendingPhotoFile = null
    }

    fun onPhotoCancelled() {
        pendingPhotoFile?.delete()
        pendingPhotoFile = null
    }

    fun exportToGallery(photo: PhotoEntry) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) { repository.exportToGallery(photo) }
            _snackbarMessage.value = if (success) "Фото добавлено в галерею" else "Ошибка экспорта"
        }
    }

    fun snackbarShown() { _snackbarMessage.value = null }
}