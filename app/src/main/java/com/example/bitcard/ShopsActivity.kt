package com.example.bitcard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bitcard.adapters.ViewPagerAdapter
import com.example.bitcard.databinding.ActivityShopsBinding
import com.example.bitcard.fragments.ShopInformationFragment
import com.example.bitcard.fragments.ShopsListFragment
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.view_models.ShopViewModel
import com.example.bitcard.view_models.ShopsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val shopsViewModel: ShopsViewModel by viewModels() {defaultViewModelProviderFactory}
    private val shopViewModel: ShopViewModel by viewModels() {defaultViewModelProviderFactory}
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val api by lazy {
        RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getLocationUpdate()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityShopsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager,
            lifecycle
        )

        binding.viewPager.isUserInputEnabled = true //for now!!!!!
        binding.viewPager.adapter = viewPagerAdapter

        viewPagerAdapter.addFragment(ShopsListFragment())
        viewPagerAdapter.addFragment(ShopInformationFragment())


        if (hasLocationPermissions()){
            getLocationUpdate()
        }else{
            askLocationPermission()
        }

        getShops()
        shopViewModel.selectedItem.observe(this){
            supportActionBar?.let {
                bar -> bar.title = it.shop_name
                binding.viewPager.currentItem = 1
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun hasLocationPermissions() : Boolean {
        val coarseResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        val fineResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        return coarseResult == PackageManager.PERMISSION_GRANTED && fineResult == PackageManager.PERMISSION_GRANTED
    }

    private fun askLocationPermission(){
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getLocationUpdate(){
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener {
            val latitude = it.latitude
            val longitude = it.longitude
            Log.i("Location point is", "Latitude: $latitude Longitude: $longitude")
        }.addOnFailureListener {

        }
    }
    private fun getShops(){
        api.getShops().enqueue(object : Callback<List<Shop>> {
            override fun onResponse(call: Call<List<Shop>>, response: Response<List<Shop>>) {
                if(response.isSuccessful){
                    val shopsList = response.body()
                    shopsList?.let { shops ->

                        shopsViewModel.selectItemsList(ArrayList(shops))

                    }
                }
            }

            override fun onFailure(call: Call<List<Shop>>, t: Throwable) {
            }

        })
    }







}