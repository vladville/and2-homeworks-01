package ru.netology.nmedia.repository

import android.widget.Toast
import retrofit2.Callback
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post

class PostRepositoryNetworkImpl : PostRepository {

    override fun get(): List<Post> {
        return PostApi.service.getAll()
            .execute()
            .let { it.body() ?: throw RuntimeException("body is null") }
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {

        PostApi.service.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(
                    call: retrofit2.Call<List<Post>>,
                    response: retrofit2.Response<List<Post>>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }

                    val posts = response.body()

                    if (posts == null) {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }

                    callback.onSuccess(posts)
                }

                override fun onFailure(
                    call: retrofit2.Call<List<Post>>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }

            })
    }

    override fun setLikeAsync(id: Long, callback: PostRepository.SetLikeCallback) {

        PostApi.service.setLike(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }

                    val post = response.body()
                    if (post == null) {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }

                    callback.onSuccess(post)
                }

                override fun onFailure(
                    call: retrofit2.Call<Post>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }
            })
    }

    override fun setUnlikeAsync(id: Long, callback: PostRepository.SetUnLikeCallback) {

        PostApi.service.setUnlike(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }

                    val post = response.body()
                    if (post == null) {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }

                    callback.onSuccess(post)
                }

                override fun onFailure(
                    call: retrofit2.Call<Post>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }

            })
    }

    override fun share(id: Long) {
//        return dao.share(id)
    }

    override fun save(post: Post): Post {
        PostApi.service.save(post)
            .execute()

        return post
    }

    override fun removeById(id: Long) {
        PostApi.service.removeById(id)
            .execute()
    }

}