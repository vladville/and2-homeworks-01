package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.activity.NewPostResultContract
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractorListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()
    private var currentEditingPost: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { content ->
            content ?: return@registerForActivityResult
            viewModel.changeContent(content)
            viewModel.save()
        }

        binding.fab.setOnClickListener {
            newPostLauncher.launch("")
        }

        val editPostLauncher = registerForActivityResult(NewPostResultContract()) { newContent ->
            currentEditingPost?.let { post ->
                viewModel.edit(post.copy(content = newContent.toString()))
                viewModel.save()
            }
        }

        val adapter = PostAdapter(
            object : OnInteractorListener {
                override fun onLike(post: Post) {
                    viewModel.like(post.id)
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val intent2 =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))

                    startActivity(intent2)
                }

                override fun onEdit(post: Post) {
                    currentEditingPost = post
                    editPostLauncher.launch(post.content)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

            }
        )

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val isNew = posts.size > adapter.itemCount //only if add operation
            adapter.submitList(posts) {
                if (isNew) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }

//        viewModel.edited.observe(this) { post ->
//            if (post.id != 0L) {
//                with(binding.content) {
//                    requestFocus()
//                    binding.editPreviewText.setText(post.content)
//                    binding.postEditGroup.visibility = View.VISIBLE
//                    setText(post.content)
//                }
//            } else {
//                binding.editPreviewText.setText("")
//                binding.postEditGroup.visibility = View.GONE
//            }
//        }

//        with(binding) {
//            previewCloseBtn.setOnClickListener {
//                editPreviewText.setText("")
//                postEditGroup.visibility = View.GONE
//                content.setText("")
//                content.clearFocus()
//                viewModel.editPostCancel()
//                AndroidUtils.hideKeyboard(it)
//            }
//            save.setOnClickListener {
//                if (content.text.isNullOrBlank()) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        R.string.error_empty_content,
//                        Toast.LENGTH_LONG
//                    ).show()
//                    return@setOnClickListener
//                }
//                viewModel.changeContent(content.text.toString())
//                viewModel.save()
//                content.setText("")
//                content.clearFocus()
//                AndroidUtils.hideKeyboard(it)
//            }
//        }

    }
}