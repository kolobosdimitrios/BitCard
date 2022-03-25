package com.example.bitcard

import android.graphics.ColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.databinding.ActivityPurchaseHistoryBinding

class PurchaseHistoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPurchaseHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.purchaseHistoryToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        binding.purchaseHistoryRecyclerView.setHasFixedSize(true)
        binding.purchaseHistoryRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}