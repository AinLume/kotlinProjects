package org.example.module4

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.system.measureTimeMillis

data class User(val id: Int, val name: String)
data class SaleItem(val product: String, val qty: Int, val revenue: Int)
data class SalesReport(val today: String, val items: List<SaleItem>)
data class WeatherInfo(val city: String, val temp: Int, val condition: String)

/**
 * Функция readResource.
 * Считывает содержимое файла и возвращает
 * @param filename путь до файла, включая имя
 * @return String - информация из файла
 * */
fun readResource(filename: String): String {
    val inputStream = Thread.currentThread()
        .contextClassLoader
        ?.getResourceAsStream(filename)
        ?: error("File not found: $filename")
    return inputStream.bufferedReader().use { it.readText() }
}

/**
 * Функция loadUsers.
 * Считывает users.json и маппит в [User]
 * @return List<String> - лист строковых [User]
 * */
suspend fun loadUsers(): List<String> {
    delay(1800)
    if ((1..10).random() <= 3) throw RuntimeException("Error loading users")

    val json = readResource("prac1/users.json")
    val type = object : TypeToken<List<User>>() {}.type
    val users: List<User> = Gson().fromJson(json, type)
    return users.map { it.name }
}

/**
 * Функция loadSales.
 * Считывает sales.json и маппит в [SalesReport]
 * @return Map<String, Int> - мапа с наименованием товара и его стоимости
 * */
suspend fun loadSales(): Map<String, Int> {
    delay(1200)
    if ((1..10).random() <= 3) throw RuntimeException("Error loading sales stats")

    val json = readResource("prac1/sales.json")
    val report: SalesReport = Gson().fromJson(json, SalesReport::class.java)
    return report.items.associate { it.product to it.revenue }
}

/**
 * Функция loadWeather.
 * Считывает weather.json и маппит в [WeatherInfo]
 * @return List<String> - лист строковых [WeatherInfo]
 * */
suspend fun loadWeather(): List<String> {
    delay(2500)
    if ((1..10).random() <= 3) throw RuntimeException("Error loading weather")

    val json = readResource("prac1/weather.json")
    val type = object : TypeToken<List<WeatherInfo>>() {}.type
    val cities: List<WeatherInfo> = Gson().fromJson(json, type)
    return cities.map { "${it.city}: ${it.temp}°C, ${it.condition}" }
}

/**
 * Функция main - точка входа в программу
 * */
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