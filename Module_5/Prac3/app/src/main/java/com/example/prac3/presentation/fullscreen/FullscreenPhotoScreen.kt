package com.example.prac3.presentation.fullscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.prac3.data.model.PhotoEntry
import com.example.prac3.data.repository.PhotoRepository
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenPhotoScreen(
    encodedPath: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val filePath = URLDecoder.decode(encodedPath, "UTF-8")
    val file = File(filePath)
    val photo = PhotoEntry(file = file, name = file.name, dateModified = file.lastModified())

    val repository = remember { PhotoRepository(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun exportPhoto() {
        scope.launch {
            val success = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                repository.exportToGallery(photo)
            }
            snackbarHostState.showSnackbar(
                if (success) "Фото добавлено в галерею" else "Ошибка экспорта"
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
        ) {
            AsyncImage(
                model = file,
                contentDescription = file.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White
                )
            }

            Button(
                onClick = { exportPhoto() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.85f),
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Экспорт в галерею")
            }
        }
    }
}