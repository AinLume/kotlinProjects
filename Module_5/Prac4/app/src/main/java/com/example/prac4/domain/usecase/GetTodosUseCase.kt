package com.example.prac4.domain.usecase

import com.example.prac4.domain.model.Todo
import com.example.prac4.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class GetTodosUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<List<Todo>> = repository.getAllTodos()
}