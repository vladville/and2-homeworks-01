package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun share(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post, photo: File?): Post
    suspend fun getAllAsync()
    suspend fun setLikeAsync(id: Long): Post
    suspend fun setUnlikeAsync(id: Long): Post
    fun isEmpty(): LiveData<Boolean>
    suspend fun sendUnsentPost()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun getAndUpdateUnshowedPosts()
}