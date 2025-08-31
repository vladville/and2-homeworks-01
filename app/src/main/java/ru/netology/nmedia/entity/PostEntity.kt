/*package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import kotlin.String

@Entity(tableName = "posts")
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String = "",
) {
    fun toDto() = Post(
        id = id,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likes = likes,
        likedByMe = likedByMe,
        shares = shares,
        views = views,
        //video = video
    )

    companion object {
       fun fromDto(post:Post) = post.run {
           PostEntity(
               id = id,
               author = author,
               authorAvatar = authorAvatar,
               content = content,
               published = published,
               likes = likes,
               likedByMe = likedByMe,
               shares = shares,
               views = views,
               //video = video
           )
       }
    }
}

fun Post.toEntity() = PostEntity(
    id = id,
    author = author,
    authorAvatar = authorAvatar,
    content = content,
    published = published,
    likes = likes,
    likedByMe = likedByMe,
    shares = shares,
    views = views,
    //video = video
)*/