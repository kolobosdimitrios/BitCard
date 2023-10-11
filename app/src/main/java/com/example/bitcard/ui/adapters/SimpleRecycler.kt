package com.example.bitcard.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcard.R

open class SimpleRecycler(
    private val context : Context,
    private val onTileClickedListener: OnTileClickedListener<TitleAndValueModel>? = null,
    private val models: ArrayList<TitleAndValueModel> = ArrayList()
) : RecyclerView.Adapter<SimpleRecycler.SimpleViewHolder>() {

    open fun update(models: ArrayList<TitleAndValueModel>){

        models.clear()
        models.addAll(models)
        notifyItemInserted(0)

    }

    fun clear() = models.clear()

    open fun add(model: TitleAndValueModel){

        models.add(model)
        notifyItemInserted(models.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        return SimpleViewHolder(
            itemView = LayoutInflater.from(context).inflate(R.layout.title_and_value_tile, parent, false),
            onTileClickedListener = onTileClickedListener
        )
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(models[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return models.size
    }

    class SimpleViewHolder(
        itemView: View,
        //Feature Use
        var isClickable : Boolean = false,
        var isSelectable : Boolean = false,
        //
        private val onTileClickedListener: OnTileClickedListener<TitleAndValueModel>?
    ) : RecyclerView.ViewHolder(itemView){



        fun bind(model: TitleAndValueModel){
            val titleTextView : TextView = itemView.findViewById(R.id.title)
            val valueTextView : TextView = itemView.findViewById(R.id.value)
            titleTextView.text = model.title
            valueTextView.text = model.value
            itemView.setOnClickListener {
                onTileClickedListener?.onClick(
                    adapterPosition = adapterPosition, model = model
                )
            }
        }

    }

}