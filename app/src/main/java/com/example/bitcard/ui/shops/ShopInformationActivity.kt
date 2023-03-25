package com.example.bitcard.ui.shops

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.example.bitcard.R
import com.example.bitcard.SettingsActivity
import com.example.bitcard.databinding.ActivityShopInformationBinding
import com.example.bitcard.network.daos.responses.models.Shop
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class ShopInformationActivity : AppCompatActivity() {

    private lateinit var binder: ActivityShopInformationBinding

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.shop_information_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.add_to_favorites -> {
                item.icon.setTint(Color.YELLOW)
                setShopAsFavorite()
            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun setShopAsFavorite(){

        Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show()
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

        override fun onMapReady(p0: GoogleMap) {
            requireActivity().intent.extras?.let {
                val shop = Data.getShopFromIntentData(
                    it
                )
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