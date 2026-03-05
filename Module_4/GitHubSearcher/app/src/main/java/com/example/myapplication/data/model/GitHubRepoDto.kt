package com.example.myapplication.data.model

import com.example.myapplication.domain.entity.GitHubRepo

data class GitHubRepoDto(
    val id: Int,
    val full_name: String,
    val description: String?,
    val stargazers_count: Int,
    val language: String?
) {
    fun toDomain() = GitHubRepo(
        id = id,
        fullName = full_name,
        description = description,
        stars = stargazers_count,
        language = language
    )
}