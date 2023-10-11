package com.example.bitcard.ui.fragments.shops

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.bitcard.R
import com.example.bitcard.databinding.ActivityShopInformationBinding
import com.example.bitcard.JSONParser
import com.example.bitcard.SharedPreferencesHelpers
import com.example.bitcard.network.data.requests.FavoriteShopModel
import com.example.bitcard.network.data.responses.SimpleResponse
import com.example.bitcard.network.data.responses.models.Shop
import com.example.bitcard.network.retrofit.client.APIClient
import com.example.bitcard.ui.view_models.ItemViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShopInformationActivity : AppCompatActivity(){

    private lateinit var binder: ActivityShopInformationBinding
    private val viewModel: ItemViewModel<Boolean> by viewModels { defaultViewModelProviderFactory }
    private lateinit var shop : Shop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binder = ActivityShopInformationBinding.inflate(layoutInflater)
        setContentView(binder.root)
        setSupportActionBar(binder.mainScreenToolbar)
        shop = getShopFromIntentData(intent.extras!!)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binder.shopInfoMapBanner.goToMapsLl.setOnClickListener {
            openGoogleMaps(
                locationLatitude = shop.location_latitude,
                locationLongitude = shop.location_longitude
            )
        }
        binder.shopInfoMapBanner.contactPhoneLl.setOnClickListener {
            displayDialog()
        }
        isShopFavorite(
            userId = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
            shopId = shop.id
        )
        binder.shopNameTextView.text = shop.shop_name
        binder.fullAddressTextView.text = shop.location_address

        val socialMedia = SocialMedia(JSONParser.parseString(shop.contact_info).getJSONObject("social_media"))

        renderSocialMedia(socialMedia)

        binder.facebookImageView.setOnClickListener {

        }

        binder.twitterImageView.setOnClickListener {

        }

    }

    private fun openTwitterIntent(userName: String){

        val twitterUri = Uri.parse("http://twitter.com/$userName")
        val twitterIntent = Intent(Intent.ACTION_VIEW, twitterUri)
        twitterIntent.setPackage("com.twitter.android")
        try {
            startActivity(twitterIntent)
        } catch (e: ActivityNotFoundException) {
            /*
            App not exist
             */
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.twitter.com/$userName")
                )
            )
        }

    }

    private fun openFacebookIntent(userName: String){
        // Open Facebook profile
        val facebookUri = Uri.parse("http://www.facebook.com/$userName")
        val facebookIntent = Intent(Intent.ACTION_VIEW, facebookUri)
        facebookIntent.setPackage("com.facebook.katana")
        try {
            startActivity(facebookIntent)
        } catch (e: ActivityNotFoundException) {
            /*
            App not exist
             */
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.facebook.com/$userName")
                )
            )
        }
    }

    private fun openInstagramIntent(userName: String){
        val uri = Uri.parse("http://instagram.com/_u/$userName")

        val i = Intent(Intent.ACTION_VIEW, uri)

        i.setPackage("com.instagram.android")

        try {
            startActivity(i)
        } catch (e: ActivityNotFoundException) {
            /*
            App not exist
             */
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/$userName")
                )
            )
        }
    }

    private fun openGoogleMaps(locationLatitude: Float, locationLongitude: Float){

        val latLng = "$locationLatitude,$locationLongitude"
        val gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=$latLng")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

    }

    private fun displayDialog(){
        val phoneNumbers = getPhoneNumbersArray(JSONParser.parseString(shop.contact_info).getJSONArray("phones"))
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.contact_phone))
            .setItems(phoneNumbers) { _, which ->
                // Handle the click event for the selected phone number
                val phoneNumber: String = phoneNumbers[which]
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)
            ) { dialog, _ -> dialog.cancel() }

        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun renderSocialMedia(socialMedia: SocialMedia){
        if(socialMedia.instagram.isNotEmpty()){
            binder.instagramImageView.visibility = View.VISIBLE
            binder.instagramImageView.setOnClickListener {
                openInstagramIntent(socialMedia.instagram)
            }
        }else{
            binder.instagramImageView.visibility = View.GONE
        }

        if(socialMedia.facebook.isNotEmpty()){
            binder.facebookImageView.visibility = View.VISIBLE
            binder.facebookImageView.setOnClickListener {
                openFacebookIntent(socialMedia.facebook)
            }
        }else{
            binder.facebookImageView.visibility = View.GONE
        }

        if(socialMedia.twitter.isNotEmpty()){
            binder.twitterImageView.visibility = View.VISIBLE
            binder.twitterImageView.setOnClickListener {
                openTwitterIntent(socialMedia.twitter)
            }
        }else{
            binder.twitterImageView.visibility = View.GONE
        }
    }

    private fun getPhoneNumbersArray(jsonArray: JSONArray):Array<String>{
        val strings = ArrayList<String>()
        for (index in 1 until jsonArray.length() step 1){
            strings.add(jsonArray.get(index).toString())
        }
        return strings.toTypedArray()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            viewModel.selectedItem.observe(this){ isFavorite ->
                if (isFavorite){
                    menu[0].icon?.setTint(Color.YELLOW)
                }else{
                    menu[0].icon?.setTint(getColor(R.color.primaryDarkColor))
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
                viewModel.selectedItem.value?.let { isFavorite ->
                    if(isFavorite){
                        removeShopFromFavorites(
                            userId = SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id"),
                            shopId = shop.id
                        )

                    }else{
                        setShopAsFavorite()
                    }
                }

            }

        }

        return super.onOptionsItemSelected(item)
    }


    private fun setShopAsFavorite(){
        //Create the model with requests params
        val favShopModel = FavoriteShopModel(
            user_id = SharedPreferencesHelpers.readLong(
                applicationContext,
                SharedPreferencesHelpers.USER_DATA, "id"
            ),
            shop_id = shop.id
        )
        //Get the APIClient instance and execute setShopAsFavorite method.
        APIClient.newInstance.setShopAsFavorite(favShopModel)
            .enqueue(
                object : Callback<SimpleResponse<Any>>{
                    override fun onResponse(
                        call: Call<SimpleResponse<Any>>,
                        response: Response<SimpleResponse<Any>>
                    ) {
                        //Check if response is successful
                        if(response.isSuccessful){
                            //take the body with not null assertion (!!)
                            val bd = response.body()!!
                            //check the status code from the body
                            if(bd.status_code == SimpleResponse.STATUS_OK){
                                //update view model and GUI.
                                viewModel.selectItem(true)
                                //inform user with a Toast.
                                runOnUiThread {
                                    Toast.makeText(
                                        applicationContext,
                                        R.string.added_to_favorite_shops,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<SimpleResponse<Any>>, t: Throwable) {
                        //inform user with a Toast.
                        runOnUiThread{
                            Toast.makeText(
                                applicationContext,
                                R.string.fail,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
    }

    private fun isShopFavorite(userId: Long, shopId: Long){
        APIClient.newInstance.isShopFavorite(user_id = userId, shop_id = shopId).enqueue(object : Callback<SimpleResponse<Any>>{
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
        APIClient.newInstance.removeShopFromFavorites(user_id = userId, shop_id = shopId).enqueue(object : Callback<SimpleResponse<Any>>{
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
                val shop = getShopFromIntentData(
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
                b.getString("working_hours", ""),
                b.getString("contact_info", ""),
                b.getDouble("distance_from_user")
            )

        }
    }

    data class SocialMedia(
        val jsonObject: JSONObject
    ){
        val instagram: String = jsonObject.get("instagram").toString()

        val facebook: String = jsonObject.get("facebook").toString()

        val twitter: String = jsonObject.get("twitter").toString()

    }



}