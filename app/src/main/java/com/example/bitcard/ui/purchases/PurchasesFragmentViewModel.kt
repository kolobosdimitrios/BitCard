package com.example.bitcard.ui.purchases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bitcard.network.daos.responses.models.Purchase

class PurchasesFragmentViewModel : ViewModel()  {

    private val mutablePurchasesList = MutableLiveData<List<Purchase>>()

    val selectedPurchases : LiveData<List<Purchase>> get() = mutablePurchasesList

    fun setSelectedPurchases(purchases : List<Purchase>){
        mutablePurchasesList.value = purchases
    }
}