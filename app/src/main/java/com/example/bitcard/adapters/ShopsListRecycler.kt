package com.example.bitcard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcard.R

class ShopsListRecycler(
    val onTileClickedListener: OnTileClickedListener<ShopTileModel>,
    val context: Context

): RecyclerView.Adapter<ShopsListRecycler.ShopViewHolder>() {

    private val shopsList : ArrayList<ShopTileModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.short_shop_info_tile_layout, parent, false)
        return ShopViewHolder(v)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(shopsList[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return shopsList.size
    }

    fun updateData(newShops: ArrayList<ShopTileModel>){
        shopsList.clear()
        notifyItemRemoved(0)
        shopsList.addAll(newShops)
        notifyItemInserted(0)
    }

    class ShopViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(shop : ShopTileModel){

            val generalLocationTextView = itemView.findViewById<TextView>(R.id.general_location)
            generalLocationTextView.text = shop.generalLocation
            val addressTextView = itemView.findViewById<TextView>(R.id.street_address)
            addressTextView.text = shop.address
            val currentDistanceTextView = itemView.findViewById<TextView>(R.id.current_distance)
            currentDistanceTextView.text = shop.currentDistance
        }

    }

    data class ShopTileModel(
        val generalLocation: String,
        val address: String,
        var currentDistance: String
    )
}