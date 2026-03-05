package com.example.socialfeed.data.repository

import com.example.socialfeed.domain.entity.Post
import com.example.socialfeed.domain.repository.PostRepository

class PostRepositoryImpl(
    private val dataSource: RawJsonDataSource
) : PostRepository {

    private var cache: List<Post>? = null

    override suspend fun getAll(): List<Post> {
        return cache ?:dataSource.loadPosts()
            .map { it.toDomain() }
            .also { cache = it }
    }

}