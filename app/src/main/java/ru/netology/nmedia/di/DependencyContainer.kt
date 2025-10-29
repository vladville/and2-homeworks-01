package ru.netology.nmedia.di

import android.content.Context
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryNetworkImpl
import java.util.concurrent.TimeUnit

class DependencyContainer(
    private val context: Context
) {

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"

        private const val TIMEOUT = 0L

        @Volatile
        private var instance: DependencyContainer? = null

        fun initApp(context: Context) {
            instance = DependencyContainer(context)
        }
        fun getInstance(): DependencyContainer {
            return instance!!        }
    }

    val appAuth = AppAuth(context)
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = appAuth.authStateFlow.value.token?.let { token ->
                chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
            } ?: chain.request()

            chain.proceed(request)
        }
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
    private val appDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .build()

    private val postDao = appDb.postDao()
    val apiService = retrofit.create<ApiService>()

    val repository: PostRepository = PostRepositoryNetworkImpl(
        postDao,
        apiService,
    )

}