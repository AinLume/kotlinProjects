package com.example.prac4.presentation.ui.screen

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
import com.example.prac4.domain.model.Todo
import com.example.prac4.presentation.ui.component.AddTodoDialog
import com.example.prac4.presentation.ui.component.TodoItem
import com.example.prac4.presentation.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onOpenTodo: (Todo) -> Unit,
    viewModel: TodoViewModel = viewModel()
) {
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val highlightDone by viewModel.highlightDone.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }

    if (showAddDialog) {
        AddTodoDialog(
            onConfirm = { title, desc ->
                viewModel.addTodo(title, desc)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    todoToDelete?.let { todo ->
        AlertDialog(
            onDismissRequest = { todoToDelete = null },
            title = { Text("Удалить задачу?") },
            text = { Text(todo.title) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteTodo(todo); todoToDelete = null }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { todoToDelete = null }) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Задачи") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    Text(
                        text = "Цвет завершённых",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Switch(
                        checked = highlightDone,
                        onCheckedChange = { viewModel.setHighlightDone(it) },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (todos.isEmpty()) {
                Text(
                    text = "Нет задач. Нажмите + чтобы добавить",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todos, key = { it.id }) { todo ->
                        TodoItem(
                            todo = todo,
                            highlightDone = highlightDone,
                            onClick = { onOpenTodo(todo) },
                            onLongClick = { todoToDelete = todo },
                            onToggle = { viewModel.toggleDone(todo) }
                        )
                    }
                }
            }
        }
    }
}