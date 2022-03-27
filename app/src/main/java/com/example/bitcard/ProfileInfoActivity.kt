package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bitcard.databinding.ActivityProfileInfoBinding

class ProfileInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}