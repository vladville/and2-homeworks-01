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
    fun setLike(likes: Int) {

    }
    override fun like() {
        if (post.likeByMe == false) {
            post = post.copy(likeByMe = true, likes = post.likes + 1)
        } else {
            post = post.copy(likeByMe = false, likes = post.likes - 1)
        }
        data.value = post
    }

    override fun share() {
        post = post.copy(shares = post.shares+1)
        data.value = post
    }

}