package com.example.bitcard

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.ShopsListRecycler
import com.example.bitcard.databinding.ActivityShopsBinding
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.view_models.ItemViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopsActivity : AppCompatActivity(), OnTileClickedListener<Shop> {

    private lateinit var binding: ActivityShopsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: ShopsListRecycler
    private val shopViewModel: ItemViewModel<Shop> by viewModels() {defaultViewModelProviderFactory}
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val api by lazy {
        RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)
    }

    val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, state: Int) {

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

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
        adapter = ShopsListRecycler(context = this, onTileClickedListener = this)
        binding.shopsRecycler.layoutManager = LinearLayoutManager(this)
        binding.shopsRecycler.setHasFixedSize(false)
        binding.shopsRecycler.adapter = adapter
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetPersistent)
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetBehaviorCallback)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (hasLocationPermissions()){
            getLocationUpdate()
        }else{
            askLocationPermission()
        }


    }

    override fun onStart() {
        super.onStart()
        getShops()
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

                        adapter.updateData(shops as ArrayList<Shop>)

                    }
                }
            }

            override fun onFailure(call: Call<List<Shop>>, t: Throwable) {
            }

        })
    }

    override fun onClick(adapterPosition: Int, model: Shop) {
        shopViewModel.selectItem(model)
        renderBottomSheet(model)
        expandCollapseSheet()
    }

    private fun expandCollapseSheet() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun renderBottomSheet(shop: Shop){
        binding.bottomSheet.shopTitle.text = shop.shop_name
    }

}