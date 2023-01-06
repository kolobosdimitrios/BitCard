package com.example.bitcard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcard.R

class PurchasesRecycler(
    private val context: Context,
    private val onTileClickedListener: OnTileClickedListener<PurchaseRecyclerModel>,
    private val purchaseRecyclerModels : ArrayList<PurchaseRecyclerModel> = ArrayList()
) : RecyclerView.Adapter<PurchasesRecycler.ViewHolder>() {

    fun clear(){
        purchaseRecyclerModels.clear()
        notifyDataSetChanged()
    }


    fun add(purchaseRecyclerModel: PurchaseRecyclerModel){
        purchaseRecyclerModels.add(purchaseRecyclerModel)
        notifyItemInserted(purchaseRecyclerModels.size - 1)
    }

    fun update(purchaseRecyclerModels: ArrayList<PurchaseRecyclerModel>){
        this.purchaseRecyclerModels.clear()
        this.purchaseRecyclerModels.addAll(purchaseRecyclerModels)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            itemView = LayoutInflater.from(context).inflate(R.layout.purchase_recycler_view_layout, parent, false),
            onTileClickedListener = onTileClickedListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(purchaseRecyclerModels[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return purchaseRecyclerModels.size
    }

    class ViewHolder(
        itemView: View,
        private val onTileClickedListener: OnTileClickedListener<PurchaseRecyclerModel>
        ) : RecyclerView.ViewHolder(itemView) {

        fun bind(purchaseRecyclerModel: PurchaseRecyclerModel, position: Int) {

            itemView.findViewById<TextView>(R.id.count_textView).text = position.toString()
            itemView.findViewById<TextView>(R.id.purchase_date).text = purchaseRecyclerModel.getTitle()
            itemView.setOnClickListener {
                onTileClickedListener.onClick(position + 1, purchaseRecyclerModel)
            }
        }

    }
}