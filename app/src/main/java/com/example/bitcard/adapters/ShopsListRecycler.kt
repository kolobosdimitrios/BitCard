package com.example.bitcard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcard.R
import com.example.bitcard.globals.Distance
import com.example.bitcard.network.daos.responses.models.Shop

class ShopsListRecycler(
    val onTileClickedListener: OnTileClickedListener<Shop>,
    val context: Context

): RecyclerView.Adapter<ShopsListRecycler.ShopViewHolder>() {

    private val shopsList : ArrayList<Shop> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.short_shop_info_tile_layout, parent, false)
        return ShopViewHolder(v)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(shopsList[holder.adapterPosition], onTileClickedListener, holder.adapterPosition, context)
    }

    override fun getItemCount(): Int {
        return shopsList.size
    }

    fun updateData(newShops: ArrayList<Shop>){
        shopsList.clear()
        notifyItemRemoved(0)
        shopsList.addAll(newShops)
        notifyItemInserted(0)
    }

    class ShopViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(shop : Shop, onTileClickedListener: OnTileClickedListener<Shop>, adapterPosition: Int, context: Context){

            val generalLocationTextView = itemView.findViewById<TextView>(R.id.general_location)
            generalLocationTextView.text = shop.location_name
            val addressTextView = itemView.findViewById<TextView>(R.id.street_address)
            addressTextView.text = shop.location_address
            val currentDistanceTextView = itemView.findViewById<TextView>(R.id.current_distance)
            currentDistanceTextView.text = Distance.toKms(shop.distanceFromUser).toString().plus(" ").plus(context.getString(R.string.distance_unit_metric))
            itemView.setOnClickListener {
                onTileClickedListener.onClick(adapterPosition, shop)
            }
        }

    }


}