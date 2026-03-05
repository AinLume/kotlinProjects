package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.entity.GitHubRepo
import com.example.myapplication.domain.usecase.SearchReposUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchReposUseCase: SearchReposUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var debounceJob: Job? = null

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }

        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(400)
            val results = searchReposUseCase(query)
            _uiState.update { it.copy(results = results, isLoading = false) }
        }
    }
}

data class SearchUiState(
    val query: String = "",
    val results: List<GitHubRepo> = emptyList(),
    val isLoading: Boolean = false
)