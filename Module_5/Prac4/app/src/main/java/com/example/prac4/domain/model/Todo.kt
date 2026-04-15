package com.example.prac4.domain.model

data class Todo(
    val id: Int = 0,
    val title: String,
    val description: String,
    val isDone: Boolean
)