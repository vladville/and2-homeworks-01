package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    author = "",
    authorId = 0,
    authorAvatar = "",
    content = "",
    published = "",
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    posts.map { it.copy(ownedByMe = it.authorId == myId) }
                }
        }.flowOn(Dispatchers.Default)

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?>
        get() = _photo

    /*val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }*/
    /*val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }*/

    fun getNewerPosts() {
        viewModelScope.launch {
            repository.getAndUpdateUnshowedPosts()
        }
    }

    fun share(id: Long) {
        viewModelScope.launch {
            repository.share(id)
        }
    }

    init {
        loadPosts()
    }

    fun updatePhoto(uri: Uri, file: File) {
        _photo.value = PhotoModel(uri, file)
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _state.value = FeedModelState(loading = true)
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
                _state.value = FeedModelState(loading = true)
                try {
                    repository.save(it, _photo.value?.file)
                    _state.value = FeedModelState()
                } catch (_: Exception) {
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
        _photo.value = null
    }

    fun isNewPost(): Boolean {
        return edited.value?.id == 0L
    }

    fun like(post: Post) {
        viewModelScope.launch {
            try {
                if (!post.likedByMe) {
                    repository.setLikeAsync(post.id)
                } else {
                    repository.setUnlikeAsync(post.id)
                }
                _state.value = FeedModelState()
            } catch (e: Exception) {
                 if (!post.likedByMe) {
                     _state.value = FeedModelState(errorSetLike = true)
                 } else {
                     _state.value = FeedModelState(errorUnLike = true)
                 }
            }
        }
    }
    /*fun like(id: Long) {
        viewModelScope.launch {
            data.value?.posts?.map { post ->
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
    }*/

    fun removeById(id: Long) {
        viewModelScope.launch {
            _state.value = FeedModelState(loading = true)
            try {
                repository.removeById(id)
                _state.value = FeedModelState()
            } catch (_: Exception) {
                _state.value = FeedModelState(errorDelete = true)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                _state.value = FeedModelState(refreshing = true)
                repository.getAllAsync()
                _state.value = FeedModelState()
            } catch (_: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun removePhoto() {
        _photo.value = null
    }
}