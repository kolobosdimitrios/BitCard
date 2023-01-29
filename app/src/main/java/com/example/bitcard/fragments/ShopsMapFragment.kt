package com.example.bitcard.fragments

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.bitcard.R
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.view_models.ArrayListViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ShopsMapFragment : Fragment() {
    private val viewModel: ArrayListViewModel<Shop> by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shops_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(OnMapReadyCallback { googleMap ->

            googleMap.isMyLocationEnabled = true
            viewModel.selectedItemsList.observe(viewLifecycleOwner, Observer { shops ->
                shops.forEach {
                    Log.i("Show shop in map", it.toString())
                    val point = LatLng(it.location_latitude.toDouble(), it.location_longitude.toDouble())
                    googleMap.addMarker(MarkerOptions().position(point).title(it.shop_name))
                }
            })
            if (hasLocationPermissions()){
                fusedLocationClient.getCurrentLocation(

                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    Log.i("Location point is", "Latitude: $latitude Longitude: $longitude")
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15f))
                }.addOnFailureListener {

                }
            }
        })
    }

    private fun hasLocationPermissions() : Boolean {
        val coarseResult = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        val fineResult = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)

        return coarseResult == PackageManager.PERMISSION_GRANTED && fineResult == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocationUpdate(){

    }
}