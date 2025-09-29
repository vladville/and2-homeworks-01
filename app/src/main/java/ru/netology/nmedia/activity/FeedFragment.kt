package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractorListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ErrorViewBinding
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
            viewModel.editPostCancel()
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

                /*override fun onVideoPlay(post: Post) {
                    val webpage: Uri = post.video.toUri()
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    startActivity(intent)
                }*/

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

                override fun onOpen(post: Post) {
                    viewModel.editPostCancel()
                    findNavController().navigate(
                        R.id.action_feedFragment_to_onePostFragment,
                        Bundle().apply {
                            textArgs = post.id.toString()
                        }
                    )
                }

            }
        )

        binding.list.adapter = adapter

        val errorMergeBinding = ErrorViewBinding.bind(binding.root)

        viewModel.data.observe(viewLifecycleOwner) { state ->
            val isNew = state.posts.size > adapter.itemCount //only if add operation
            adapter.submitList(state.posts)
            binding.empty.isVisible = state.empty

            if (isNew) {
                binding.list.smoothScrollToPosition(0)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) {state ->
            binding.progress.isVisible = state.loading
            //errorMergeBinding.errorGroup.isVisible = state.error
            if (state.error) {
                Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) {
                        viewModel.loadPosts()
                    }.show()
            }
            binding.swipeRefresh.isRefreshing = state.refreshing

            //error by toast
            if (state.errorSetLike) {
                Snackbar.make(binding.root, R.string.network_like_error, Snackbar.LENGTH_LONG).show()
                //Toast.makeText(requireContext(), R.string.network_like_error, Toast.LENGTH_LONG).show()
            }
            if (state.errorUnLike) {
                Snackbar.make(binding.root, R.string.network_unlike_error, Snackbar.LENGTH_LONG).show()
                //Toast.makeText(requireContext(), R.string.network_unlike_error, Toast.LENGTH_LONG).show()
            }
            if (state.errorDelete) {
                Snackbar.make(binding.root, R.string.network_post_delete_error, Snackbar.LENGTH_LONG).show()
                //Toast.makeText(requireContext(), R.string.network_post_delete_error, Toast.LENGTH_LONG).show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        errorMergeBinding.retry.setOnClickListener{
            viewModel.loadPosts()
        }

        return binding.root
    }

    companion object {
        var Bundle.textArgs by StringArg
    }

}