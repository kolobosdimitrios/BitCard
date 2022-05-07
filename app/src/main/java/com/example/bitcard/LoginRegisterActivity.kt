package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bitcard.databinding.ActivityLoginRegisterBinding

class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        binding.loginBtn.setOnClickListener { onLoginButtonClick() }
    }

    private fun onLoginButtonClick(){
        val username = binding.username.text.toString().trim()
        val password = binding.password.text.toString().trim()

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return true
    }



}