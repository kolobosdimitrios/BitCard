package com.example.bitcard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bitcard.databinding.ActivityProfileInfoBinding
import com.example.bitcard.network.daos.requests.UserIdModel
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.retrofit.api.UsersApi
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileInfoBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            result -> handleResult(result)
        }
        super.onCreate(savedInstanceState)
        binding = ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser != null){
            getUserData(firebaseUser.uid)
        }

        binding.changePasswordTextview.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            launcher.launch(intent)
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getUserData(userId: String){

        val userIdModel = UserIdModel(userId)

        val usersApi = RetrofitHelper.getRetrofitInstance().create(UsersApi::class.java)

        usersApi.get(userIdModel.userId).enqueue(object : Callback<GetUserResponse> {

            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                if (response.isSuccessful) {
                    val simpleResponse = response.body()
                    simpleResponse.let {
                        if (it != null) {
                            if (it.status_code == SimpleResponse.STATUS_OK.toLong()) {
                                //render
                                binding.emailTextView.text = it.data.email
                                binding.streetAddressTextView.text = it.data.address
                                binding.birthdayTextView.text = it.data.dateOfBirth
                                binding.fullnameTextView.text = it.data.name + " " +it.data.surname
                                binding.usernameTextView.text = it.data.username
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                TODO("Handle errors in http call")
            }
        })
    }

    private fun handleResult(result: ActivityResult?){
        if(result != null){
            when (result.resultCode) {
                ChangePasswordActivity.RESULT_PASSWORD_CHANGED -> {
                    Toast.makeText(this, "Password changed!", Toast.LENGTH_SHORT).show()
                }
                ChangePasswordActivity.RESULT_PASSWORD_NOT_CHANGED -> {
                    Toast.makeText(this, "Password not changed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}