package com.example.socialfeed.domain.entity

sealed class LoadState<out T> {
    object Loading : LoadState<Nothing>()
    data class Ready<T>(val data: T) : LoadState<T>()
    object Error: LoadState<Nothing>()
}