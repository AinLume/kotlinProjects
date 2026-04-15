package com.example.prac1.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prac1.data.model.DiaryEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryListScreen(
    onOpenEntry: (String) -> Unit,
    onNewEntry: () -> Unit,
    viewModel: DiaryListViewModel = viewModel()
) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var entryToDelete by remember { mutableStateOf<DiaryEntry?>(null) }


    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Удалить запись?") },
            text = { Text(if (entry.title.isNotBlank()) entry.title else "Запись от ${entry.date}") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEntry(entry)
                    entryToDelete = null
                }) { Text("Удалить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мой дневник") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEntry) {
                Icon(Icons.Default.Add, contentDescription = "Новая запись")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                entries.isEmpty() -> {
                    EmptyState(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(entries, key = { it.fileName }) { entry ->
                            DiaryEntryCard(
                                entry = entry,
                                onClick = { onOpenEntry(entry.fileName) },
                                onLongClick = { entryToDelete = entry }
                            )
                        }
                    }
                }
            }
        }
    }
}