package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import okhttp3.Response
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun share(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post): Post
    suspend fun getAllAsync()
    suspend fun setLikeAsync(id: Long): Post
    suspend fun setUnlikeAsync(id: Long): Post
    fun isEmpty(): LiveData<Boolean>
    suspend fun sendUnsentPost()
}