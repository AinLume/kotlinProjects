package com.example.socialfeed.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.socialfeed.domain.entity.Comment

@Composable
fun CommentCard(com: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(text = com.name, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = com.body, style = MaterialTheme.typography.bodySmall)
        }
    }
}