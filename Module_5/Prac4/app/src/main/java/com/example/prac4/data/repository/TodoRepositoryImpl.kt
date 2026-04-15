package com.example.prac4.data.repository

import com.example.prac4.data.local.TodoDao
import com.example.prac4.data.model.TodoEntity
import com.example.prac4.domain.model.Todo
import com.example.prac4.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
private data class TodoJson(
    val id: Int = 0,
    val title: String,
    val description: String,
    val isDone: Boolean
)

class TodoRepositoryImpl(private val dao: TodoDao) : TodoRepository {

    override fun getAllTodos(): Flow<List<Todo>> =
        dao.getAllTodos().map { list -> list.map { it.toDomain() } }

    override suspend fun getCount(): Int = dao.getCount()

    override suspend fun addTodo(todo: Todo) = dao.insert(todo.toEntity())

    override suspend fun updateTodo(todo: Todo) = dao.update(todo.toEntity())

    override suspend fun deleteTodo(todo: Todo) = dao.delete(todo.toEntity())

    override suspend fun importFromJson(json: String) {
        val items = Json.decodeFromString<List<TodoJson>>(json)
        dao.insertAll(items.map {
            TodoEntity(title = it.title, description = it.description, isDone = it.isDone)
        })
    }

    private fun TodoEntity.toDomain() = Todo(id, title, description, isDone)
    private fun Todo.toEntity() = TodoEntity(id, title, description, isDone)
}