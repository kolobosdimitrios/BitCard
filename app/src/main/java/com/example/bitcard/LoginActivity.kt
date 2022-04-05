package com.example.bitcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bitcard.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.loginBtn.setOnClickListener { onLoginButtonClick() }
    }

    private fun onLoginButtonClick(){
        val username = binding.username.text.toString().trim()
        binding.username.text.clear()
        val password = binding.password.text.toString().trim()
        binding.password.text.clear()
        startActivity(Intent(this, MainScreenActivity::class.java))

    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, StartupActivity::class.java))
        finish()
    }



}