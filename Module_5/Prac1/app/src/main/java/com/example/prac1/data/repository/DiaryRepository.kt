package com.example.prac1.data.repository

import android.content.Context
import com.example.prac1.data.model.DiaryEntry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryRepository(private val context: Context) {

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    fun loadAllEntries(): List<DiaryEntry> {
        return context.filesDir
            .listFiles { file -> file.name.endsWith(".txt") }
            ?.mapNotNull { parseFileToEntry(it) }
            ?.sortedByDescending { it.timestamp }
            ?: emptyList()
    }

    fun saveEntry(title: String, text: String): DiaryEntry {
        val timestamp = System.currentTimeMillis()
        val safeTitle = title.trim()
            .replace(Regex("[^\\w ]"), "")
            .replace(" ", "_")
            .take(30)

        val fileName = if (safeTitle.isNotEmpty()) "${timestamp}_${safeTitle}.txt"
        else "${timestamp}.txt"

        val content = buildString {
            if (title.isNotBlank()) appendLine("TITLE:${title.trim()}")
            append(text)
        }

        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(content.toByteArray(Charsets.UTF_8))
        }

        return DiaryEntry(
            fileName = fileName,
            title = title.trim(),
            preview = text.take(40).replace("\n", " "),
            date = dateFormatter.format(Date(timestamp)),
            timestamp = timestamp
        )
    }

    fun readEntry(fileName: String): Pair<String, String> {
        val file = File(context.filesDir, fileName)

        if (!file.exists()) return "" to ""

        val content = file.readText(Charsets.UTF_8)
        return if (content.startsWith("TITLE:")) {
            val lines = content.lines()
            val title = lines.first().removePrefix("TITLE:")
            val text = lines.drop(1).joinToString("\n")
            title to text
        }
        else {
            "" to content
        }
    }

    fun updateEntry(fileName: String, title: String, text: String): DiaryEntry {
        val file = File(context.filesDir, fileName)

        val content = buildString {
            if (title.isNotBlank()) appendLine("TITLE:${title.trim()}")
            append(text)
        }

        file.writeText(content, Charsets.UTF_8)

        val timestamp = extractTimestamp(fileName)
        return DiaryEntry(
            fileName = fileName,
            title = title.trim(),
            preview = text.take(40).replace("\n", " "),
            date = dateFormatter.format(Date(timestamp)),
            timestamp = timestamp
        )
    }

    fun deleteEntry(fileName: String) {
        File(context.filesDir, fileName).delete()
    }

    private fun parseFileToEntry(file: File): DiaryEntry? = try {
        val content = file.readText(Charsets.UTF_8)
        val timestamp = extractTimestamp(file.name)
        val title: String
        val bodyText: String

        if (content.startsWith("TITLE:")) {
            val lines = content.lines()
            title = lines.first().removePrefix("TITLE:")
            bodyText = lines.drop(1).joinToString("\n")
        }
        else {
            title = ""; bodyText = content
        }

        DiaryEntry(
            fileName = file.name,
            title = title,
            preview = bodyText.take(40).replace("\n", " "),
            date = dateFormatter.format(Date(timestamp)),
            timestamp = timestamp
        )
    }
    catch (e: Exception) { null }

    private fun extractTimestamp(fileName: String): Long =
        fileName.substringBefore("_").substringBefore(".").toLongOrNull()
            ?: System.currentTimeMillis()
}