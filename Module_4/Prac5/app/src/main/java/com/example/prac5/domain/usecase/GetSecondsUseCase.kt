package com.example.prac5.domain.usecase

import com.example.prac5.domain.repository.TimerRepository
import kotlinx.coroutines.flow.StateFlow

class GetSecondsUseCase (
    private val timerRepository: TimerRepository
) {
    operator fun invoke(): StateFlow<Int> {
        return timerRepository.seconds
    }
}