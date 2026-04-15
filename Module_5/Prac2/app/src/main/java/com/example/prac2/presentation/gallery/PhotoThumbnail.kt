package com.example.prac2.presentation.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.prac2.data.model.PhotoEntry

@Composable
fun PhotoThumbnail(
    photo: PhotoEntry,
    onClick: () -> Unit,
    onExport: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        AsyncImage(
            model = photo.file,
            contentDescription = photo.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .clickable(
                    onClick = onClick,
                    onClickLabel = "Открыть фото"
                )
        )

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Экспорт в галерею") },
                onClick = {
                    onExport()
                    showMenu = false
                }
            )
        }

        IconButton(
            onClick = onExport,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp)
                .padding(4.dp)
        ) {
            Icon(
                Icons.Default.AddAPhoto,
                contentDescription = "Экспорт",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}