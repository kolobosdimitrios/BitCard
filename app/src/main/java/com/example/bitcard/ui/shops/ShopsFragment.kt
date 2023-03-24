package com.example.bitcard.ui.shops

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.ShopsListRecycler
import com.example.bitcard.databinding.ShopsFragmentBinding
import com.example.bitcard.network.daos.responses.models.Shop
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class ShopsFragment : Fragment(), OnTileClickedListener<Shop> {

    private lateinit var binder : ShopsFragmentBinding
    private val shopsViewModel : ShopsFragmentsViewModel by activityViewModels() {defaultViewModelProviderFactory}
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binder = ShopsFragmentBinding.inflate(inflater)
        this.adapter = ShopsListRecycler(this, requireContext())
        this.binder.shopsRecycler.layoutManager = LinearLayoutManager(requireContext())
        this.binder.shopsRecycler.setHasFixedSize(false)
        this.binder.shopsRecycler.adapter = adapter
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasLocationPermissions()){
            getLocationUpdate()
        }else{
            askLocationPermission()
        }
        shopsViewModel.shopsList.observe(viewLifecycleOwner){
            this.adapter.updateData(ArrayList(it))
        }

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

    override fun onClick(adapterPosition: Int, model: Shop) {
        TODO("Render shops information layout (activity or another fragment)")
    }

}