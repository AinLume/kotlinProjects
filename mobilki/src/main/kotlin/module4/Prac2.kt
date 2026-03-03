package org.example.module4

import kotlinx.coroutines.*
import java.io.File
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

/**
 * Точка входа программы.
 *
 * Запускает поиск JSON-файлов-дубликатов в директории [rootDirPath] с общим таймаутом [timeoutSeconds].
 * Если поиск не завершился за отведённое время — все корутины отменяются и выводится сообщение о таймауте.
 * По завершении выводит время выполнения в миллисекундах.
 */
fun main() = runBlocking {
    val rootDirPath = "src/main/resources/prac2"
    val timeoutSeconds = 10L

    val time = measureTimeMillis {
        val result = withTimeoutOrNull(timeoutSeconds * 1000) {
            findDuplicateJsonFiles(rootDirPath)
        }

        if (result == null) {
            println("Search timeout")
        } else {
            printDuplicateGroups(result)
        }
    }

    println("Complete at $time ms")
}

/**
 * Рекурсивно обходит директорию [rootDirPath], находит все JSON-файлы
 * и возвращает группы дубликатов, сгруппированных по SHA-256 хэшу содержимого.
 *
 * Хэширование каждого файла выполняется параллельно с помощью [async] + [Dispatchers.IO].
 * Если корутина была отменена до начала обработки файла — файл пропускается.
 *
 * @param rootDirPath абсолютный или относительный путь к корневой директории поиска.
 * @return Map, где ключ — SHA-256 хэш, значение — список файлов с одинаковым содержимым.
 *         Содержит только группы с двумя и более файлами (то есть реальные дубликаты).
 */
suspend fun findDuplicateJsonFiles(rootDirPath: String): Map<String, List<File>> =
    coroutineScope {
        val root = File(rootDirPath)

        val jsonFiles = root
            .walkTopDown()
            .filter { it.isFile && it.extension.equals("json", ignoreCase = true) }
            .toList()

        val deferredHashes: List<Deferred<Pair<String, File>?>> = jsonFiles.map { file ->
            async(Dispatchers.IO) {
                if (!isActive) return@async null
                val hash = computeFileSha256(file)
                hash to file
            }
        }

        val completed = deferredHashes.mapNotNull { it.await() }

        completed
            .groupBy({ it.first }, { it.second })
            .filterValues { it.size > 1 }
    }

/**
 * Вычисляет SHA-256 хэш содержимого файла [file].
 *
 * Файл читается побуферно ([DEFAULT_BUFFER_SIZE]) для экономии памяти при работе с большими файлами.
 * После каждого прочитанного чанка вызывается [ensureActive] — это позволяет корректно
 * отреагировать на отмену корутины (например, при срабатывании таймаута) без утечки ресурсов.
 *
 * Выполняется в [Dispatchers.IO] — пуле потоков, предназначенном для блокирующих IO-операций.
 *
 * @param file файл, для которого вычисляется хэш.
 * @return строка с HEX-представлением SHA-256 хэша (64 символа, нижний регистр).
 * @throws kotlinx.coroutines.CancellationException если корутина была отменена во время чтения.
 * @throws java.io.IOException если файл недоступен для чтения.
 */
suspend fun computeFileSha256(file: File): String = withContext(Dispatchers.IO) {
    val digest = MessageDigest.getInstance("SHA-256")

    file.inputStream().use { input ->
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        while (true) {
            val read = input.read(buffer)
            if (read <= 0) break

            digest.update(buffer, 0, read)

            ensureActive()
        }
    }
    val hashBytes = digest.digest()
    hashBytes.joinToString("") { "%02x".format(it) }
}

/**
 * Выводит в консоль группы файлов-дубликатов.
 *
 * Для каждой группы печатает SHA-256 хэш и абсолютные пути всех файлов с этим хэшем.
 * Если дубликатов не найдено — выводит соответствующее сообщение.
 *
 * @param duplicates Map с результатами поиска: ключ — SHA-256 хэш, значение — список дубликатов.
 *                   Ожидается что каждая группа содержит минимум два файла.
 */
fun printDuplicateGroups(duplicates: Map<String, List<File>>) {
    if (duplicates.isEmpty()) {
        println("Duplicates don't exist")
        return
    }

    println("Existing duplicates:")
    duplicates.forEach { (hash, files) ->
        println("Hash: $hash")
        files.forEach { file ->
            println("  - ${file.absolutePath}")
        }
        println()
    }
}
