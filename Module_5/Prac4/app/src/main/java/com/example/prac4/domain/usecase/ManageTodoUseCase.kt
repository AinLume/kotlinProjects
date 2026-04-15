package com.example.prac4.domain.usecase

import com.example.prac4.domain.model.Todo
import com.example.prac4.domain.repository.TodoRepository

class ManageTodoUseCase(private val repository: TodoRepository) {
    suspend fun add(todo: Todo) = repository.addTodo(todo)
    suspend fun update(todo: Todo) = repository.updateTodo(todo)
    suspend fun delete(todo: Todo) = repository.deleteTodo(todo)
    suspend fun importIfEmpty(json: String) {
        if (repository.getCount() == 0) repository.importFromJson(json)
    }
}