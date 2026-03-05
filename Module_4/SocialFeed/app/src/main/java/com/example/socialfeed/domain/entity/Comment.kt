package com.example.socialfeed.domain.entity

data class Comment (
    val id: Int,
    val postId: Int,
    val name: String,
    val body: String
)
