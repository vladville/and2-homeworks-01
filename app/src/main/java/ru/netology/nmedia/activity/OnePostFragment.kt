package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.textArgs
import ru.netology.nmedia.adapter.OnInteractorListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentOnePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class OnePostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel: PostViewModel by viewModels()

        val binding = FragmentOnePostBinding.inflate(
            inflater,
            container,
            false,
        )

        val viewHolder = PostViewHolder(binding.post, object : OnInteractorListener {
            override fun onLike(post: Post) {
                //viewModel.like(post.id)
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
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onOpenFullImage(post: Post) {
            }

            /*override fun onOpen(post: Post) {
                findNavController().currentDestination
            }*/
        })


        /*viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id != 0L) {
                findNavController().navigate(
                    R.id.action_onePostFragment_to_newPostFragment,
                    Bundle().apply {
                        textArgs = it.content
                    }
                )
            }
        }*/

        val id = arguments?.textArgs?.toLong() ?: -1
        /*viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == id } ?: run {
                findNavController().navigateUp()
                return@observe
            }
            viewHolder.bind(post)
        }*/

        return binding.root
    }


}