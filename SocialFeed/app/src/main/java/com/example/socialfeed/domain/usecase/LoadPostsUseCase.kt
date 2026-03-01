package com.example.socialfeed.domain.usecase

import com.example.socialfeed.domain.entity.LoadState
import com.example.socialfeed.domain.entity.PostUiState
import com.example.socialfeed.domain.repository.PostRepository

class LoadPostsUseCase (
    private val postRepository: PostRepository,
) {

    suspend operator fun invoke(): List<PostUiState> {

        val posts = postRepository.getAll()

        return posts.map {post ->
            PostUiState (
                post = post,
                avatarState = LoadState.Loading,
                commentsState = LoadState.Loading
            )
        }
    }
}