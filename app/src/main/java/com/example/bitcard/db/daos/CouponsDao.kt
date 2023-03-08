package com.example.bitcard.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bitcard.db.entities.Coupon

@Dao
interface CouponsDao {

    @Query("SELECT * FROM coupons")
    fun getAll() : LiveData<List<Coupon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(coupons: Array<Coupon>)

    @Query("delete from coupons")
    fun deleteAll()

    @Delete
    fun delete(coupon: Coupon)

    @Query("select * from coupons where id = :coupon_id")
    fun get(coupon_id: Long): LiveData<Coupon>
}