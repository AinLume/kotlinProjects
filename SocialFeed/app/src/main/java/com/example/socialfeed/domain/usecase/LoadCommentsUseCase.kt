package com.example.socialfeed.domain.usecase

import com.example.socialfeed.domain.entity.Comment
import com.example.socialfeed.domain.repository.CommentRepository

class LoadCommentsUseCase (
    private val commentRepository: CommentRepository
) {

    suspend operator fun invoke(postId: Int): List<Comment> {
        return commentRepository.getAllByPostId(postId)
    }
}