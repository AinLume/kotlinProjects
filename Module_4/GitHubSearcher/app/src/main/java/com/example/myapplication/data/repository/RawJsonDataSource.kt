package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.R
import com.example.myapplication.data.model.GitHubRepoDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RawJsonDataSource(private val context: Context) {

    suspend fun loadAll(): List<GitHubRepoDto> = withContext(Dispatchers.IO) {

        val inputStream = context.resources.openRawResource(R.raw.github_repos)

        val json = inputStream.bufferedReader().use { it.readText() }

        val type = object : TypeToken<List<GitHubRepoDto>>() {}.type

        Gson().fromJson(json, type)
    }
}