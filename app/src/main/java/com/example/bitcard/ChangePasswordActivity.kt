package com.example.bitcard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bitcard.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    companion object{
        val RESULT_PASSWORD_CHANGED = 1000
        val RESULT_PASSWORD_NOT_CHANGED = 1001
    }

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        binding.proceedBtn.setOnClickListener {
            val oldPassword = binding.password.text.toString()
            if(oldPassword.isBlank()){
                binding.password.error = getString(R.string.password_cannot_be_empty)
            }

            if(firebaseUser != null) {
                val email = firebaseUser.email
                if(!email.isNullOrBlank()) {
                    val authCredential =
                        EmailAuthProvider.getCredential(email, oldPassword)
                    firebaseUser.reauthenticate(authCredential)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                val newPassword = binding.newPassword.text.toString()
                                if(newPassword.isNotBlank() && oldPassword != newPassword)
                                    firebaseUser.updatePassword(newPassword).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                           Log.i("Password", "Updated")
                                            returnWithResult(RESULT_PASSWORD_CHANGED)
                                        } else {
                                            Log.d("Password", "Cannot be updated!")
                                            returnWithResult(RESULT_PASSWORD_NOT_CHANGED)
                                        }
                                    }
                            }
                        }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun returnWithResult(result: Int){
        setResult(result)
        finish()
    }
}