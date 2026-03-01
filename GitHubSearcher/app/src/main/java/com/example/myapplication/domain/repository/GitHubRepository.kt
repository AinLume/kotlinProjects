package com.example.myapplication.domain.repository

import com.example.myapplication.domain.entity.GitHubRepo

interface GithubRepository {
    suspend fun getAll(): List<GitHubRepo>
}