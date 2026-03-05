package com.example.socialfeed.data.repository

import com.example.socialfeed.domain.entity.Comment
import com.example.socialfeed.domain.repository.CommentRepository

class CommentRepositoryImpl (
    private val dataSource: RawJsonDataSource
) : CommentRepository {

    private var lruCache: LinkedHashMap<Int, List<Comment>> = LinkedHashMap(
        16, 0.75f, true)

    override suspend fun getAllByPostId(postId: Int): List<Comment> {

        if (lruCache.containsKey(postId)) {
            val comments: List<Comment>? = lruCache.get(postId)
            if (comments != null) return comments
        }

        val comments: List<Comment> = dataSource.loadComments()
            .filter { it.post_id == postId }
            . map { it.toDomain() }

        lruCache.put(postId, comments)

        return comments
    }
}