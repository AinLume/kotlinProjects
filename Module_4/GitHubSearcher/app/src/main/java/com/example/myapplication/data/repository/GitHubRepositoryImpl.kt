package com.example.myapplication.data.repository

import com.example.myapplication.domain.entity.GitHubRepo
import com.example.myapplication.domain.repository.GithubRepository

class GithubRepositoryImpl(
    private val dataSource: RawJsonDataSource
) : GithubRepository {
    private var cache: List<GitHubRepo>? = null

    override suspend fun getAll(): List<GitHubRepo> {
        return cache ?: dataSource.loadAll()
            .map { it.toDomain() }
            .also { cache = it }
    }
}