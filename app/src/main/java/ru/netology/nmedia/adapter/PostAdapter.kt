package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.load
import ru.netology.nmedia.dto.numbersToThousands

interface OnInteractorListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)

    //fun onVideoPlay(post: Post)
    //fun onOpen(post: Post)

    fun onOpenFullImage(post: Post)
}

class PostAdapter(
    private val onInteractorListener: OnInteractorListener
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallBack) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknown item type")
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractorListener)
            }

            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }

            else -> error("unknown item type: $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
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
            isChecked = post.likedByMe
            text = numbersToThousands(post.likes)
        }

        menu.isVisible = post.ownedByMe

        if (!post.sended) {
            sendStatus.setBackgroundResource(R.drawable.ic_unsend)
        } else {
            sendStatus.setBackgroundResource(R.drawable.ic_send)
        }
        if (post.authorAvatar.isNotEmpty()) {
            avatar.load("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
        }

        if (!post.attachment?.url.isNullOrBlank()) {
            attachment.load("${BuildConfig.BASE_URL}/media/${post.attachment?.url}", 10)
            attachment.visibility = View.VISIBLE
            /*if (!post.attachment?.description.isNullOrBlank()) {
                attachment.contentDescription = post.attachment?.description
            }*/
        } else {
            attachment.visibility = View.GONE
        }
        //if (post.video.isNotEmpty()) {
        /*if (!post.video.isNullOrBlank()) {
            video.visibility = View.VISIBLE
            playVideoIcon.visibility = View.VISIBLE
        }*/
        /*playVideoIcon.setOnClickListener {
            onInteractorListener.onVideoPlay(post)
        }*/
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
        attachment.setOnClickListener {
            onInteractorListener.onOpenFullImage(post)
        }
        /*root.setOnClickListener {
            onInteractorListener.onOpen(post)
        }*/
    }
}

object PostDiffCallBack : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {

        if (oldItem::class != newItem::class) {
            return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}