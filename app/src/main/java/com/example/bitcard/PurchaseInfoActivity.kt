package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.bitcard.databinding.ActivityPurchaseInfoBinding
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.responses.models.Product
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class PurchaseInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPurchaseInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.purchaseInfoToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)



    }

    override fun onStart() {
        super.onStart()
        getPurchaseIds()?.forEach { getPurchasesInfo(it) }

    }

    private fun getPurchasesInfo(purchaseId: Long){

        val api = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

        api.getPurchaseProducts(
            user_id = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
//            token_id = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "token_id"),
            1,
            purchase_id = purchaseId
        ).enqueue(object : Callback<List<Product>>{
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if(response.isSuccessful){
                    Log.i("Purchase id", purchaseId.toString())
                    response.body()?.forEach {
                        Log.i("product", it.toString())
                    }
                    Log.i("End", "-----------------------------------------")
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
}