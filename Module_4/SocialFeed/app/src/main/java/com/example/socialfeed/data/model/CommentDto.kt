package com.example.socialfeed.data.model

import com.example.socialfeed.domain.entity.Comment

data class CommentDto (
    val id: Int,
    val post_id: Int,
    val name: String,
    val body: String
) {
    fun toDomain() = Comment(
        id = id,
        postId = post_id,
        name = name,
        body = body
    )
}