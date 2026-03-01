package com.example.socialfeed.data.repository

import com.example.socialfeed.R
import android.content.Context
import com.example.socialfeed.data.model.CommentDto
import com.example.socialfeed.data.model.PostDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RawJsonDataSource(private val context: Context) {

    suspend fun loadPosts(): List<PostDto> = withContext(Dispatchers.IO) {

        val inputStream = context.resources.openRawResource(R.raw.social_posts)

        val json = inputStream.bufferedReader().use { it.readText() }

        val type = object : TypeToken<List<PostDto>>() {}.type

        Gson().fromJson(json, type)
    }

    suspend fun loadComments(): List<CommentDto> = withContext(Dispatchers.IO) {

        val inputStream = context.resources.openRawResource(R.raw.comments)

        val json = inputStream.bufferedReader().use { it.readText() }

        val type = object : TypeToken<List<CommentDto>>() {}.type

        Gson().fromJson(json, type)
    }
}