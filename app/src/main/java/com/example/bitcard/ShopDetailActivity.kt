package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.SimpleRecycler
import com.example.bitcard.adapters.TitleAndValueModel
import com.example.bitcard.databinding.ActivityLoginBinding
import com.example.bitcard.databinding.ActivityShopDetailBinding
import com.example.bitcard.fragments.ShopLocationMapsFragment


class ShopDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_detail)

        binding = ActivityShopDetailBinding.inflate(layoutInflater)

        binding.contactInfoRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.contactInfoRecyclerView.setHasFixedSize(true)
        binding.contactInfoRecyclerView.adapter = SimpleRecycler(applicationContext, onTileClickedListener = object :
            OnTileClickedListener<TitleAndValueModel> {
            override fun onClick(adapterPosition: Int, model: TitleAndValueModel) {
                Log.i("Contact info adapter","Model $model clicked @ position $adapterPosition")
            }
        })

        binding.openingHoursRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.openingHoursRecyclerView.setHasFixedSize(true)
        binding.openingHoursRecyclerView.adapter = SimpleRecycler(applicationContext, onTileClickedListener = object :
            OnTileClickedListener<TitleAndValueModel> {
            override fun onClick(adapterPosition: Int, model: TitleAndValueModel) {
                Log.i("Contact info adapter","Model $model clicked @ position $adapterPosition")
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}