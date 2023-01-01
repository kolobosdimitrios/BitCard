package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.SimpleRecycler
import com.example.bitcard.adapters.TitleAndValueModel
import com.example.bitcard.databinding.ActivityPurchaseHistoryBinding
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.requests.Token
import com.example.bitcard.network.daos.responses.PurchasesListResponse
import com.example.bitcard.network.daos.responses.models.Purchase
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseHistoryActivity : AppCompatActivity(), OnTileClickedListener<TitleAndValueModel> {

    private lateinit var binding : ActivityPurchaseHistoryBinding

    private var usersApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

    private lateinit var adapter : SimpleRecycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.purchaseHistoryToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        adapter = SimpleRecycler(
            applicationContext,
            this,
            ArrayList()
        )

        binding.purchaseHistoryRecyclerView.setHasFixedSize(true)
        binding.purchaseHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.purchaseHistoryRecyclerView.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        getUsersTokensHistory(
            SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id")
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getUsersTokensHistory(user_id: Long){

        usersApiV1.getUserTokens(user_id).enqueue(object : Callback<List<Token>> {
            override fun onResponse(
                call: Call<List<Token>>,
                response: Response<List<Token>>
            ) {
                if(response.isSuccessful){
                    Log.i("response", "successful with code : " + response.code())
                    val tokens = response.body()
                    if( ! tokens.isNullOrEmpty() )
                        for (t in tokens){
                            getTokensPurchasesHistory(user_id = t.user_id, t.id)
                        }
                }else Log.i("response", "un-successful with code : " + response.code())
            }

            override fun onFailure(call: Call<List<Token>>, t: Throwable) {
                Log.e("response", "failed")
            }

        })

    }

    private fun getTokensPurchasesHistory(user_id : Long, token_id : Long){

        usersApiV1.getTokensPurchases(user_id, token_id).enqueue( object : Callback<List<Purchase>>{
            override fun onResponse(
                call: Call<List<Purchase>>,
                response: Response<List<Purchase>>
            ) {
                if(response.isSuccessful){
                    val body = response.body()
                    if(body != null) {
                        for (p in body) {
                            addPurchaseToAdapter(p)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Purchase>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun addPurchaseToAdapter(purchase: Purchase){
        runOnUiThread{
            //TODO : add the purchase instance to recycler view!!
            val tmp = TitleAndValueModel(
                title = purchase.id.toString(),
                value = purchase.created_at
            )
            adapter.add(tmp)
        }
    }

    override fun onClick(adapterPosition: Int, model: TitleAndValueModel) {

        Toast.makeText(applicationContext, "You clicked : " + model.title, Toast.LENGTH_SHORT).show()

    }

}