package ru.netology.nmedia.dto

data class Post (
    val id: Int,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 12_999,
    var likeByMe: Boolean = false,
    var shares: Int = 1_095,
    var views: Int = 2_202_200
)