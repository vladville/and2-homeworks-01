package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {

    private var index: Long = 1L
    private var posts = listOf(
        Post(
            id = index++,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 999,
            views = 202_999
        ),
        Post(
            id = index++,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999,
            video = "https://rutube.ru/video/6550a91e7e523f9503bed47e4c46d0cb"
        ),
        Post(
            id = index++,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "26 мая в 19:13",
            likes = 12_999,
            shares = 1_099,
            views = 2_202_200
        ),
        Post(
            id = index++,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = index++,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = index++,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "26 мая в 19:13",
            likes = 12_999,
            shares = 1_099,
            views = 2_202_200
        ),
        Post(
            id = index++,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = index++,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "03 июня в 19:13",
            likes = 999,
            shares = 9,
            views = 202_999
        ),
        Post(
            id = index++,
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

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            listOf(
                post.copy(
                    id = index++,
                    author = "Me",
                    published = "now"
                )
            ) + posts
        } else {
            posts.map {
                if (post.id == it.id) {
                    it.copy(content = post.content)
                } else it
            }
        }
        data.value = posts
    }

}