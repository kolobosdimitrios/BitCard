package com.example.bitcard.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcard.R
import com.example.bitcard.Time
import com.example.bitcard.network.data.responses.models.Purchase

class PurchasesRecyclerViewAdapter(
    private val context: Context,
    private val onTileClickedListener: OnTileClickedListener<Purchase>,
    private val purchases : ArrayList<Purchase> = ArrayList()
) : RecyclerView.Adapter<PurchasesRecyclerViewAdapter.ViewHolder>() {

    fun clear(){
        purchases.clear()
        notifyDataSetChanged()
    }


    fun add(purchase: List<Purchase>){
        this.purchases.addAll(purchase)
        notifyItemInserted(this.purchases.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            itemView = LayoutInflater.from(context).inflate(R.layout.purchase_recycler_view_layout, parent, false),
            onTileClickedListener = onTileClickedListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.purchases[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return purchases.size
    }

    class ViewHolder(
        itemView: View,
        private val onTileClickedListener: OnTileClickedListener<Purchase>
        ) : RecyclerView.ViewHolder(itemView) {

        fun bind(purchase: Purchase, position: Int) {
            itemView.findViewById<TextView>(R.id.purchase_date).text = Time.format (purchase.created_at)
            itemView.setOnClickListener {
                onTileClickedListener.onClick(position + 1, purchase)
            }
        }

    }
}