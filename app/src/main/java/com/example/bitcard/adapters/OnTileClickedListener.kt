package com.example.bitcard.adapters

interface OnTileClickedListener<ModelClass> {

    fun onClick(adapterPosition : Int, model : ModelClass)
}