package com.example.bitcard.ui.shops

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.bitcard.R
import com.example.bitcard.databinding.ActivityShopInformationBinding
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.requests.FavoriteShopModel
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.daos.responses.models.Shop
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

    private lateinit var binder: ActivityShopInformationBinding
    private val viewModel: ItemViewModel<Boolean> by viewModels() { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binder = ActivityShopInformationBinding.inflate(layoutInflater)
        setContentView(binder.root)
        setSupportActionBar(binder.mainScreenToolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.extras?.let { getShopFromIntentData(it).shop_name }

    }

    override fun onResume() {
        super.onResume()
        if(intent.extras == null) {
            throw IllegalStateException("intent extras are null")
        }
        intent.extras?.let { extras ->
            isShopFavorite(
                SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
                getShopFromIntentData(extras).id
            )
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            viewModel.selectedItem.observe(this){ isFavorite ->
                if (isFavorite){
                    menu[0].icon.setTint(Color.YELLOW)
                }else{
                    menu[0].icon.setTint(getColor(R.color.primaryDarkColor))
                }

            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.shop_information_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.add_to_favorites -> {
                intent.extras?.let { extras ->
                    viewModel.selectedItem.value?.let { isFavorite ->
                        if(isFavorite){
                            removeShopFromFavorites(
                                userId = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
                                shopId = getShopFromIntentData(extras).id
                            )

                        }else{
                            setShopAsFavorite(extras)
                        }
                    }
                }

            }

        }

        return super.onOptionsItemSelected(item)
    }


    private fun setShopAsFavorite(bundle: Bundle){
        val favShopModel = FavoriteShopModel(
            user_id = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
            shop_id = getShopFromIntentData(bundle).id
        )
        RetrofitHelper.newInstance.setShopAsFavorite(favShopModel).enqueue(object : Callback<SimpleResponse<Any>>{
            override fun onResponse(
                call: Call<SimpleResponse<Any>>,
                response: Response<SimpleResponse<Any>>
            ) {
                if(response.isSuccessful){
                    val bd = response.body()!!
                    if(bd.status_code == SimpleResponse.STATUS_OK){
                        viewModel.selectItem(true)
                        runOnUiThread { Toast.makeText(applicationContext, R.string.added_to_favorite_shops, Toast.LENGTH_SHORT).show() }
                    }
                }
            }

            override fun onFailure(call: Call<SimpleResponse<Any>>, t: Throwable) {
                runOnUiThread{ Toast.makeText(applicationContext, R.string.fail, Toast.LENGTH_SHORT).show() }

            }

        })

    }

    private fun isShopFavorite(userId: Long, shopId: Long){
        RetrofitHelper.newInstance.isShopFavorite(user_id = userId, shop_id = shopId).enqueue(object : Callback<SimpleResponse<Any>>{
            override fun onResponse(
                call: Call<SimpleResponse<Any>>,
                response: Response<SimpleResponse<Any>>
            ) {
                if(response.isSuccessful){
                   response.body()?.let { body ->
                       if(body.status_code == SimpleResponse.STATUS_OK){
                           viewModel.selectItem(true)
                       }else{
                           viewModel.selectItem(false)
                       }
                   }
                }
            }

            override fun onFailure(call: Call<SimpleResponse<Any>>, t: Throwable) {
                Toast.makeText(applicationContext, R.string.fail, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun removeShopFromFavorites(userId: Long, shopId: Long){
        RetrofitHelper.newInstance.removeShopFromFavorites(user_id = userId, shop_id = shopId).enqueue(object : Callback<SimpleResponse<Any>>{
            override fun onResponse(
                call: Call<SimpleResponse<Any>>,
                response: Response<SimpleResponse<Any>>
            ) {
                if(response.isSuccessful){
                    val bd = response.body()!!
                    if(bd.status_code == SimpleResponse.STATUS_OK){
                        viewModel.selectItem(false)
                        runOnUiThread { Toast.makeText(applicationContext, R.string.removed_from_favorite_shops, Toast.LENGTH_SHORT).show() }
                    }
                }
            }

            override fun onFailure(call: Call<SimpleResponse<Any>>, t: Throwable) {
                Toast.makeText(applicationContext, R.string.fail, Toast.LENGTH_SHORT).show()
            }

        })
    }

    class MapFragment : Fragment(), OnMapReadyCallback{

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
            requireActivity().intent.extras?.let {
                val shop = Data.getShopFromIntentData(
                    it
                )
                val latLng = LatLng(shop.location_latitude.toDouble(), shop.location_longitude.toDouble())
                gMap.addMarker(
                    MarkerOptions()
                        .position(
                            latLng
                        ).title(shop.shop_name)
                )
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            }

        }


    }

    companion object Data{
        private fun getShopFromIntentData(b: Bundle) : Shop {
            return Shop(
                b.getLong("shop_id"),
                b.getString("shop_name", ""),
                b.getString("location_name", ""),
                b.getFloat("location_latitude"),
                b.getFloat("location_longitude"),
                b.getString("description", ""),
                b.getString("location_address", ""),
                b.getString("created_at", ""),
                b.getString("updated_at", ""),
                b.getDouble("distance_from_user")
            )

        }
    }


}