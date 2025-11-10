package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractorListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostLoadingStateAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
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

        val viewModel: PostViewModel by viewModels()

        binding.fab.setOnClickListener {
            viewModel.editPostCancel()
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        val adapter = PostAdapter(
            object : OnInteractorListener {
                override fun onLike(post: Post) {
                    viewModel.like(post)
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

                override fun onOpenFullImage(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_fullScreenImageFragment,
                        Bundle().apply {
                            textArgs = post.attachment?.url
                        }
                    )
                }

                /*override fun onOpen(post: Post) {
                    viewModel.editPostCancel()
                    findNavController().navigate(
                        R.id.action_feedFragment_to_onePostFragment,
                        Bundle().apply {
                            textArgs = post.id.toString()
                        }
                    )
                }*/

            }
        )

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter { adapter.retry() },
            footer = PostLoadingStateAdapter { adapter.retry() }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter::submitData)
            }
        }

        /*viewModel.data.observe(viewLifecycleOwner) { state ->
            val isNew = state.posts.size > adapter.itemCount //only if add operation
            adapter.submitList(state.posts)
            binding.empty.isVisible = state.empty

            if (isNew) {
                binding.list.smoothScrollToPosition(0)
            }
        }*/

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing =
                        state.refresh is LoadState.Loading ||
                                state.prepend is LoadState.Loading ||
                                state.append is LoadState.Loading
                }
            }
        }

        /*viewModel.newerCount.observe(viewLifecycleOwner) { state ->
            val haveNew = state > 0
            binding.newPostBtn.isVisible = haveNew
        }*/

        binding.newPostBtn.setOnClickListener {
            binding.newPostBtn.isVisible = false
            viewModel.getNewerPosts()
            binding.list.smoothScrollToPosition(0)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) {
                        viewModel.loadPosts()
                    }.show()
            }
            binding.swipeRefresh.isRefreshing = state.refreshing

            //error by snackbar
            if (state.errorSetLike) {
                Snackbar.make(binding.root, R.string.network_like_error, Snackbar.LENGTH_LONG)
                    .show()
            }
            if (state.errorUnLike) {
                Snackbar.make(binding.root, R.string.network_unlike_error, Snackbar.LENGTH_LONG)
                    .show()
            }
            if (state.errorDelete) {
                Snackbar.make(
                    binding.root,
                    R.string.network_post_delete_error,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            adapter.refresh()
        }

        return binding.root
    }

    companion object {
        var Bundle.textArgs by StringArg
    }

}