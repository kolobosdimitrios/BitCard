package com.example.bitcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.bitcard.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onStart() {
        super.onStart()
        val firebaseUser = auth.currentUser
        updateUi(firebaseUser)
    }

    private fun onLoginButtonClick() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        if (checkValues(email = email, password = password)) {
            loginUser(email = email, password = password)
        } else {
            Toast.makeText(this, "fail", Toast.LENGTH_LONG).show()
        }

    }


    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                Log.i("Login action", "Started")
                if (result.isSuccessful) {
                    Log.i("User login", "successful")
                    result.result.user?.uid?.let { Log.i("User ID", it) }
                    val intent = Intent(applicationContext, MainScreenActivity::class.java)
                    startActivity(intent)
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
            password.length < 6 ->{
                binding.password.error = getString(R.string.password_must_have_6_plus_digits)
                false
            }
            else -> true
        }


    }

    private fun setError(@StringRes value: Int, editText: EditText) {


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

    private fun updateUi(user: FirebaseUser?) {

    }


}