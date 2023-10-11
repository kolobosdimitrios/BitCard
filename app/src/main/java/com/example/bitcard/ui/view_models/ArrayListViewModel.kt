package com.example.bitcard.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ArrayListViewModel<L> : ViewModel() {

    private val mutableSelectedItem = MutableLiveData<ArrayList<L>>()
    val selectedItemsList: LiveData<ArrayList<L>> get() = mutableSelectedItem

    fun selectItemsList(items: ArrayList<L>) {
        mutableSelectedItem.value = items
    }

}