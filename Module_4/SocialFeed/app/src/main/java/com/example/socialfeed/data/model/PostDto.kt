package com.example.socialfeed.data.model

import com.example.socialfeed.domain.entity.Post

data class PostDto(
    val id: Int,
    val user_id: Int,
    val title: String,
    val body: String,
    val avatar_url: String
) {
    fun toDomain() = Post(
        id = id,
        userId = user_id,
        title = title,
        body = body,
        avatarUrl = avatar_url
    )
}
