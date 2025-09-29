package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryNetworkImpl
import ru.netology.nmedia.utils.SingleLiveEvent

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryNetworkImpl(AppDb.getInstance(application).postDao())
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    val data: LiveData<FeedModel> =
        repository.data.asFlow().combine(repository.isEmpty().asFlow(), ::FeedModel).asLiveData()
    val edited = MutableLiveData(empty)

    fun share(id: Long) {
        viewModelScope.launch {
            repository.share(id)
        }
    }

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = FeedModelState(loading = true)
            try {
                repository.getAllAsync()
                _state.value = FeedModelState()
            } catch (_: Exception) {
                _state.value = FeedModelState(error = true)
            }
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
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _state.value = FeedModelState()
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }
            }
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
        viewModelScope.launch {
            repository.data.value?.map { post ->
                if (post.id == id) {
                    if (!post.likedByMe) {
                        try {
                            repository.setLikeAsync(id)
                            _state.value = FeedModelState()
                        } catch (_: Exception) {
                            _state.value = FeedModelState(errorSetLike = true)
                        }
                    } else {
                        try {
                            repository.setUnlikeAsync(id)
                            _state.value = FeedModelState()
                        } catch (_: Exception) {
                            _state.value = FeedModelState(errorUnLike = true)
                        }
                    }
                }
            }
        }
    }

    fun removeById(id: Long) {
        val currentState = repository.data.value ?: return
        var deletingPost = empty
        viewModelScope.launch {
            //save deleting post
            currentState.map { post ->
                if (post.id == id) {
                    deletingPost = post
                }
            }
            try {
                repository.removeById(id)
                _state.value = FeedModelState()
            } catch (_: Exception) {
                _state.value = FeedModelState(errorDelete = true)
                repository.save(deletingPost)
                repository.getAllAsync()
                _state.value = FeedModelState()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = FeedModelState(refreshing = true)
            try {
                repository.getAllAsync()
                _state.value = FeedModelState()
            } catch (_: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }
}