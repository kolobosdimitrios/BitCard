package com.example.bitcard.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.example.bitcard.R
import com.example.bitcard.databinding.ActivityMainBottomNavigationBinding
import com.example.bitcard.db.entities.Coupon
import com.example.bitcard.db.entities.User
import com.example.bitcard.SharedPreferencesHelpers
import com.example.bitcard.network.data.requests.Token
import com.example.bitcard.network.data.responses.models.Purchase
import com.example.bitcard.network.data.responses.models.Shop
import com.example.bitcard.network.retrofit.client.APIClient
import com.example.bitcard.ui.fragments.home.HomeFragment
import com.example.bitcard.ui.fragments.home.HomeFragmentViewModel
import com.example.bitcard.ui.fragments.purchases.PurchasesFragment
import com.example.bitcard.ui.fragments.purchases.PurchasesFragmentViewModel
import com.example.bitcard.ui.fragments.shops.ShopsFragment
import com.example.bitcard.ui.fragments.shops.ShopsFragmentsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : AppCompatActivity() {

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
                R.id.navigation_home ->{
                    binding.mainScreenToolbar.title = getString(R.string.title_home)
                    setCurrentFragment(homeFragment)
                }
                R.id.navigation_shops ->{
                    binding.mainScreenToolbar.title = getString(R.string.ShopsActivityLabel)
                    setCurrentFragment(shopsFragment)
                }
                R.id.navigation_purchases ->{
                    binding.mainScreenToolbar.title = getString(R.string.purchases_fragment_title)
                    setCurrentFragment(purchasesFragment)
                }
            }
            true
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            backgroundSyncing()
        }
        binding.swipeRefreshLayout.setColorSchemeColors(getColor(R.color.primaryDarkColor), getColor(
            R.color.secondaryDarkColor
        ))

        backgroundSyncing()

    }

    private fun backgroundSyncing() {

        binding.swipeRefreshLayout.isRefreshing = true

        val userId = getUserId()
        CoroutineScope(Dispatchers.IO).launch {

            val isCompletedPurchase: Boolean
            val isCompletedShopList: Boolean
            val isCompletedTokenRefresh: Boolean
            val isCompletedUserDataRefresh: Boolean
            val isCompleteUserCouponsRefresh: Boolean
            try{

                val retrofitInstance = APIClient.newInstance

                val userDataResponse = retrofitInstance.get(userId).execute()
                isCompletedUserDataRefresh = userDataResponse.isSuccessful && userDataResponse.body() != null

                val userCouponsResponse = retrofitInstance.getCoupons(userId).execute()
                isCompleteUserCouponsRefresh = userCouponsResponse.isSuccessful && userCouponsResponse.body() != null

                val userPurchasesResponse = retrofitInstance.getUsersPurchases(userId).execute()
                isCompletedPurchase = userPurchasesResponse.isSuccessful && userPurchasesResponse.body() != null

                val shopsListResponse = retrofitInstance.getShops().execute()
                isCompletedShopList = shopsListResponse.isSuccessful && shopsListResponse.body() != null

                val tokenResponse = retrofitInstance.getToken(userId).execute()
                isCompletedTokenRefresh = tokenResponse.isSuccessful && tokenResponse.body() != null


                withContext(Main){
                    binding.swipeRefreshLayout.isRefreshing = false
                    if(isCompletedPurchase && isCompletedShopList && isCompletedTokenRefresh && isCompletedUserDataRefresh && isCompleteUserCouponsRefresh){

                        userDataResponse.body()?.let {
                                user -> syncUserInUi(user.data)
                        }

                        userCouponsResponse.body()?.let {
                                coupons -> syncUserCouponsInUi(coupons)
                        }

                        userPurchasesResponse.body()?.let {
                                purchases -> syncPurchasesInUi(purchases)
                        }

                        shopsListResponse.body()?.let {
                                shops -> syncShopsInUi(shops)
                        }

                        tokenResponse.body()?.let {
                            syncTokenInUi(it.data)
                        }

                        //Show success sync message
                        Snackbar.make(binding.root, R.string.sync_successfull, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getColor(R.color.primaryDarkColor))
                            .setTextColor(Color.WHITE)
                            .show()
                    }else{
                        //Show error success message
                        Snackbar.make(binding.root, R.string.sync_error, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.RED)
                            .setTextColor(Color.WHITE)
                            .show()
                    }
                }

            }catch (ioException: IOException){
                ioException.printStackTrace()
            }



        }
    }

    private suspend fun syncUserInUi(user: User){
        withContext(Main){
            homeFragmentViewModel.selectItem(user)
        }
    }

    private suspend fun syncUserCouponsInUi(coupons: List<Coupon>){
        withContext(Main){
            homeFragmentViewModel.selectItem(HomeFragmentViewModel.CouponsList(coupons))
        }
    }

    private suspend fun syncTokenInUi(token: Token){
        withContext(Main){
            homeFragmentViewModel.selectItem(token)
            SharedPreferencesHelpers.write(applicationContext, SharedPreferencesHelpers.USER_DATA, "token_id", token.id)
        }
    }

    private suspend fun syncShopsInUi(shopsList: List<Shop>){
        withContext(Main){
            shopsFragmentViewModel.selectShopList(shopsList)
        }
    }

    private suspend fun syncPurchasesInUi(purchases: List<Purchase>){
        withContext(Main){
            purchasesFragmentViewModel.setSelectedPurchases(
                purchases
            )
        }
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


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



}