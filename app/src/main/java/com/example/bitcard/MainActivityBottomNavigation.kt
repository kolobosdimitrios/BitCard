package com.example.bitcard

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.bitcard.databinding.ActivityMainBottomNavigationBinding

class MainActivityBottomNavigation : AppCompatActivity() {

private lateinit var binding: ActivityMainBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityMainBottomNavigationBinding.inflate(layoutInflater)
         setContentView(binding.root)

    }
}