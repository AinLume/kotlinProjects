package com.example.prac10.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.example.prac10.domain.model.LocationResult
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class LocationRepositoryImpl(private val context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult {
        return try {
            val location = suspendCancellableCoroutine { continuation ->
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMaxUpdateAgeMillis(10_000L)
                    .build()

                fusedClient.getCurrentLocation(request, null)
                    .addOnSuccessListener { loc ->
                        continuation.resume(loc)
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            }

            if (location == null) {
                return LocationResult.Error("Не удалось получить координаты. Проверьте GPS.")
            }

            val address = reverseGeocode(location.latitude, location.longitude)

            LocationResult.Success(
                latitude = location.latitude,
                longitude = location.longitude,
                address = address
            )

        } catch (e: Exception) {
            LocationResult.Error("Ошибка: ${e.localizedMessage}")
        }
    }

    private suspend fun reverseGeocode(lat: Double, lng: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())

        return try {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    val result = addresses.firstOrNull()?.let { addr ->
                        buildAddress(addr)
                    } ?: "Адрес не найден"
                    continuation.resume(result)
                }
            }
        } catch (e: Exception) {
            "Адрес недоступен (нет интернета?)"
        }
    }

    private fun buildAddress(addr: android.location.Address): String {
        return listOfNotNull(
            addr.thoroughfare,
            addr.subThoroughfare,
            addr.locality,
            addr.adminArea,
            addr.countryName
        ).joinToString(", ")
    }
}