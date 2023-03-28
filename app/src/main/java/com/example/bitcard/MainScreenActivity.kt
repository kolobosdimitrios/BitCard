package com.example.bitcard


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bitcard.databinding.ActivityMainScreenWNavDrawerBinding
import com.example.bitcard.databinding.MainScreenMenuBinding
import com.example.bitcard.db.database.MainDatabase
import com.example.bitcard.db.entities.Coupon
import com.example.bitcard.db.entities.User
import com.example.bitcard.globals.QR
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.daos.responses.TokenResponse
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreenWNavDrawerBinding
    private lateinit var menuBind : MainScreenMenuBinding
    private val database by lazy { MainDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainScreenWNavDrawerBinding.inflate(layoutInflater)
        menuBind = MainScreenMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.mainScreenLayout.mainScreenToolbar,
            R.string.open,
            R.string.close
        )

        binding.drawerLayout.addDrawerListener(drawerToggle)

        drawerToggle.syncState()

        binding.menu.purchaseHistoryOption.setOnClickListener{
            startActivity(Intent(this, PurchaseHistoryActivity::class.java))
        }

        binding.menu.settingsOption.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

//        binding.menu.shopListOption.setOnClickListener {
//            startActivity(Intent(this, ShopsActivity::class.java))
//        }

        binding.mainScreenLayout.profilePicture.setOnClickListener {
            startActivity(Intent(this, ProfileInfoActivity::class.java))
        }

        binding.menu.logoutOption.setOnClickListener {
            callLogout(getUserId())
        }
        getUserData(userId = getUserId())
        getToken(userId = getUserId())
        getUserCoupons(userId = getUserId())
    }

    override fun onResume() {
        super.onStart()

    }

    private fun getUserData(userId: Long){
        val bitcardApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

        bitcardApiV1.get(userId).enqueue(object : Callback<GetUserResponse>{

            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                if(response.isSuccessful) {
                    val simpleResponse = response.body()
                    simpleResponse.let {
                        if(it != null){
                            if(it.status_code == SimpleResponse.STATUS_OK){
                                //render
                                runOnUiThread {
                                    renderLayoutWithUserData(it.data)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        Log.i("user", "UPDATED")
                                        database.userDao().update(it.data)

                                    }

                                }

                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.e("CALL FAILED", call.toString())
            }
        })

    }

    private fun getUserCoupons(userId: Long){
        val bitcardApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

        bitcardApiV1.getCoupons(userId).enqueue(object : Callback<List<Coupon>>{
            override fun onResponse(call: Call<List<Coupon>>, response: Response<List<Coupon>>) {
                if (response.isSuccessful){
                    runOnUiThread {
                        response.body()?.let {
                            renderCouponsAndPoints(it)
                            CoroutineScope(Dispatchers.IO).launch {

                                    database.couponsDao().deleteAll()

                                    database.couponsDao().insert(it.toTypedArray())


                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                Log.e("CALL FAILED", call.toString())
            }

        })
    }

    private fun renderCouponsAndPoints(coupons: List<Coupon>){

        binding.mainScreenLayout.availableCouponsTextView.text = coupons.size.toString()

    }

    private fun renderLayoutWithUserData(user: User){

        Log.e("user data", user.toString())

        val nameSurname = String.format(resources.getString(R.string.space_between), user.name, user.surname)

        binding.mainScreenLayout.username.text = nameSurname
        binding.menu.usernameMenu.text = nameSurname
        binding.mainScreenLayout.pointsTextView.text = user.points.toString()
        binding.mainScreenLayout.nextRewardTextView.text = user.remainingPoints.toString()
        user.image?.let {
            if(it.trim().isNotEmpty()) {
                val bmp = decodeImageToBitmap(it)
                binding.menu.profilePictureLarge.setImageBitmap(bmp)
                binding.mainScreenLayout.profilePicture.setImageBitmap(bmp)
            }
        }
    }

    private fun decodeImageToBitmap(encodedImage: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun callLogout(userId: Long){
        val bitcardApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)

        bitcardApiV1.logout().enqueue( object : Callback<SimpleResponse>{
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                if(response.isSuccessful) {
                    val simpleResponse = response.body()
                    if(simpleResponse != null) {
                        when(simpleResponse.status_code){
                            SimpleResponse.STATUS_OK, SimpleResponse.STATUS_IGNORE -> {

                                /*
                                Delete user from database
                                 */
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.userDao().deleteWithId(
                                        id= userId
                                    )
                                }
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }
                            SimpleResponse.STATUS_ERROR -> {
                                Toast.makeText(applicationContext, R.string.logout_error, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Snackbar.make(binding.root, R.string.logout_error, Snackbar.LENGTH_SHORT).show()
            }

        })
    }

    private fun getToken(userId : Long){
        val bitcardApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)
        bitcardApiV1.getToken(userId).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if(response.isSuccessful){
                    val tokenResponse = response.body()
                    if(tokenResponse != null){
                        when(tokenResponse.status_code){
                            SimpleResponse.STATUS_OK, SimpleResponse.STATUS_IGNORE -> {
                                runOnUiThread {
                                    drawQr(binding.mainScreenLayout.cardImageView, tokenResponse.data.token)
                                    SharedPreferencesHelpers.write(applicationContext, SharedPreferencesHelpers.USER_DATA, "token_id", tokenResponse.data.id)
                                }
                            }
                            SimpleResponse.STATUS_ERROR -> {

                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
            }

        })
    }

    fun drawQr(imageView : ImageView, content: String){
        val bmp = QR.generate(content, getColor(R.color.primaryDarkColor), fetchPrimaryColor())
        imageView.setImageBitmap(bmp)
    }

    private fun fetchPrimaryColor(): Int {
        val a = TypedValue()
        theme.resolveAttribute(android.R.attr.windowBackground, a, true)
        val color : Int = if (a.isColorType) {
            // windowBackground is a color
            a.data
        }else{
            Color.MAGENTA
        }

        return color
    }

    private fun getUserId() : Long {
        return SharedPreferencesHelpers.readLong(applicationContext, SharedPreferencesHelpers.USER_DATA, "id")
    }

}