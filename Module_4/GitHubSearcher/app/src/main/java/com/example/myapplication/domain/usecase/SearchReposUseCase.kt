package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.entity.GitHubRepo
import com.example.myapplication.domain.repository.GithubRepository

class SearchReposUseCase (
    private val repository: GithubRepository
) {
    suspend operator fun invoke(query: String): List<GitHubRepo> {

        if (query.isBlank()) return emptyList()

        val all = repository.getAll()

        return all.filter { repo ->
            repo.fullName.contains(query, ignoreCase = true) ||
            repo.description?.contains(query, ignoreCase = true) == true ||
            repo.language?.contains(query, ignoreCase = true) == true
        }
    }
}