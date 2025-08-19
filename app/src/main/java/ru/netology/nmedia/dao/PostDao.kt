package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun get(): LiveData<List<PostEntity>>

    fun save(post: PostEntity) {
        if (post.id == 0L) {
            insert(post)
        } else {
            updateById(post.id, post.content)
        }
    }

    @Insert
    fun insert(entity: PostEntity)

    @Query("UPDATE posts SET content=:content WHERE id=:id")
    fun updateById(id: Long,content: String)

    @Query("""
           UPDATE posts SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = :id;
        """)
    fun like(id: Long)

    @Query("""
           UPDATE posts SET
               shares = shares + 1
           WHERE id = :id;
        """)
    fun share(id: Long)

    @Query("DELETE FROM posts WHERE id=:id")
    fun removeById(id: Long)
}