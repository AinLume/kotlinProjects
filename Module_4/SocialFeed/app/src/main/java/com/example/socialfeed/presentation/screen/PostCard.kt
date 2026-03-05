package com.example.socialfeed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.socialfeed.domain.entity.PostUiState
import androidx.core.graphics.toColorInt
import com.example.socialfeed.domain.entity.LoadState

@Composable
fun PostCard(post: PostUiState) {
    var commentsIsOpen by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {

            Row(
                modifier = Modifier.paddingFromBaseline(2.dp, bottom = 2.dp)
            ) {
                when (post.avatarState) {
                    is LoadState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is LoadState.Ready -> {
                        val color = post.avatarState.data

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(
                                        color.toColorInt()
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${post.post.userId}",
                                color = Color.White
                            )
                        }
                    }

                    is LoadState.Error -> {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "❌",
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(text = post.post.title, style = MaterialTheme.typography.titleSmall)
            }

            Row {
                Text(text = post.post.body, style = MaterialTheme.typography.bodyMedium)
            }

            Row {
                TextButton(onClick = { commentsIsOpen = !commentsIsOpen }) {
                    Text(if (commentsIsOpen) "Скрыть" else "Комментарии")
                    Icon(
                        imageVector = if (commentsIsOpen) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            if (commentsIsOpen) {
                Column {
                    when (post.commentsState) {
                        is LoadState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is LoadState.Ready -> {
                            val comments = post.commentsState.data

                            comments.forEach { comment ->
                                CommentCard(comment)
                            }
                        }

                        is LoadState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RectangleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "❌",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}