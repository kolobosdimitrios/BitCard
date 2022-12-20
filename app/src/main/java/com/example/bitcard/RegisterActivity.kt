package com.example.bitcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitcard.databinding.ActivityRegisterBinding
import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.requests.UserModel
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.retrofit.api.UsersApi
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnFocusChangeListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        binding.date.onFocusChangeListener = this
        binding.registerBtn.setOnClickListener {
            if(valuesCorrect()) registerUser()
        }

    }

    private fun valuesCorrect(): Boolean{
        val username = binding.username.text.toString().trim()
        val password =  binding.password.text.toString().trim()
        val passwordConfirmation = binding.verifyPassword.text.toString().trim()
        val name = binding.name.text.toString().trim()
        val surname = binding.surname.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val dateOfBirth = binding.date.text.toString().trim()
        val address = binding.address.text.toString().trim()

        if(name.isEmpty()) { binding.name.error = getString(R.string.name_cannot_be_empty); return false;}
        if(surname.isEmpty()) { binding.surname.error = getString(R.string.surname_cannot_be_empty); return false;}
        if(email.isEmpty()) { binding.username.error = getString(R.string.email_cannot_be_empty); return false;}
        if(username.isEmpty()) { binding.username.error = getString(R.string.username_cannot_be_empty); return false;}
        if(password.isEmpty()) { binding.password.error = getString(R.string.password_cannot_be_empty); return false; }
        if(passwordConfirmation.isEmpty()) { binding.verifyPassword.error = getString(R.string.password_cannot_be_empty); return false;}
        if(dateOfBirth.isEmpty()) { binding.date.error = getString(R.string.date_of_birth_cannot_be_empty); return false;}
        if(address.isEmpty()) { binding.address.error = getString(R.string.address_cannot_be_empty); return false;}

        if(password.length <= 6){
            binding.password.error = getString(R.string.password_must_have_6_plus_digits)
            binding.password.text.clear()
            binding.verifyPassword.text.clear()
            return false
        }

        if(password != passwordConfirmation){
            binding.password.text.clear()
            binding.verifyPassword.text.clear()
            binding.verifyPassword.error = getString(R.string.wrong_password_confirmation)
            return false
        }
        return true

    }

    private fun registerUser(){

        auth.createUserWithEmailAndPassword(binding.email.text.toString().trim(), binding.password.text.toString().trim())
            .addOnCompleteListener { result ->
                Log.i("Register action", "Started")
                if (result.isSuccessful) {
                    Log.i("User register", "successful")
                    result.result.user?.uid?.let {
                        Log.i("User ID", it)
                        val registerUserModel = UserModel(
                            name = binding.name.text.toString(),
                            surname = binding.surname.text.toString(),
                            username = binding.username.text.toString(),
                            email = binding.email.text.toString(),
                            userId = it,
                            dateOfBirth = binding.date.text.toString(),
                            address = binding.address.text.toString(),
                            id = null
                        )

                        sendCreateUserRequest(registerUserModel)
                    }


                } else if (result.isCanceled) {
                    Log.e("Register action", "Cancelled")
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }


    }

    private fun sendCreateUserRequest(user: UserModel){
        val register = RegisterModel(user)

        val usersApi = RetrofitHelper.getRetrofitInstance().create(UsersApi::class.java)

        usersApi.register(register).enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                if(response.isSuccessful) {
                    val simpleResponse = response.body()
                    simpleResponse.let {
                        if(it != null){
                            if(it.status_code == SimpleResponse.STATUS_OK){
                                //TODO : Successful registration in backend to!
                                val intent = Intent(applicationContext, MainScreenActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }

                Toast.makeText(applicationContext, "User created", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "User not created", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun selectDate(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date_dialog_header))
                .build()

        datePicker.addOnPositiveButtonClickListener {
            //TODO get timestamp


            binding.date.setText(getDateStringFormatted(it))
        }
        datePicker.show(supportFragmentManager, null)
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        if(p1){ //has focus
            selectDate()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun getDateStringFormatted(millis: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return simpleDateFormat.format(millis)
    }
}