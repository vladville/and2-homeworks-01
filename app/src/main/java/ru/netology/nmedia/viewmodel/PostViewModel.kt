package ru.netology.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.application
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryNetworkImpl
import ru.netology.nmedia.utils.SingleLiveEvent
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = ""
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
        _data.value = (FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.PostCallback<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.value = (FeedModel(posts = result, empty = result.isEmpty()))
            }

            override fun onError(e: Throwable) {
                _data.value = (FeedModel(error = true))
            }
        })
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
        edited.value?.let {
            repository.save(it, object : PostRepository.PostCallback<Post> {
                override fun onSuccess(result: Post) {
                    loadPosts()
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Throwable) {
                    _data.value = (FeedModel(error = true))
                }
            })
        }
        edited.value = empty
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

        _data.value?.posts.orEmpty().map { post ->
            if (post.id == id) {
                if (!post.likedByMe) {
                    repository.setLikeAsync(id, object : PostRepository.PostCallback<Post> {
                        override fun onSuccess(result: Post) {
                            val refreshState = _data.value ?: return
                            val updatedPosts = refreshState.posts.map {
                                if (it.id == result.id) result else it
                            }
                            _data.value = (refreshState.copy(posts = updatedPosts))
                        }

                        override fun onError(e: Throwable) {
                            val refreshState = _data.value ?: return
                            _data.postValue(refreshState)
                            _data.value = (FeedModel(errorSetLike = true))
                        }
                    })
                } else {
                    repository.setUnlikeAsync(id, object : PostRepository.PostCallback<Post> {
                            override fun onSuccess(result: Post) {
                                val refreshState = _data.value ?: return
                                val updatedPosts = refreshState.posts.map {
                                    if (it.id == result.id) result else it
                                }
                                _data.value = (refreshState.copy(posts = updatedPosts))
                            }

                            override fun onError(e: Throwable) {
                                val refreshState = _data.value ?: return
                                _data.postValue(refreshState)
                                _data.value = (FeedModel(errorUnLike = true))
                            }
                        })
                }
            }
        }
    }

    fun removeById(id: Long) {
        val currentState = _data.value ?: return

        repository.removeById(id, object : PostRepository.PostCallback<Unit> {
            override fun onSuccess(result: Unit) {
                _data.postValue(currentState.copy(posts = currentState.posts.filter { it.id != id }))
            }

            override fun onError(e: Throwable) {
                _data.postValue(currentState)
                _data.value = (FeedModel(errorDelete = true))
            }
        })
    }
}