package com.example.prac10.domain.model

sealed class LocationResult {
    object Loading : LocationResult()

    data class Success(
        val latitude: Double,
        val longitude: Double,
        val address: String
    ) : LocationResult()

    data class Error(val message: String) : LocationResult()

    object Idle : LocationResult()
}