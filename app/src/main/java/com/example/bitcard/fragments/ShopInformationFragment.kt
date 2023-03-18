package com.example.bitcard.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bitcard.R
import com.example.bitcard.databinding.FragmentShopInformationBinding
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.view_models.GoogleMapsViewModel
import com.example.bitcard.view_models.ShopViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ShopInformationFragment: Fragment() {

    private val shopViewModel: ShopViewModel by activityViewModels() {defaultViewModelProviderFactory}
    private val mapShopViewModel: GoogleMapsViewModel by activityViewModels() {defaultViewModelProviderFactory}
    private lateinit var binding: FragmentShopInformationBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopInformationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shopViewModel.selectedItem.observe(
            viewLifecycleOwner
        ){
            mapShopViewModel.selectItem(it)
            renderUiWith(it)
        }
    }

    private fun renderUiWith(shop: Shop){

    }



     class ShopLocationMapsFragment : Fragment(), OnMapReadyCallback {
         private val mapShopViewModel: GoogleMapsViewModel by activityViewModels() {defaultViewModelProviderFactory}


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
            mapFragment?.getMapAsync(this)
        }

        override fun onMapReady(gMap: GoogleMap) {

            /**
             * Manipulates the map once available.
             * This callback is triggered when the map is ready to be used.
             * This is where we can add markers or lines, add listeners or move the camera.
             * In this case, we just add a marker near Sydney, Australia.
             * If Google Play services is not installed on the device, the user will be prompted to
             * install it inside the SupportMapFragment. This method will only be triggered once the
             * user has installed Google Play services and returned to the app.
             */
            /**
             * Manipulates the map once available.
             * This callback is triggered when the map is ready to be used.
             * This is where we can add markers or lines, add listeners or move the camera.
             * In this case, we just add a marker near Sydney, Australia.
             * If Google Play services is not installed on the device, the user will be prompted to
             * install it inside the SupportMapFragment. This method will only be triggered once the
             * user has installed Google Play services and returned to the app.
             */
            mapShopViewModel.selectedItem.observe(viewLifecycleOwner){
                renderMapWith(it, gMap)
            }

        }

        private fun renderMapWith(shop: Shop, gMap: GoogleMap){
            gMap.clear()
            val latLng = LatLng(
                shop.location_latitude.toDouble(),
                shop.location_longitude.toDouble()
            )
            gMap.addMarker(
                MarkerOptions()
                    .position(
                        latLng
                    )
            )
            Log.i("Marker created at", latLng.toString())
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }


    }



}