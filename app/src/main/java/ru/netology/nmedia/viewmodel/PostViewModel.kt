package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryNetworkImpl
import ru.netology.nmedia.utils.SingleLiveEvent
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryNetworkImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    fun share(id: Long) = repository.share(id)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))

            try {
                val posts = repository.get()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: Exception) {
                FeedModel(error = true)
            }
                .let(_data::postValue)
        }
    }

    fun changeContent(content: String) {
        val text = content.trim()
        edited.value?.let {
            if (text == it.content)
                return@let

            edited.value = it.copy(content = text)
        }
    }

    fun save() {
        thread {
            edited.value?.let {
                repository.save(it)
                loadPosts()
                _postCreated.postValue(Unit)
            }
            edited.postValue(empty)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun editPostCancel() {
        edited.value = empty
    }

    fun isNewPost(): Boolean {
        return edited.value?.id == 0L
    }

    fun like(id: Long) {
        thread {
            _data.postValue(
                _data.value?.copy(
                    posts = _data.value?.posts.orEmpty().map { post ->
                        if (post.id == id) {
                            if (!post.likeByMe) {
                                repository.like(id)
                                post.copy(likeByMe = true, likes = post.likes + 1)
                            } else {
                                repository.unlike(id)
                                post.copy(likeByMe = false, likes = post.likes - 1)
                            }
                        } else {
                            post
                        }
                    }
                )
            )
        }
    }

    fun removeById(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(
                    posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }

                )
            )
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }
}