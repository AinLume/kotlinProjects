package com.example.socialfeed.domain.entity

data class PostUiState(
    val post: Post,
    val avatarState: LoadState<String>,
    val commentsState: LoadState<List<Comment>>
)
