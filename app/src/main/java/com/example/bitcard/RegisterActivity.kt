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
        binding.registerBtn.setOnClickListener { registerUser() }

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
                            address = binding.address.text.toString()
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
                            if(it.status_code == SimpleResponse.STATUS_OK.toLong()){
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