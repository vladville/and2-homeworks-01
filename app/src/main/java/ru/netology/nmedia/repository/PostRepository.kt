package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    //suspend fun get(): List<Post>
    //fun like(id: Long): Post
    //fun unlike(id: Long): Post
    val data: LiveData<List<Post>>
    suspend fun share(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post): Post
    suspend fun getAllAsync()
    suspend fun setLikeAsync(id: Long): Post
    suspend fun setUnlikeAsync(id: Long): Post
    fun isEmpty(): LiveData<Boolean>
}