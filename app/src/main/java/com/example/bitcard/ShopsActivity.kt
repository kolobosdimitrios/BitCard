package com.example.bitcard

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.bitcard.adapters.ViewPagerAdapter
import com.example.bitcard.databinding.ActivityShopsBinding
import com.example.bitcard.fragments.ShopsListFragment
import com.example.bitcard.fragments.ShopsMapFragment
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.view_models.ArrayListViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: ArrayListViewModel<Shop> by viewModels()
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
        binding = ActivityShopsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
        viewPagerAdapter.addFragment(ShopsListFragment())
        viewPagerAdapter.addFragment(ShopsMapFragment())

        binding.viewPager.adapter = viewPagerAdapter


        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
            tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.ShopsListFragmentTitle)
                1 -> tab.text = getString(R.string.ShopsMapsFragmentTitle)
            }
        }.attach()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (hasLocationPermissions()){
            getLocationUpdate()
        }else{
            askLocationPermission()
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
            getShops(
                latitude = latitude,
                longitude = longitude
            )
        }.addOnFailureListener {

        }
    }
    private fun getShops(latitude: Double, longitude: Double){
        api.getShops().enqueue(object : Callback<List<Shop>> {
            override fun onResponse(call: Call<List<Shop>>, response: Response<List<Shop>>) {
                if(response.isSuccessful){
                    val shopsList = response.body()
                    shopsList?.let { shops ->
                        if(shops.isNotEmpty()){
                            shops.forEach { shop ->
                                val floats = FloatArray(1)
                                Location.distanceBetween(
                                    latitude,
                                    longitude,
                                    shop.location_latitude.toDouble(),
                                    shop.location_longitude.toDouble(),
                                    floats
                                )
                                shop.distanceFromUser = floats[0].toDouble()
                            }
                            viewModel.selectItemsList(ArrayList(shops))

                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Shop>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

}