package com.example.bitcard.ui.shops

import android.Manifest
import android.content.Intent
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
import com.example.bitcard.databinding.SimpleRecyclerInCardviewLayoutBinding
import com.example.bitcard.network.daos.responses.models.Shop
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class ShopsFragment : Fragment(), OnTileClickedListener<Shop> {

    private lateinit var binder : SimpleRecyclerInCardviewLayoutBinding
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
        this.binder = SimpleRecyclerInCardviewLayoutBinding.inflate(inflater)
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
            if(hasLocationPermissions()) {
                getLocationUpdate()
            }else {
                //TODO: Show explanatory message!
            }
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
            this.adapter.updateDistanceFromUser(
                longitude = it.longitude.toFloat(),
                latitude = it.latitude.toFloat()
            )
            Log.i("Location point is", "Latitude: $latitude Longitude: $longitude")
        }.addOnFailureListener {

        }
    }

    override fun onClick(adapterPosition: Int, model: Shop) {
        val intent = Intent(requireContext(), ShopInformationActivity::class.java)
        val b = Bundle()
        b.putString("shop_name", model.shop_name)
        b.putString("location_name", model.location_name)
        b.putString("created_at", model.created_at)
        b.putString("updated_at", model.updated_at)
        b.putString("description", model.description)
        b.putString("location_address", model.location_address)
        b.putFloat("location_longitude", model.location_longitude)
        b.putFloat("location_latitude", model.location_latitude)
        b.putString("working_hours", model.working_hours)
        b.putString("contact_info", model.contact_info)
        b.putLong("shop_id", model.id)
        model.distanceFromUser?.let {
            b.putDouble("distance_from_user", it)
        }
        intent.putExtras(b)
        startActivity(intent)
    }

}