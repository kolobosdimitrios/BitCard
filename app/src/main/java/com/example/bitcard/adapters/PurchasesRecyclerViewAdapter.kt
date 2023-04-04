package com.example.bitcard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcard.R
import com.example.bitcard.globals.Time
import com.example.bitcard.network.daos.responses.models.Purchase

class PurchasesRecyclerViewAdapter(
    private val context: Context,
    private val onTileClickedListener: OnTileClickedListener<PurchaseRecyclerModel>,
    private val recyclerModels : ArrayList<PurchaseRecyclerModel> = ArrayList()
) : RecyclerView.Adapter<PurchasesRecyclerViewAdapter.ViewHolder>() {

    fun clear(){
        recyclerModels.clear()
        notifyDataSetChanged()
    }


    fun add(purchases: PurchaseRecyclerModel){
        this.recyclerModels.add(purchases)
        notifyItemInserted(recyclerModels.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            itemView = LayoutInflater.from(context).inflate(R.layout.purchase_recycler_view_layout, parent, false),
            onTileClickedListener = onTileClickedListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.recyclerModels[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return recyclerModels.size
    }

    class ViewHolder(
        itemView: View,
        private val onTileClickedListener: OnTileClickedListener<PurchaseRecyclerModel>
        ) : RecyclerView.ViewHolder(itemView) {

        fun bind(recyclerModel: PurchaseRecyclerModel, position: Int) {
            itemView.findViewById<TextView>(R.id.purchase_date).text = recyclerModel.getTitle()
            itemView.setOnClickListener {
                onTileClickedListener.onClick(position + 1, recyclerModel)
            }
        }

    }
}