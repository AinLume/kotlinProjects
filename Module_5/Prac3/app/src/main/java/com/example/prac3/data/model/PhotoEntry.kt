package com.example.prac3.data.model

import java.io.File

data class PhotoEntry(
    val file: File,
    val name: String,
    val dateModified: Long
)