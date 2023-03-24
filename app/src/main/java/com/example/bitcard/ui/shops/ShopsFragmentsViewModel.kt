package com.example.bitcard.ui.shops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bitcard.network.daos.responses.models.Shop

class ShopsFragmentsViewModel : ViewModel() {

    private val mutableSelectedShop = MutableLiveData<Shop>()
    private val mutableShopsList = MutableLiveData<List<Shop>>()

    val selectedShop : LiveData<Shop> get() = mutableSelectedShop
    val shopsList : LiveData<List<Shop>> get() = mutableShopsList

    fun selectShop(shop: Shop){
        this.mutableSelectedShop.value = shop
    }

    fun selectShopList(shopList: List<Shop>){
        this.mutableShopsList.value = shopList
    }
}