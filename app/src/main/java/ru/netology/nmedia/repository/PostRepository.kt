package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun get(): List<Post>

    //fun like(id: Long): Post
    //fun unlike(id: Long): Post
    fun share(id: Long)
    fun removeById(id: Long, callback: PostCallback<Unit>)
    fun save(post: Post, callback: PostCallback<Post>)
    fun getAllAsync(callback: PostCallback<List<Post>>)
    fun setLikeAsync(id: Long, callback: PostCallback<Post>)
    fun setUnlikeAsync(id: Long, callback: PostCallback<Post>)

    interface PostCallback<T> {
        fun onSuccess(result: T)
        fun onError(e: Throwable)
    }
}