package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.bitcard.databinding.ActivityMainScreenBinding
import com.example.bitcard.databinding.ActivityMainScreenWNavDrawerBinding

class MainScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreenWNavDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenWNavDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerToggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.mainScreenLayout.mainScreenToolbar,
            R.string.open,
            R.string.close
        )

        binding.drawerLayout.addDrawerListener(drawerToggle)

        drawerToggle.syncState()




    }
}