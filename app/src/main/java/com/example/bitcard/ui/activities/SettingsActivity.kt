package com.example.bitcard.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bitcard.R
import com.example.bitcard.databinding.ActivitySettingsBinding
import com.example.bitcard.db.database.MainDatabase
import com.example.bitcard.SharedPreferencesHelpers
import com.example.bitcard.network.data.responses.SimpleResponse
import com.example.bitcard.network.retrofit.client.APIClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val userDao by lazy { MainDatabase.getInstance(this).userDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar =  binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.changeThemeBtn.setOnClickListener {
            val intent = Intent(applicationContext, SelectThemeActivity::class.java)
            startActivity(intent)
        }

        binding.logoutOption.setOnClickListener {
            callLogout(
                SharedPreferencesHelpers.readLong(
                    applicationContext,
                    SharedPreferencesHelpers.USER_DATA, "id"
                )
            )
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callLogout(userId: Long){

        APIClient.newInstance.logout().enqueue( object : Callback<SimpleResponse<Any>> {
            override fun onResponse(
                call: Call<SimpleResponse<Any>>,
                response: Response<SimpleResponse<Any>>
            ) {
                if(response.isSuccessful) {
                    val simpleResponse = response.body()
                    if(simpleResponse != null) {
                        when(simpleResponse.status_code){
                            SimpleResponse.STATUS_OK, SimpleResponse.STATUS_IGNORE -> {
                                Toast.makeText(applicationContext, R.string.logout_success, Snackbar.LENGTH_SHORT).show()
                                SharedPreferencesHelpers.clear(applicationContext, SharedPreferencesHelpers.USER_CREDENTIALS_NAME) //remove credentials from shared preferences
                                SharedPreferencesHelpers.clear(applicationContext, SharedPreferencesHelpers.USER_DATA) //remove user_data
                                /*
                                Delete user from database
                                 */
                                CoroutineScope(Dispatchers.IO).launch {
                                    userDao.deleteWithId(
                                        id= userId
                                    )
                                }
                                runOnUiThread {
                                    finishAffinity()
                                    startActivity(Intent(applicationContext, StartActivity::class.java))
                                }
                            }
                            SimpleResponse.STATUS_ERROR -> {
                                Toast.makeText(applicationContext, R.string.logout_error, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SimpleResponse<Any>>, t: Throwable) {
                Snackbar.make(binding.root, R.string.logout_error, Snackbar.LENGTH_SHORT).show()
            }

        })
    }
}