package com.example.bitcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.bitcard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BitCard)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding.createAccountButton.setOnClickListener {
            this.startActivity(Intent(applicationContext, RegisterActivity::class.java))
            finish()
        }

        binding.loginWithAccountButton.setOnClickListener{
            this.startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }
}