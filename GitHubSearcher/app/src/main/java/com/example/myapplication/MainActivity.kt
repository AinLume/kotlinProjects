package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplication.data.repository.GithubRepositoryImpl
import com.example.myapplication.data.repository.RawJsonDataSource
import com.example.myapplication.domain.usecase.SearchReposUseCase
import com.example.myapplication.presentation.screen.SearchScreen
import com.example.myapplication.presentation.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataSource = RawJsonDataSource(applicationContext)
        val repository = GithubRepositoryImpl(dataSource)
        val useCase = SearchReposUseCase(repository)
        val viewModel = SearchViewModel(useCase)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SearchScreen(viewModel = viewModel)
                }
            }
        }
    }
}