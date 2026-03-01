package com.example.socialfeed.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.domain.entity.LoadState
import com.example.socialfeed.domain.entity.PostUiState
import com.example.socialfeed.domain.usecase.LoadCommentsUseCase
import com.example.socialfeed.domain.usecase.LoadPostsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class LoadPostsViewModel (
    private val loadPostsUseCase: LoadPostsUseCase,
    private val loadCommentsUseCase: LoadCommentsUseCase
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostUiState>>(emptyList())
    val posts: StateFlow<List<PostUiState>> = _posts.asStateFlow()

    private var loadingJob: Job? = null

    fun loadFeed() {
        loadingJob?.cancel()

        loadingJob = viewModelScope.launch {
            val initPosts = loadPostsUseCase()

            _posts.value = initPosts

            initPosts.forEach { postUiState ->
                launch {
                    supervisorScope {

                        val avatarJob = async {
                            try {
                                delay(300)

                                val colors = listOf(
                                    "#FF5733",
                                    "#33FF57",
                                    "#3357FF",
                                    "#FF33A8",
                                    "#FFD700"
                                )

                                LoadState.Ready(
                                    colors[postUiState.post.userId % colors.size]
                                )
                            } catch (e: Exception) {
                                LoadState.Error
                            }
                        }

                        val commentsJob = async {
                            try {
                                LoadState.Ready(
                                    loadCommentsUseCase(postUiState.post.id)
                                )
                            }
                            catch (e: Exception) {
                                LoadState.Error
                            }
                        }

                        val avatarResult = avatarJob.await()
                        val commentsResults = commentsJob.await()

                        _posts.update { currentList ->
                            currentList.map {
                                if (it.post.id == postUiState.post.id)
                                    it.copy(
                                        postUiState.post,
                                        avatarResult,
                                        commentsResults
                                    ) else it
                            }
                        }
                    }
                }
            }
        }
    }
}