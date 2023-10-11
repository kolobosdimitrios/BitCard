package com.example.bitcard.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bitcard.db.entities.Coupon
import com.example.bitcard.db.entities.User
import com.example.bitcard.network.data.requests.Token

class HomeFragmentViewModel : ViewModel(){

    private val mutableSelectedUser = MutableLiveData<User>()
    private val mutableSelectedToken = MutableLiveData<Token>()
    private val mutableSelectedCoupons = MutableLiveData<List<Coupon>>()

    val selectedUser: LiveData<User> get() = mutableSelectedUser
    val selectedToken: LiveData<Token> get() = mutableSelectedToken
    val selectedCoupons: LiveData<List<Coupon>> get() = mutableSelectedCoupons

    fun <I> selectItem(item : I){
        when(item){
            is User -> mutableSelectedUser.value = item
            is Token -> mutableSelectedToken.value = item
            is CouponsList -> mutableSelectedCoupons.value = item.coupons
            else -> {
                throw IllegalStateException("Unresolved data type")
            }
        }
    }

    data class CouponsList(
        val coupons: List<Coupon>
    ){}
}