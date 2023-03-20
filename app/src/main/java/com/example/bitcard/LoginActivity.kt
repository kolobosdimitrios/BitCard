package com.example.bitcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bitcard.databinding.ActivityLoginBinding
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.retrofit.api.BitcardApiV1
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val bitcardApiV1 = RetrofitHelper.getRetrofitInstance().create(BitcardApiV1::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        FirebaseApp.initializeApp(applicationContext)
        auth = FirebaseAuth.getInstance()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.loginBtn.setOnClickListener { onLoginButtonClick() }
    }



    private fun onLoginButtonClick() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        if (checkValues(email = email, password = password)) {
            loginUser(email = email, password = password)
        }

    }


    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                Log.i("Login action", "Started")
                if (result.isSuccessful) {
                    Log.i("User login", "successful")
                    result.result.user?.uid?.let { user ->
                        Log.i("User ID", user)
                        runOnUiThread {
                            bitcardApiV1.login(user).enqueue(object : Callback<GetUserResponse>{
                                override fun onResponse(
                                    call: Call<GetUserResponse>,
                                    response: Response<GetUserResponse>
                                ) {
                                    if(response.body() != null && response.isSuccessful){
                                        if(response.body()!!.status_code == SimpleResponse.STATUS_OK){
                                            Log.i("user", response.body()!!.data.toString())
                                            SharedPreferencesHelpers.create(applicationContext, SharedPreferencesHelpers.USER_DATA)
                                            val id = response.body()!!.data.id
                                            SharedPreferencesHelpers.write(applicationContext, SharedPreferencesHelpers.USER_DATA, "id", id)
                                            if(SharedPreferencesHelpers.readBoolean(applicationContext, SharedPreferencesHelpers.USER_CREDENTIALS_NAME, "remember_me")) {
                                                SharedPreferencesHelpers.write(
                                                    applicationContext,
                                                    SharedPreferencesHelpers.USER_CREDENTIALS_NAME,
                                                    key = "email",
                                                    value = email
                                                )
                                                SharedPreferencesHelpers.write(
                                                    applicationContext,
                                                    SharedPreferencesHelpers.USER_CREDENTIALS_NAME,
                                                    key = "password",
                                                    value = password
                                                )
                                            }
                                            synchronized(this){
                                                runOnUiThread{
                                                    val intent = Intent(applicationContext, MainScreenActivity::class.java)
                                                    startActivity(intent)
                                                }
                                            }
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                                    TODO("Not yet implemented")
                                }

                            })

                        }
                    }
                } else if (result.isCanceled) {
                    Log.e("Login action", "Cancelled")
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun checkValues(email: String?, password: String?): Boolean {
        return when {
            email.isNullOrBlank() -> {
                binding.email.error = getString(R.string.email_empty)
                false
            }
            password.isNullOrBlank() -> {
                binding.password.error = getString(R.string.password_empty)
                false
            }
            else -> true
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.rememberMe -> {
                    SharedPreferencesHelpers.write(applicationContext, SharedPreferencesHelpers.USER_CREDENTIALS_NAME, "remember_me", value = checked)
                }
            }
        }
    }

}