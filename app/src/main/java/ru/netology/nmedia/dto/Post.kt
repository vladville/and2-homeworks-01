package ru.netology.nmedia.dto

data class Post (
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val likeByMe: Boolean = false,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String = "",
)