package com.example.socialfeed.domain.entity

data class PostUiState(
    val post: Post,
    val avatarState: LoadState,
    val commentsState: LoadState
)
