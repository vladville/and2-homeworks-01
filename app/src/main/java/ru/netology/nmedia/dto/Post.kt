package ru.netology.nmedia.dto

data class Post (
    val id: Int,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 0,
    val likeByMe: Boolean = false,
    var shares: Int = 0,
    val views: Int = 0
)