package com.example.socialfeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.socialfeed.data.repository.CommentRepositoryImpl
import com.example.socialfeed.data.repository.PostRepositoryImpl
import com.example.socialfeed.data.repository.RawJsonDataSource
import com.example.socialfeed.domain.usecase.LoadCommentsUseCase
import com.example.socialfeed.domain.usecase.LoadPostsUseCase
import com.example.socialfeed.presentation.screen.FeedsScreen
import com.example.socialfeed.presentation.viewmodel.LoadPostsViewModel
import com.example.socialfeed.ui.theme.SocialFeedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataSource = RawJsonDataSource(applicationContext)
        val postRepository = PostRepositoryImpl(dataSource)
        val commentRepository = CommentRepositoryImpl(dataSource)
        val loadPostsUseCase = LoadPostsUseCase(postRepository)
        val loadCommentsUseCase = LoadCommentsUseCase(commentRepository)
        val viewModel = LoadPostsViewModel(loadPostsUseCase, loadCommentsUseCase)

        setContent {
            MaterialTheme {
                FeedsScreen(viewModel = viewModel)
            }
        }
    }
}