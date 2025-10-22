package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.textArgs
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    menuInflater.inflate(R.menu.auth_menu, menu)

                    viewModel.data.observe(this@AppActivity) {
                        val authorized = viewModel.isAuthorized
                        menu.setGroupVisible(R.id.authorized, authorized)
                        menu.setGroupVisible(R.id.unauthorized, !authorized)
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.singIn -> {
                            //TODO homework
                            AppAuth.getInstance().setAuth(5, "x-token")
                            true
                        }

                        R.id.singUp -> {
                            //TODO homework
                            AppAuth.getInstance().setAuth(5, "x-token")
                            true
                        }

                        R.id.logOut -> {
                            AppAuth.getInstance().removeAuth()
                            true
                        }

                        else -> false
                    }
            }
        )

        intent?.let {
            if (it.action != Intent.ACTION_SEND) return@let

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(
                    binding.root,
                    R.string.error_empty_content,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(android.R.string.ok) {
                        finish()
                    }.show()
                return@let
            }

            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArgs = text
                }
            )

        }
    }
}