package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

interface PostRepository {
    fun get(): List<Post>
    fun like(id: Long)
    fun unlike(id: Long)
    fun share(id: Long)
    fun removeById(id: Long)
    fun save(post: Post): Post
}