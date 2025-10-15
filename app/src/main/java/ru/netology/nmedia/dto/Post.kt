package ru.netology.nmedia.dto

data class Post (
    val id: Long,
    val author: String,
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
    var showed: Boolean = true
)

enum class AttachmentType {
    IMAGE
}
data class Attachment(
    val url: String,
    val type: AttachmentType,
)