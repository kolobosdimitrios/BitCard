package com.example.bitcard.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bitcard.db.entities.User

@Dao
interface UserDao{

    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE users.id = :user_id")
    fun getUser(user_id: Long) : LiveData<User?>

    @Query("SELECT * FROM users WHERE users.id = :user_id")
    fun get(user_id: Long) : User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM USERS WHERE id = :id")
    fun deleteWithId(id: Long)

    @Update
    fun update(new_user: User)

    @Update
    fun updateAll(vararg new_users: User)

}