package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractorListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false,
        )

        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        val adapter = PostAdapter(
            object : OnInteractorListener {
                override fun onLike(post: Post) {
                    viewModel.like(post.id)
                }

                override fun onShare(post: Post) {
                    viewModel.share(post.id)
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val intent2 =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))

                    startActivity(intent2)
                }

                override fun onVideoPlay(post: Post) {
                    val webpage: Uri = post.video.toUri()
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    startActivity(intent)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply {
                            textArgs = post.content
                        }
                    )
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

            }
        )

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val isNew = posts.size > adapter.itemCount //only if add operation
            adapter.submitList(posts) {
                if (isNew) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }

        return binding.root
    }

    companion object {
        var Bundle.textArgs by StringArg
    }

}