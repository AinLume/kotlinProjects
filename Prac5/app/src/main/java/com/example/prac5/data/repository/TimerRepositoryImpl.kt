package com.example.prac5.data.repository

import com.example.prac5.domain.repository.TimerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TimerRepositoryImpl: TimerRepository {

    private val _seconds = MutableStateFlow(0)
    override val seconds: StateFlow<Int> = _seconds

    override fun updateSeconds(value: Int) {
        _seconds.value = value
    }

    override fun reset() {
        _seconds.value = 0
    }
}