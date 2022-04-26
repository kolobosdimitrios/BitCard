package com.example.bitcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bitcard.databinding.ActivityRegisterBinding
import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.requests.UserModel
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.retrofit.api.UsersApi
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), View.OnFocusChangeListener {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.date.onFocusChangeListener = this
        binding.registerBtn.setOnClickListener { registerUser() }

    }

    private fun registerUser(){
        val registerUserModel = UserModel(
            name = binding.name.text.toString(),
            surname = binding.surname.text.toString(),
            username = binding.username.text.toString(),
            email = binding.email.text.toString(),
            password = binding.password.text.toString(),
            passwordConfirm = binding.verifyPassword.text.toString(),
            dateOfBirth = binding.date.text.toString(),
            address = binding.address.text.toString()
        )

        val register = RegisterModel(registerUserModel)

        val usersApi = RetrofitHelper.getRetrofitInstance().create(UsersApi::class.java)

        usersApi.register(register).enqueue(object: Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.i("Request", call.request().body().toString())
                Log.i("response", response.toString())
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {

                Log.e("request", call.request().toString())
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

            binding.date.setText(datePicker.headerText)
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
        startActivity(Intent(this, StartupActivity::class.java))
        finish()
    }
}