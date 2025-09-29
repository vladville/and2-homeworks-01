package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto

class PostRepositoryNetworkImpl(
    private val dao: PostDao,
) : PostRepository {

//    override fun get(): List<Post> {
//        return PostApi.service.getAll()
//            .execute()
//            .let { it.body() ?: throw RuntimeException("body is null") }
//    }


    override val data = dao.get().map(List<PostEntity>::toDto)

    override suspend fun share(id: Long) {
        return dao.share(id)
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        PostApi.service.removeById(id)
        return
    }

    override suspend fun save(post: Post): Post {
        val postFromServer = PostApi.service.save(post)
        dao.insert(PostEntity.fromDto(postFromServer))

        return postFromServer
    }

    override suspend fun getAllAsync() {
        val posts = PostApi.service.getAll()
        dao.insert(posts.map(PostEntity::fromDto))
    }

    override suspend fun setLikeAsync(id: Long): Post {
        val post = PostApi.service.setLike(id)
        dao.like(id)
        return post
    }

    override suspend fun setUnlikeAsync(id: Long): Post {
        val post = PostApi.service.setUnlike(id)
        dao.like(id)
        return post
    }

    override fun isEmpty() = dao.isEmpty()
}