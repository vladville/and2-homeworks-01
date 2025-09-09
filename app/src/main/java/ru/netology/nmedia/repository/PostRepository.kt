package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun get(): List<Post>
    //fun like(id: Long): Post
    //fun unlike(id: Long): Post
    fun share(id: Long)
    fun removeById(id: Long)
    fun save(post: Post): Post
    fun getAllAsync(callback: GetAllCallback)
    fun setLikeAsync(id: Long, callback: SetLikeCallback)
    fun setUnlikeAsync(id: Long, callback: SetUnLikeCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Throwable)
    }

    interface SetLikeCallback {
        fun onSuccess(post: Post)
        fun onError(e: Exception)
    }

    interface SetUnLikeCallback {
        fun onSuccess(post: Post)
        fun onError(e: Exception)
    }
}