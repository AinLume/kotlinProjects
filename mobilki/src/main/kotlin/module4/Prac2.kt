package org.example.module4

import kotlinx.coroutines.*
import java.io.File
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

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