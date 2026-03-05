package com.example.prac13.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.random.Random

data class RateState(
    val rate: Double = 90.5,
    val direction: Direction = Direction.NONE
)
sealed class Direction {
    object UP   : Direction()
    object DOWN : Direction()
    object NONE : Direction()
}

class ExchangeRateViewModel : ViewModel() {
    private val _rateState = MutableStateFlow(RateState())
    val rateState = _rateState.asStateFlow()

    init {
        startAutoUpdate()
    }

    private fun startAutoUpdate() {
        viewModelScope.launch {
            while (true) {
                delay(5000L)
                emitNewRate()
            }
        }
    }

    fun refreshNow() {
        viewModelScope.launch {
            emitNewRate()
        }
    }

    private fun emitNewRate() {
        val currentRate = _rateState.value.rate
        val delta = Random.nextDouble(-2.0, 2.0)
        val newRate = (currentRate + delta).coerceIn(80.0, 110.0)
        val rounded = (newRate * 100.0).roundToLong() / 100.0

        val direction = when {
            rounded > currentRate -> Direction.UP
            rounded < currentRate -> Direction.DOWN
            else -> Direction.NONE
        }

        _rateState.value = RateState(rate = rounded, direction = direction)
    }
}