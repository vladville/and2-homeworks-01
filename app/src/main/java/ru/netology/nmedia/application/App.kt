package ru.netology.nmedia.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.di.DependencyContainer
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var auth: AppAuth

    override fun onCreate() {
        super.onCreate()
        setupAuth()
        DependencyContainer.initApp(this)
    }

    private fun setupAuth() {
        appScope.launch {
            auth.sendPushToken()
        }
    }
}