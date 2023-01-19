package com.example.bitcard.db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.bitcard.db.entities.User

@Dao
interface UserDao{

    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE users.id = :user_id")
    fun getUser(user_id: Long)

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

}