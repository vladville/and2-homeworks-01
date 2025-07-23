package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.toEntity

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {

    override fun get(): LiveData<List<Post>> {
        return dao.get().map { list ->
            list.map {
                it.toDto()
            }
        }
    }

    override fun like(id: Long) {
        return dao.like(id)
    }

    override fun share(id: Long) {
        return dao.share(id)
    }

    override fun removeById(id: Long) {
        return dao.removeById(id)
    }

    override fun save(post: Post) {
        //return dao.save(PostEntity.fromDto(post))
        return dao.save(post.toEntity())
    }

}