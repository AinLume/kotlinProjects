package com.example.prac5.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val seconds: StateFlow<Int>
    fun updateSeconds(value: Int)
    fun reset()
}