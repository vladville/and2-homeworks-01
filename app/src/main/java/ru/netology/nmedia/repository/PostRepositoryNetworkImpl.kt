package ru.netology.nmedia.repository

import androidx.lifecycle.map
import okhttp3.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto

class PostRepositoryNetworkImpl(
    private val dao: PostDao,
) : PostRepository {

    override val data = dao.get().map(List<PostEntity>::toDto)

    override suspend fun share(id: Long) {
        return dao.share(id)
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = PostApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw RuntimeException()
            }
            dao.removeById(id)
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
            dao.insert(PostEntity.fromDto(post))
            return post
        } catch (_: Exception) {
            throw RuntimeException()
        }
    }

    override suspend fun getAllAsync() {
        val posts = PostApi.service.getAll()
        dao.insert(posts.map(PostEntity::fromDto))
    }

    override suspend fun setLikeAsync(id: Long): Post {
        try {
            val response = PostApi.service.setLike(id)
            if (!response.isSuccessful) {
                throw RuntimeException()
            }
            val post = response.body() ?: throw RuntimeException()
            dao.like(id)
            return post
        } catch (_: Exception) {
            throw RuntimeException()
        }
    }

    override suspend fun setUnlikeAsync(id: Long): Post {
        try {
            val response = PostApi.service.setUnlike(id)
            if (!response.isSuccessful) {
                throw RuntimeException()
            }
            val post = response.body() ?: throw RuntimeException()
            dao.like(id)
            return post
        } catch (_: Exception) {
            throw RuntimeException()
        }
    }

    override fun isEmpty() = dao.isEmpty()
}