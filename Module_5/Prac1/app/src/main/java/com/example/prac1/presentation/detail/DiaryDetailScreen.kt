package com.example.prac1.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prac1.data.model.DiaryEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    fileName: String?,
    onBack: () -> Unit,
    onSaved: (DiaryEntry) -> Unit,
    viewModel: DiaryDetailViewModel = viewModel()
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val text by viewModel.text.collectAsStateWithLifecycle()
    val savedEntry by viewModel.savedEntry.collectAsStateWithLifecycle()
    val isNewEntry = fileName == null

    LaunchedEffect(fileName) {
        if (fileName != null) viewModel.loadEntry(fileName)
    }

    LaunchedEffect(savedEntry) {
        savedEntry?.let {
            onSaved(it)
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNewEntry) "Новая запись" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.save() },
                        enabled = text.isNotBlank()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Заголовок (необязательно)") },
                singleLine = true
            )

            OutlinedTextField(
                value = text,
                onValueChange = viewModel::onTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                label = { Text("Текст записи") },
                placeholder = { Text("Напишите что-нибудь...") },
                maxLines = Int.MAX_VALUE
            )

            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth(),
                enabled = text.isNotBlank()
            ) {
                Text("Сохранить запись")
            }
        }
    }
}