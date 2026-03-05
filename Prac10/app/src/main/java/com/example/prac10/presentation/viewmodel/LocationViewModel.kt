package com.example.prac10.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.prac10.data.repository.LocationRepositoryImpl
import com.example.prac10.domain.model.LocationResult
import com.example.prac10.domain.usecase.GetCurrentAddressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val useCase = GetCurrentAddressUseCase(
        LocationRepositoryImpl(application)
    )

    private val _result = MutableStateFlow<LocationResult>(LocationResult.Idle)
    val result: StateFlow<LocationResult> = _result.asStateFlow()

    fun fetchLocation() {
        viewModelScope.launch {
            _result.value = LocationResult.Loading
            _result.value = useCase.execute()
        }
    }
}