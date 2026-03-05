package com.example.prac10.domain.usecase

import com.example.prac10.data.repository.LocationRepositoryImpl
import com.example.prac10.domain.model.LocationResult

class GetCurrentAddressUseCase(
    private val repository: LocationRepositoryImpl
) {
    suspend fun execute(): LocationResult = repository.getCurrentLocation()
}