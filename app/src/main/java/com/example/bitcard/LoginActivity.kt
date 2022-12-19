package com.example.bitcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.example.bitcard.databinding.ActivityLoginBinding
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

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
                    result.result.user?.uid?.let { Log.i("User ID", it) }
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