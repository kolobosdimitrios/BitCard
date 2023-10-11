package com.example.bitcard.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.bitcard.R
import com.example.bitcard.BitmapHandler
import com.example.bitcard.network.data.responses.models.Product

class ProductsRecyclerViewAdapter(
    private val context: Context,
    private val products: ArrayList<Product> = ArrayList(),
    private val onTileClickedListener: OnTileClickedListener<Product>
) : RecyclerView.Adapter<ProductsRecyclerViewAdapter.ProductViewHolder>() {


    fun add(product: Product) {
        products.add(product)
        notifyItemInserted(products.size - 1)
    }

    fun clear(){
        products.clear()
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {

        return ProductViewHolder(
            context = context,
            itemView = LayoutInflater.from(context).inflate(R.layout.product_recycler_view_layout, parent, false),
            onTileClickedListener = onTileClickedListener
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ProductViewHolder(
        itemView: View,
        private val onTileClickedListener: OnTileClickedListener<Product>,
        private val context: Context
    ) : ViewHolder(itemView) {

        fun bind(product: Product, position: Int) {

            itemView.findViewById<TextView>(R.id.product_name).text = product.name
            itemView.findViewById<TextView>(R.id.product_description).text = product.description
            itemView.findViewById<TextView>(R.id.product_price).text = context.getString(R.string.value) + " : " + product.value.toString()

            itemView.setOnClickListener {
                onTileClickedListener.onClick(position, product)
            }

            itemView.findViewById<ImageView>(R.id.product_image).setImageBitmap(
                product.image?.let { BitmapHandler.decodeImageToBitmap(it) }
            )
        }

    }
}