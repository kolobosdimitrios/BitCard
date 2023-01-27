package com.example.bitcard.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.GnssAntennaInfo.SphericalCorrections
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.R
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.ShopsListRecycler
import com.example.bitcard.databinding.FragmentShopsListBinding
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A [Fragment] subclass. Displaying a RecyclerView with the shop listed.
 */
class ShopsListFragment : Fragment(), OnTileClickedListener<Shop> {

    private lateinit var binding: FragmentShopsListBinding
    private lateinit var adapter: ShopsListRecycler
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

    private val api by lazy {
        RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shops_list, container, false)
        binding = FragmentShopsListBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shopsRecycler.layoutManager = LinearLayoutManager(context)
        binding.shopsRecycler.setHasFixedSize(true)
        //TODO set adapter
        context?.let {
            adapter = ShopsListRecycler(context = it, onTileClickedListener = this)
            binding.shopsRecycler.adapter = adapter
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (hasLocationPermissions()){
            getLocationUpdate()
        }else{
            askLocationPermission()
        }

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
        api.getShops().enqueue(object : Callback<List<Shop>>{
            override fun onResponse(call: Call<List<Shop>>, response: Response<List<Shop>>) {
                if(response.isSuccessful){
                    val shopsList = response.body()
                    shopsList?.let {
                        if(it.isNotEmpty()){
                            it.forEach { shop ->
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
                            adapter.updateData(ArrayList(it))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Shop>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun hasLocationPermissions() : Boolean {
        val coarseResult = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        val fineResult = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)

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

    override fun onClick(adapterPosition: Int, model: Shop) {
        Log.i("Adapter position", adapterPosition.toString())
        Log.i("Model class", model.toString())
    }
}