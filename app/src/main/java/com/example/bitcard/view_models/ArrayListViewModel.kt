package com.example.bitcard.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArrayListViewModel<L> : ViewModel() {

    private val mutableSelectedItem = MutableLiveData<ArrayList<L>>()
    val selectedItemsList: LiveData<ArrayList<L>> get() = mutableSelectedItem

    fun selectItemsList(items: ArrayList<L>) {
        mutableSelectedItem.value = items
    }

}