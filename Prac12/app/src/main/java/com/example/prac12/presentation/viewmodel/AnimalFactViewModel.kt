package com.example.prac12.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class FactUiState {
    object Idle : FactUiState()
    object Loading : FactUiState()
    data class Success(val fact: String) : FactUiState()
}

class AnimalFactViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<FactUiState>(FactUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val facts = listOf(
        "Осьминоги имеют три сердца и голубую кровь.",
        "Коровы могут спать стоя, но видят сны только лёжа.",
        "Дельфины дают друг другу имена и откликаются на них.",
        "Слоны — единственные животные, которые не умеют прыгать.",
        "Пчела может узнать человека в лицо и запомнить его.",
        "Морские выдры держатся за лапки во время сна, чтобы не разлучиться.",
        "Тарантулы могут жить без еды больше двух лет.",
        "У кошек есть специальный орган, который улавливает феромоны — «якобсонов орган».",
        "Гориллы смеются, когда их щекочут.",
        "Лошади не могут дышать ртом — только через нос.",
        "Медузы на 95% состоят из воды.",
        "Акулы старше деревьев — им около 450 млн лет.",
        "Улитка может спать 3 года подряд.",
        "Бегемоты потеют красным потом — это их природный солнцезащитный крем.",
        "Попугаи жако по интеллекту сравнимы с пятилетним ребёнком."
    )
    private fun getRandomFact(): Flow<String> = flow {
        delay(Random.nextLong(1500, 3000))
        emit(facts.random())
    }

    fun loadFact() {
        viewModelScope.launch {
            _uiState.value = FactUiState.Loading

            getRandomFact().collect { fact ->
                _uiState.value = FactUiState.Success(fact)
            }
        }
    }
}