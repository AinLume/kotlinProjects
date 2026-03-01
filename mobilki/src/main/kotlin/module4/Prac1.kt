package org.example.module4

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.system.measureTimeMillis

data class User(val id: Int, val name: String)
data class SaleItem(val product: String, val qty: Int, val revenue: Int)
data class WeatherInfo(val city: String, val temp: Int, val condition: String)

suspend fun loadUsers(): List<String> {
    delay(1800)
    if ((1..10).random() <= 3) throw RuntimeException("Error loading users")
    val users = listOf(
        User(1, "Alice"), User(2, "Bob"), User(3, "Ivan"), User(4, "Olga")
    )
    return users.map { it.name }
}

suspend fun loadSales(): Map<String, Int> {
    delay(1200)
    if ((1..10).random() <= 3) throw RuntimeException("Error loading sales stats")
    return mapOf(
        "Coffee" to 1680,
        "Tea"    to 475
    )
}

suspend fun loadWeather(): List<String> {
    delay(2500)
    if ((1..10).random() <= 3) throw RuntimeException("Error loading weather")
    val cities = listOf(
        WeatherInfo("Moscow",   -18, "snow"),
        WeatherInfo("New York",  -5, "sunny"),
        WeatherInfo("Tokio",     11, "rain")
    )
    return cities.map { "${it.city}: ${it.temp}°C, ${it.condition}" }
}

fun main() {
    val totalTime = measureTimeMillis {
        runBlocking {
            supervisorScope {
                val usersTask   = async { loadUsers() }
                val salesTask   = async { loadSales() }
                val weatherTask = async { loadWeather() }

                val users = try {
                    usersTask.await()
                } catch (e: Exception) {
                    println("Users: ${e.message}")
                    emptyList()
                }

                val sales = try {
                    salesTask.await()
                } catch (e: Exception) {
                    println("Sales: ${e.message}")
                    emptyMap()
                }

                val weather = try {
                    weatherTask.await()
                } catch (e: Exception) {
                    println("Weather: ${e.message}")
                    emptyList()
                }

                println("Users: ")
                if (users.isEmpty()) println("Data isn't present")
                else users.forEach { println("  - $it") }

                println("\nDaily sales:")
                if (sales.isEmpty()) println("Data isn't present")
                else sales.forEach { (product, revenue) ->
                    println("  $product → $revenue ₽")
                }

                println("\nWeather:")
                if (weather.isEmpty()) println("Data isn't present")
                else weather.forEach { println("  $it") }
            }
        }
    }

    println("Task time: ${totalTime}s")
}