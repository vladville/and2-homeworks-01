package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {

    private var posts = listOf(
        Post(
            id = 9,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = 8,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = 7,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "26 мая в 19:13",
            likes = 12_999,
            shares = 1_099,
            views = 2_202_200
        ),
        Post(
            id = 6,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = 5,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = 4,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "26 мая в 19:13",
            likes = 12_999,
            shares = 1_099,
            views = 2_202_200
        ),
        Post(
            id = 3,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = 2,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "26 мая в 19:13",
            likes = 12_999,
            shares = 1_099,
            views = 2_202_200
        ),
    )

    private val data = MutableLiveData(posts)

    override fun get(): LiveData<List<Post>> = data

    override fun like(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                if (post.likeByMe == false) {
                    post.copy(likeByMe = true, likes = post.likes + 1)
                } else {
                    post.copy(likeByMe = false, likes = post.likes - 1)
                }
            } else {
                post
            }
        }
        data.value = posts
    }

    override fun share(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(shares = post.shares + 1)
            } else {
                post
            }
        }
        data.value = posts
    }

}