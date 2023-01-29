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
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.R
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.ShopsListRecycler
import com.example.bitcard.databinding.FragmentShopsListBinding
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.view_models.ArrayListViewModel
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
    private val viewModel: ArrayListViewModel<Shop> by activityViewModels()
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

        viewModel.selectedItemsList.observe(viewLifecycleOwner, Observer { shops ->
            updateAdapter(shops)
        })

    }

    private fun updateAdapter(shops: ArrayList<Shop>){
        adapter.updateData(shops)
    }





    override fun onClick(adapterPosition: Int, model: Shop) {
        Log.i("Adapter position", adapterPosition.toString())
        Log.i("Model class", model.toString())
    }
}