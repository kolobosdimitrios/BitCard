package com.example.bitcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.ProductsRecyclerViewAdapter
import com.example.bitcard.databinding.ActivityPurchaseInfoBinding
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.responses.models.Product
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseInfoActivity : AppCompatActivity(), OnTileClickedListener<Product> {

    private lateinit var binding: ActivityPurchaseInfoBinding
    private lateinit var adapter: ProductsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        getPurchaseIds()?.forEach { getPurchasesInfo(it) }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun getPurchasesInfo(purchaseId: Long){

        val api = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

        api.getPurchaseProducts(
            user_id = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
            getTokenId(),
            purchase_id = purchaseId
        ).enqueue(object : Callback<List<Product>>{
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if(response.isSuccessful){
                    response.body()?.forEach {
                        adapter.add(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getPurchaseIds(): LongArray? {

        return intent.getLongArrayExtra("purchase_ids")
    }

    private fun getTokenId() : Long {

        return intent.getLongExtra("token_id", Long.MIN_VALUE)
    }

    override fun onClick(adapterPosition: Int, model: Product) {

    }
}