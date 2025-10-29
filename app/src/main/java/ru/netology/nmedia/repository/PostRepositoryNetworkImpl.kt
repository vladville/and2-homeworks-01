package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.File
import java.io.IOException
import javax.inject.Inject

class PostRepositoryNetworkImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: ApiService
) : PostRepository {

    val lastId = Long.MAX_VALUE

    override val data = dao.get()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun share(id: Long) {
        return dao.share(id)
    }

    override suspend fun sendUnsentPost() {
        try {
            val post = dao.getUnsentPost()
            if (post != null) {
                val response = apiService.save(post.toDto().copy(0L))
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                } else {
                    removeById(lastId)
                }
            }
        } catch (_: IOException) {
            throw NetworkError
        } catch (_: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            val entities = body.map { post ->
                PostEntity.fromDto(post.copy(showed = false, sended = true))
            }

            dao.insert(entities)
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getAndUpdateUnshowedPosts() {
        try {
            dao.setShowedPost()
        } catch (_: IOException) {
            throw NetworkError
        } catch (_: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (_: IOException) {
            throw NetworkError
        } catch (_: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post, photo: File?): Post {
        try {

            val media = photo?.let {
                upload(it)
            }

            val postWithAttachment = post.copy(
                attachment = media?.let {
                    Attachment(url = it.id, type = AttachmentType.IMAGE)
                })

            val response = apiService.save(postWithAttachment)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val post = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(post.copy(sended = true, showed = true)))
            return post
        } catch (_: IOException) {
            dao.insert(PostEntity.fromDto(post.copy(lastId)))
            throw NetworkError
        } catch (_: Exception) {
            dao.insert(PostEntity.fromDto(post.copy(lastId)))
            throw UnknownError
        }
    }

    private suspend fun upload(file: File): Media =
        apiService.upload(
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                file.asRequestBody()
            )
        )

    override suspend fun getAllAsync() {
        try {
            sendUnsentPost()
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body() ?: throw ApiError(response.code(), response.message())
            val entities = posts.map { post ->
                PostEntity.fromDto(post.copy(sended = true, showed = true))
            }
            dao.insert(entities)
        } catch (_: IOException) {
            throw NetworkError
        } catch (_: Exception) {
            throw UnknownError
        }
    }

    override suspend fun setLikeAsync(id: Long): Post {
        try {
            dao.like(id)
            val response = apiService.setLike(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val post = response.body() ?: throw ApiError(response.code(), response.message())
            return post
        } catch (_: IOException) {
            dao.like(id)
            throw NetworkError
        } catch (_: Exception) {
            dao.like(id)
            throw UnknownError
        }
    }

    override suspend fun setUnlikeAsync(id: Long): Post {
        try {
            dao.like(id)
            val response = apiService.setUnlike(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val post = response.body() ?: throw ApiError(response.code(), response.message())
            return post
        } catch (_: IOException) {
            dao.like(id)
            throw NetworkError
        } catch (_: Exception) {
            dao.like(id)
            throw UnknownError
        }
    }

    override fun isEmpty() = dao.isEmpty()
}