package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.databinding.ActivityPurchaseHistoryBinding
import com.example.bitcard.network.daos.requests.Token
import com.example.bitcard.network.daos.responses.PurchasesListResponse
import com.example.bitcard.network.daos.responses.TokenListResponse
import com.example.bitcard.network.daos.responses.models.Purchase
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseHistoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPurchaseHistoryBinding

    private var usersApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

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

    private fun getUsersTokensHistory(user_id: Long){

        usersApiV1.getUserTokens(user_id).enqueue(object : Callback<TokenListResponse> {
            override fun onResponse(
                call: Call<TokenListResponse>,
                response: Response<TokenListResponse>
            ) {
                if(response.isSuccessful){
                    if(response.body() != null){
                        val tokens = response.body()!!.tokens
                        for(token in tokens){
                            getTokensPurchasesHistory(user_id, token.id)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TokenListResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun getTokensPurchasesHistory(user_id : Long, token_id : Long){

        usersApiV1.getTokensPurchases(user_id, token_id).enqueue( object : Callback<PurchasesListResponse>{
            override fun onResponse(
                call: Call<PurchasesListResponse>,
                response: Response<PurchasesListResponse>
            ) {
                if(response.isSuccessful){
                    val body = response.body()
                    if(body != null){
                        val purchasesListResponse = body.purchases
                        for ( p in purchasesListResponse){
                            addPurchaseToAdapter(p)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PurchasesListResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun addPurchaseToAdapter(purchase: Purchase){
        runOnUiThread{
            //TODO : add the purchase instance to recycler view!!
        }
    }
}