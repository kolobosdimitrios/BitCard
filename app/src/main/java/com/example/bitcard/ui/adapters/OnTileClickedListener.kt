package com.example.bitcard.ui.adapters

interface OnTileClickedListener<ModelClass> {

    fun onClick(adapterPosition : Int, model : ModelClass)
}