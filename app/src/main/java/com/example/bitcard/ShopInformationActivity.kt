package com.example.bitcard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.bitcard.databinding.ActivityShopInformationBinding
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.example.bitcard.view_models.ItemViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopInformationBinding
    private val shopViewModel: ItemViewModel<Shop> by viewModels() {defaultViewModelProviderFactory}
    private val api by lazy {
        RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)
    }


    companion object{
        fun getIntent(context: Context, shop_id: Long) : Intent{
            val intent = Intent(context, ShopInformationActivity::class.java)
            intent.putExtra("shop_id", shop_id)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getShop(
            shopId = getShopId()
        )
    }

    override fun onStart() {
        super.onStart()

    }

    private fun getShopId() : Long{
        return intent.getLongExtra("shop_id", -1)
    }

    private fun getShop(shopId: Long){

        api.getShop(shopId).enqueue(object : Callback<Shop>{
            override fun onResponse(call: Call<Shop>, response: Response<Shop>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        renderUiWith(it)
                    }
                }
            }

            override fun onFailure(call: Call<Shop>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun renderUiWith(shop: Shop){
        binding.toolbar.title = shop.shop_name
        shopViewModel.selectItem(shop)
    }





    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    class ShopLocationMapsFragment : Fragment(), OnMapReadyCallback {

        private val shopViewModel: ItemViewModel<Shop> by activityViewModels() {defaultViewModelProviderFactory}


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

            shopViewModel.selectedItem.observe(this) {
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

