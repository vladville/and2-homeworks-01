package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.numbersToThousands

//typealias OnLikeListener = (post: Post) -> Unit
//typealias OnShareListener = (post: Post) -> Unit
//typealias OnRemoveListener = (post: Post) -> Unit
//typealias OnEditListener = (post: Post) -> Unit

interface OnInteractorListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onVideoPlay(post: Post)
    fun onOpen(post: Post)
}

class PostAdapter(
    private val onInteractorListener: OnInteractorListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallBack) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractorListener)
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractorListener: OnInteractorListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) = with(binding) {
        author.text = post.author
        content.text = post.content
        published.text = post.published
        shareIcon.text = numbersToThousands(post.shares)
        viewCount.text = numbersToThousands(post.views)
        likeIcon.apply {
            isChecked = post.likeByMe
            text = numbersToThousands(post.likes)
        }
        if (post.video.isNotEmpty()) {
            video.visibility = View.VISIBLE
            playVideoIcon.visibility = View.VISIBLE
        }
        playVideoIcon.setOnClickListener {
            onInteractorListener.onVideoPlay(post)
        }
        likeIcon.setOnClickListener {
            onInteractorListener.onLike(post)
        }
        shareIcon.setOnClickListener {
            onInteractorListener.onShare(post)
        }
        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.post_options)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            onInteractorListener.onRemove(post)
                            true
                        }

                        R.id.edit -> {
                            onInteractorListener.onEdit(post)
                            true
                        }

                        else -> false
                    }
                }
            }.show()
        }
        root.setOnClickListener {
            onInteractorListener.onOpen(post)
        }
    }
}

object PostDiffCallBack : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}