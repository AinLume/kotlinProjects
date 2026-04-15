package com.example.prac4.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prac4.domain.model.Todo
import com.example.prac4.presentation.ui.screen.TodoDetailScreen
import com.example.prac4.presentation.ui.screen.TodoListScreen
import com.example.prac4.presentation.viewmodel.TodoViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: TodoViewModel = viewModel()
    var selectedTodo by remember { mutableStateOf<Todo?>(null) }

    NavHost(navController, startDestination = "list") {
        composable("list") {
            TodoListScreen(
                viewModel = viewModel,
                onOpenTodo = { todo ->
                    selectedTodo = todo
                    navController.navigate("detail")
                }
            )
        }
        composable("detail") {
            selectedTodo?.let { todo ->
                TodoDetailScreen(
                    todo = todo,
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }
    }
}