package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE showed = 1 ORDER BY id DESC")
    fun get(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) = 0 FROM posts")
    fun isEmpty(): LiveData<Boolean>

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) {
            insert(post)
        } else {
            updateById(post.id, post.content)
        }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE posts SET content=:content WHERE id=:id")
    suspend fun updateById(id: Long, content: String)

    @Query(
        """
           UPDATE posts SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = :id;
        """
    )
    suspend fun like(id: Long)

    @Query(
        """
           UPDATE posts SET
               shares = shares + 1
           WHERE id = :id;
        """
    )
    suspend fun share(id: Long)

    @Query("DELETE FROM posts WHERE id=:id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM posts WHERE sended = 0")
    suspend fun getUnsentPost(): PostEntity?

    @Query("UPDATE posts SET showed = 1 WHERE showed = 0")
    suspend fun setShowedPost()
}