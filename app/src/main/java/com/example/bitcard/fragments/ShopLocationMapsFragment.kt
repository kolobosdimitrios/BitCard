package com.example.bitcard.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.bitcard.R
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.view_models.ItemViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ShopLocationMapsFragment : Fragment() {


    private val shopViewModel: ItemViewModel<Shop> by activityViewModels() {defaultViewModelProviderFactory}
    private lateinit var googleMap : GoogleMap
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        this.googleMap = googleMap
        shopViewModel.selectedItem.observe(viewLifecycleOwner) {
            renderMap(it)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_location_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun renderMap(shop: Shop){
        this.googleMap.clear()
        val latLng = LatLng(
            shop.location_latitude.toDouble(),
            shop.location_longitude.toDouble()
        )
        this.googleMap.addMarker(
            MarkerOptions()
                .position(
                    latLng
                )
        )
        Log.i("Marker created at", latLng.toString())
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }
}