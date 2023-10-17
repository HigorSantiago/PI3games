package com.gamelink.gamelinkapp.service.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gamelink.gamelinkapp.service.model.PostModel

@Dao
interface PostDAO {
    @Insert
    fun save(post: PostModel)

    @Query("SELECT * FROM POSTS")
    fun list(): List<PostModel>

    @Query("SELECT * FROM POSTS WHERE user_id = :userId")
    fun listByUser(userId: Int): List<PostModel>
}