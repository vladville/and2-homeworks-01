package ru.netology.nmedia.dto

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String = "",
    val content: String,
    val published: String,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val shares: Int = 0,
    val views: Int = 0,
    var attachment: Attachment? = null,
    //val video: String = "",
    var sended: Boolean = false,
    var showed: Boolean = true,
    val ownedByMe: Boolean = false,
) : FeedItem

data class Ad(
    override val id: Long,
    val image: String,
) : FeedItem

enum class AttachmentType {
    IMAGE
}

data class Attachment(
    val url: String,
    val type: AttachmentType,
)