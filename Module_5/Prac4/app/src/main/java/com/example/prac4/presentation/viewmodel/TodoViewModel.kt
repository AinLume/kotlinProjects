package com.example.prac4.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.prac4.data.local.TodoDatabase
import com.example.prac4.data.preferences.UserPreferencesRepository
import com.example.prac4.data.repository.TodoRepositoryImpl
import com.example.prac4.domain.model.Todo
import com.example.prac4.domain.usecase.GetTodosUseCase
import com.example.prac4.domain.usecase.ManageTodoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TodoDatabase.getDatabase(application).todoDao()
    private val repository = TodoRepositoryImpl(dao)
    private val getTodosUseCase = GetTodosUseCase(repository)
    private val manageTodoUseCase = ManageTodoUseCase(repository)
    val prefsRepository = UserPreferencesRepository(application)

    val todos: StateFlow<List<Todo>> = getTodosUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val highlightDone: StateFlow<Boolean> = prefsRepository.highlightDoneTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val json = application.assets.open("todos.json")
                .bufferedReader().use { it.readText() }
            manageTodoUseCase.importIfEmpty(json)
        }
    }

    fun addTodo(title: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            manageTodoUseCase.add(Todo(title = title, description = description, isDone = false))
        }
    }

    fun toggleDone(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            manageTodoUseCase.update(todo.copy(isDone = !todo.isDone))
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) { manageTodoUseCase.update(todo) }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) { manageTodoUseCase.delete(todo) }
    }

    fun setHighlightDone(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setHighlightDoneTasks(enabled) }
    }
}