package com.example.socialfeed.domain.entity

data class Post (
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val avatarUrl: String
)