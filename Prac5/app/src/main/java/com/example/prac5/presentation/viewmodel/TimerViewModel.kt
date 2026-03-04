package com.example.prac5.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.prac5.domain.usecase.GetSecondsUseCase
import kotlinx.coroutines.flow.StateFlow

class TimerViewModel (
    private val getSecondsUseCase: GetSecondsUseCase
) : ViewModel() {

    val seconds: StateFlow<Int> = getSecondsUseCase()

}