package com.example.bitcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bitcard.databinding.ActivityMainBinding
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        if(SharedPreferencesHelpers.readBoolean(applicationContext, SharedPreferencesHelpers.USER_CREDENTIALS_NAME, "remember_me")) {
            val email = SharedPreferencesHelpers.readString(applicationContext, SharedPreferencesHelpers.USER_CREDENTIALS_NAME, "email")
            val password = SharedPreferencesHelpers.readString(applicationContext, SharedPreferencesHelpers.USER_CREDENTIALS_NAME, "password")
            if (email != null && password != null) {
                loginUser(email, password)
            }
        }
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createAccountButton.setOnClickListener {
            this.startActivity(Intent(applicationContext, RegisterActivity::class.java))
            finish()
        }

        binding.loginWithAccountButton.setOnClickListener{
            this.startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

        val sharedPrefs = getSharedPreferences("app_settings", MODE_PRIVATE)


        when (sharedPrefs.getString("theme", "system")) {
            "dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            "light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            "system" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun loginUser(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                Log.i("Login action", "Started")
                if (result.isSuccessful) {
                    Log.i("User login", "successful")
                    result.result.user?.uid?.let { Log.i("User ID", it) }
                    val intent = Intent(applicationContext, MainScreenActivity::class.java)
                    startActivity(intent)
                    this.finish()
                } else if (result.isCanceled) {
                    Log.e("Login action", "Cancelled")
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }
    }
}