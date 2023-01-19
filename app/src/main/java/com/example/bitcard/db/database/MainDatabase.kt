package com.example.bitcard.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bitcard.db.daos.UserDao
import com.example.bitcard.db.entities.User

@Database(entities = [User::class], version = 1)
abstract class MainDatabase : RoomDatabase() {

    abstract fun userDao() : UserDao

    companion object {
        private var instance: MainDatabase? = null

        fun getInstance(context: Context) : MainDatabase{
            if (instance == null) {
                synchronized(this) {
                    instance =
                        Room.databaseBuilder(
                            context,
                            MainDatabase::class.java,
                            "main_database")
                            .build()
                }
            }
            return instance!!
        }
    }

}