package com.example.myapplication.domain.entity

data class GitHubRepo (
    val id: Int,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val language: String?
)