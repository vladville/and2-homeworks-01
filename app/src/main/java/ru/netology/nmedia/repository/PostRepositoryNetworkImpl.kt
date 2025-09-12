package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post

class PostRepositoryNetworkImpl : PostRepository {

    override fun get(): List<Post> {
        return PostApi.service.getAll()
            .execute()
            .let { it.body() ?: throw RuntimeException("body is null") }
    }

    override fun getAllAsync(callback: PostRepository.PostCallback<List<Post>>) {

        PostApi.service.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(
                    call: Call<List<Post>>,
                    response: Response<List<Post>>
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
                    call: Call<List<Post>>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }

            })
    }

    override fun setLikeAsync(id: Long, callback: PostRepository.PostCallback<Post>) {

        PostApi.service.setLike(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: Call<Post>,
                    response: Response<Post>
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
                    call: Call<Post>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }
            })
    }

    override fun setUnlikeAsync(id: Long, callback: PostRepository.PostCallback<Post>) {

        PostApi.service.setUnlike(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: Call<Post>,
                    response: Response<Post>
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
                    call: Call<Post>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }

            })
    }

    override fun share(id: Long) {
//        return dao.share(id)
    }

    override fun save(post: Post, callback: PostRepository.PostCallback<Post>) {
        PostApi.service.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }

                    val body = response.body()
                    if (body == null) {
                        callback.onError(RuntimeException("body is null"))
                    } else {
                        callback.onSuccess(body)
                    }

                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callback.onError(e)
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.PostCallback<Unit>) {
        PostApi.service.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(t)
                }
            })
    }
}