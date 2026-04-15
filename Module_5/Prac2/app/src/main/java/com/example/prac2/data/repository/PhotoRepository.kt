package com.example.prac2.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.net.Uri
import com.example.prac2.data.model.PhotoEntry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoRepository(private val context: Context) {

    private val picturesDir: File
        get() = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

    fun loadAllPhotos(): List<PhotoEntry> {
        return picturesDir
            .listFiles { file -> file.extension.lowercase() == "jpg" }
            ?.map { PhotoEntry(file = it, name = it.name, dateModified = it.lastModified()) }
            ?.sortedByDescending { it.dateModified }
            ?: emptyList()
    }

    fun createPhotoUri(): Pair<Uri, File> {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(picturesDir, "IMG_${timeStamp}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return uri to file
    }

    fun exportToGallery(photo: PhotoEntry): Boolean {
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, photo.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Gallery")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return false

            context.contentResolver.openOutputStream(uri)?.use { output ->
                photo.file.inputStream().use { it.copyTo(output) }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(
                uri,
                contentValues,
                null,
                null
            )

            true
        } catch (e: Exception) {
            false
        }
    }
}