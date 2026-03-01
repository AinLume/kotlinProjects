package com.example.socialfeed.domain.entity

sealed class LoadState {
    object Loading : LoadState()
    data class Ready<T>(val data: T) : LoadState()
    object Error: LoadState()
}