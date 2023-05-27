package com.example.bitcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.example.bitcard.databinding.ActivityMainBottomNavigationBinding
import com.example.bitcard.db.entities.Coupon
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.daos.responses.TokenResponse
import com.example.bitcard.network.daos.responses.models.Purchase
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.ui.home.HomeFragment
import com.example.bitcard.ui.home.HomeFragmentViewModel
import com.example.bitcard.ui.purchases.PurchasesFragment
import com.example.bitcard.ui.purchases.PurchasesFragmentViewModel
import com.example.bitcard.ui.shops.ShopsFragment
import com.example.bitcard.ui.shops.ShopsFragmentsViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityBottomNavigation : AppCompatActivity() {

private lateinit var binding: ActivityMainBottomNavigationBinding

    private val homeFragmentViewModel : HomeFragmentViewModel by viewModels() {defaultViewModelProviderFactory}
    private val shopsFragmentViewModel : ShopsFragmentsViewModel by viewModels() {defaultViewModelProviderFactory}
    private val purchasesFragmentViewModel : PurchasesFragmentViewModel by viewModels() {defaultViewModelProviderFactory}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
         binding = ActivityMainBottomNavigationBinding.inflate(layoutInflater)
         setContentView(binding.root)

        setSupportActionBar(binding.mainScreenToolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val homeFragment = HomeFragment()
        val shopsFragment = ShopsFragment()
        val purchasesFragment = PurchasesFragment()

        binding.mainScreenToolbar.title = getString(R.string.title_home)
        setCurrentFragment(homeFragment)

        binding.navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home->{
                    binding.mainScreenToolbar.title = getString(R.string.title_home)
                    setCurrentFragment(homeFragment)
                }
                R.id.navigation_shops->{
                    binding.mainScreenToolbar.title = getString(R.string.ShopsActivityLabel)
                    setCurrentFragment(shopsFragment)
                }
                R.id.navigation_purchases->{
                    binding.mainScreenToolbar.title = getString(R.string.purchases_fragment_title)
                    setCurrentFragment(purchasesFragment)
                }
            }
            true
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            sync()
        }
        binding.swipeRefreshLayout.setColorSchemeColors(getColor(R.color.primaryDarkColor), getColor(R.color.secondaryDarkColor))
        sync()

    }

    private fun backgroundSyncing(){

    }

    private fun sync(){
        fetchUserData(getUserId())
        fetchShopsListData()
        getTokensPurchasesHistory(getUserId())
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.toolbar_settings_button -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    private fun getUserId() : Long {
        return SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id")
    }

    private fun getToken(userId : Long){
        RetrofitHelper.newInstance.getToken(userId).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if(response.isSuccessful){
                    val tokenResponse = response.body()
                    if(tokenResponse != null){
                        when(tokenResponse.status_code){
                            SimpleResponse.STATUS_OK, SimpleResponse.STATUS_IGNORE -> {
                                runOnUiThread {
                                    homeFragmentViewModel.selectItem(tokenResponse.data)
                                    SharedPreferencesHelpers.write(applicationContext, SharedPreferencesHelpers.USER_DATA, "token_id", tokenResponse.data.id)
                                }
                            }
                            SimpleResponse.STATUS_ERROR -> {

                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
            }

        })
    }

    private fun getUserCoupons(userId: Long){


        RetrofitHelper.newInstance.getCoupons(userId).enqueue(object : Callback<List<Coupon>>{
            override fun onResponse(call: Call<List<Coupon>>, response: Response<List<Coupon>>) {
                if (response.isSuccessful){
                    runOnUiThread {
                        response.body()?.let {
                            homeFragmentViewModel.selectItem(HomeFragmentViewModel.CouponsList(it))
//                            CoroutineScope(Dispatchers.IO).launch {
                                /**
                                 * Update datebase (write new - delete old).
                                 */
//                                database.couponsDao().deleteAll()
//                                database.couponsDao().insert(it.toTypedArray())
//                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                Log.e("CALL FAILED", call.toString())
            }

        })
    }

    private fun getUserData(userId: Long){
        RetrofitHelper.newInstance.get(userId).enqueue(object : Callback<GetUserResponse>{

            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                if(response.isSuccessful) {
                    val simpleResponse = response.body()
                    simpleResponse.let {
                        if(it != null){
                            if(it.status_code == SimpleResponse.STATUS_OK){
                                //render
                                runOnUiThread {
                                    homeFragmentViewModel.selectItem(it.data)
                                    /**
                                     * Save user to database
                                     */
//                                    CoroutineScope(Dispatchers.IO).launch {
//                                        Log.i("user", "UPDATED")
//                                        database.userDao().update(it.data)
//
//                                    }

                                }

                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.e("CALL FAILED", call.toString())
            }
        })

    }

    private fun getShops(){
        RetrofitHelper.newInstance.getShops().enqueue(object : Callback<List<Shop>> {
            override fun onResponse(call: Call<List<Shop>>, response: Response<List<Shop>>) {
                if(response.isSuccessful){
                    val shopsList = response.body()
                    shopsList?.let { shops ->
                        shopsFragmentViewModel.selectShopList(shops)
                    }
                }
            }

            override fun onFailure(call: Call<List<Shop>>, t: Throwable) {
            }

        })
    }

    private fun getTokensPurchasesHistory(user_id : Long){

        RetrofitHelper.newInstance.getUsersPurchases(user_id).enqueue( object :
            Callback<List<Purchase>> {
            override fun onResponse(
                call: Call<List<Purchase>>,
                response: Response<List<Purchase>>
            ) {
                if(response.isSuccessful){
                    val body = response.body()
                    if(body != null) {
                        purchasesFragmentViewModel.setSelectedPurchases(
                            body
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<Purchase>>, t: Throwable) {

            }

        })

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun fetchUserData(userId: Long){
        getUserData(userId)
        getToken(userId)
        getUserCoupons(userId)
    }

    private fun fetchShopsListData(){

        getShops()
    }

}