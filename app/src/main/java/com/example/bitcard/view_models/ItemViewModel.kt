package com.example.bitcard.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ItemViewModel<I>: ViewModel() {

    private val mutableSelectedItem = MutableLiveData<I>()
    val selectedItem: LiveData<I> get() = mutableSelectedItem

    fun selectItem(item: I){
        mutableSelectedItem.value = item
    }

}