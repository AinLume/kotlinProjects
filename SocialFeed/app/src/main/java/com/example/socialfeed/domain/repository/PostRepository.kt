package com.example.socialfeed.domain.repository

import com.example.socialfeed.domain.entity.Post

interface PostRepository {
    suspend fun getAll(): List<Post>
}