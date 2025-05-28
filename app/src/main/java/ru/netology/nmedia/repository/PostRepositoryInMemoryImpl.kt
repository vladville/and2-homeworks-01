package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {

    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессией будущего",
        content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
        published = "26 мая в 19:13",
        likes = 12_999,
        shares = 1_099,
        views = 2_202_200
    )

    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        if (post.likeByMe == false) {
            post.likes++
            post = post.copy(likeByMe = true)
        } else {
            post.likes--
            post = post.copy(likeByMe = false)
        }
        data.value = post
    }

    override fun share() {
        post = post.copy(post.shares++)
        data.value = post
    }

}