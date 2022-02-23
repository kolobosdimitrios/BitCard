package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bitcard.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_BitCard)
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
        Toast.makeText(this, "Not working", Toast.LENGTH_LONG).show()

    }


}