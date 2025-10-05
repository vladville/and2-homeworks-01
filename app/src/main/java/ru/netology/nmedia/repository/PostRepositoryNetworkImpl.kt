package ru.netology.nmedia.repository

import androidx.lifecycle.map
import okhttp3.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import kotlin.text.isNullOrBlank

class PostRepositoryNetworkImpl(
    private val dao: PostDao,
) : PostRepository {

    val lastId = Long.MAX_VALUE

    override val data = dao.get().map(List<PostEntity>::toDto)

    override suspend fun sendUnsentPost() {
        try {
            val post = dao.getUnsentPost()
            if (post != null) {
                println("0510 " + post)
                val response = PostApi.service.save(post.toDto().copy(0L))
                if (!response.isSuccessful) {
                    throw RuntimeException()
                } else {
                    removeById(lastId)
                }
            }
        } catch (_: Exception) {
            throw RuntimeException()
        }
    }

    override suspend fun share(id: Long) {
        return dao.share(id)
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = PostApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw RuntimeException()
            }
        } catch (_: Exception) {
            throw RuntimeException()
        }
    }

    override suspend fun save(post: Post): Post {
        try {
            val response = PostApi.service.save(post)
            if (!response.isSuccessful) {
                throw RuntimeException()
            }
            val post = response.body() ?: throw RuntimeException()
            dao.insert(PostEntity.fromDto(post.copy(sended = true)))
            return post
        } catch (_: Exception) {
            dao.insert(PostEntity.fromDto(post.copy(lastId)))
            throw RuntimeException()
        }
    }

    override suspend fun getAllAsync() {
        sendUnsentPost()
        val response = PostApi.service.getAll()
        if (!response.isSuccessful) {
            throw RuntimeException()
        }
        val posts = response.body() ?: throw RuntimeException()
        posts.forEach { it.sended = true } //update posts from server
        dao.insert(posts.map(PostEntity::fromDto))
    }

    override suspend fun setLikeAsync(id: Long): Post {
        try {
            dao.like(id)
            val response = PostApi.service.setLike(id)
            if (!response.isSuccessful) {
                throw RuntimeException()
            }
            val post = response.body() ?: throw RuntimeException()
            return post
        } catch (_: Exception) {
            dao.like(id)
            throw RuntimeException()
        }
    }

    override suspend fun setUnlikeAsync(id: Long): Post {
        try {
            dao.like(id)
            val response = PostApi.service.setUnlike(id)
            if (!response.isSuccessful) {
                throw RuntimeException()
            }
            val post = response.body() ?: throw RuntimeException()
            return post
        } catch (_: Exception) {
            dao.like(id)
            throw RuntimeException()
        }
    }

    override fun isEmpty() = dao.isEmpty()
}