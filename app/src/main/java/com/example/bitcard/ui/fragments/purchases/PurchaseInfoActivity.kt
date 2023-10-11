package com.example.bitcard.ui.fragments.purchases

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.ui.adapters.OnTileClickedListener
import com.example.bitcard.ui.adapters.ProductsRecyclerViewAdapter
import com.example.bitcard.databinding.ActivityPurchaseInfoBinding
import com.example.bitcard.network.data.responses.models.Product
import com.example.bitcard.network.retrofit.client.APIClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseInfoActivity : AppCompatActivity(), OnTileClickedListener<Product> {

    private lateinit var binding: ActivityPurchaseInfoBinding
    private lateinit var adapter: ProductsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityPurchaseInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.purchaseInfoToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        adapter = ProductsRecyclerViewAdapter(
            this,
            onTileClickedListener = this
        )

        binding.productsRecycler.layoutManager = LinearLayoutManager(this)
        binding.productsRecycler.setHasFixedSize(false)
        binding.productsRecycler.adapter = adapter
        val purchaseId = getPurchaseId()
        fetchData(purchaseId)


    }

    override fun onResume() {
        super.onResume()

    }

    private fun fetchData(purchaseId: Long){

        APIClient.newInstance.getPurchaseProducts(
            purchaseId
        ).enqueue(object : Callback<List<Product>>{
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if(response.isSuccessful){
                    response.body()?.forEach {
                        adapter.add(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                t.printStackTrace()
            }

        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getPurchaseId(): Long {

        return intent.getLongExtra("purchase_id", Long.MIN_VALUE)
    }

    override fun onClick(adapterPosition: Int, model: Product) {

    }
}