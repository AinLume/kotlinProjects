package com.example.socialfeed.domain.repository

import com.example.socialfeed.domain.entity.Comment

interface CommentRepository {
    suspend fun getAllByPostId(postId: Int): List<Comment>
}