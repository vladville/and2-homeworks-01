package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import kotlin.collections.map

class PostRepositoryNetworkImpl(
    private val dao: PostDao,
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
                val response = PostApi.service.save(post.toDto().copy(0L))
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                } else {
                    removeById(lastId)
                }
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostApi.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = PostApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post): Post {
        try {
            val response = PostApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val post = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(post.copy(sended = true)))
            return post
        } catch (e: IOException) {
            dao.insert(PostEntity.fromDto(post.copy(lastId)))
            throw NetworkError
        } catch (e: Exception) {
            dao.insert(PostEntity.fromDto(post.copy(lastId)))
            throw UnknownError
        }
    }

    override suspend fun getAllAsync() {
        try {
            sendUnsentPost()
            val response = PostApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body() ?: throw ApiError(response.code(), response.message())
            posts.forEach { it.sended = true } //update posts from server
            dao.insert(posts.map(PostEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun setLikeAsync(id: Long): Post {
        try {
            dao.like(id)
            val response = PostApi.service.setLike(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val post = response.body() ?: throw ApiError(response.code(), response.message())
            return post
        } catch (e: IOException) {
            dao.like(id)
            throw NetworkError
        } catch (e: Exception) {
            dao.like(id)
            throw UnknownError
        }
    }

    override suspend fun setUnlikeAsync(id: Long): Post {
        try {
            dao.like(id)
            val response = PostApi.service.setUnlike(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val post = response.body() ?: throw ApiError(response.code(), response.message())
            return post
        } catch (e: IOException) {
            dao.like(id)
            throw NetworkError
        } catch (e: Exception) {
            dao.like(id)
            throw UnknownError
        }
    }

    override fun isEmpty() = dao.isEmpty()
}