package com.example.bitcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.bitcard.databinding.ActivityMainScreenWNavDrawerBinding
import com.example.bitcard.databinding.MainScreenMenuBinding
import com.example.bitcard.globals.SharedPreferencesHelpers
import com.example.bitcard.network.daos.requests.UserIdModel
import com.example.bitcard.network.daos.requests.UserModel
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.retrofit.api.UsersApi
import com.example.bitcard.network.retrofit.client.RetrofitHelper
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreenWNavDrawerBinding
    private lateinit var menuBind : MainScreenMenuBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenWNavDrawerBinding.inflate(layoutInflater)
        menuBind = MainScreenMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.auth = FirebaseAuth.getInstance()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.mainScreenLayout.mainScreenToolbar,
            R.string.open,
            R.string.close
        )

        binding.drawerLayout.addDrawerListener(drawerToggle)

        drawerToggle.syncState()


        binding.menu.purchaseHistoryOption.setOnClickListener{
            startActivity(Intent(this, PurchaseHistoryActivity::class.java))
        }

        binding.menu.settingsOption.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.menu.shopListOption.setOnClickListener {
            startActivity(Intent(this, ShopsActivity::class.java))
        }

        binding.mainScreenLayout.profilePicture.setOnClickListener {
            startActivity(Intent(this, ProfileInfoActivity::class.java))
        }

        binding.menu.logoutOption.setOnClickListener {
            auth.signOut()
            SharedPreferencesHelpers.clear(applicationContext, SharedPreferencesHelpers.USER_CREDENTIALS_NAME) //remove credentials from shared preferences
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        firebaseUser?.let {
            getUserData(firebaseUser.uid)
        }


    }

    private fun getUserData(userId: String){

        val userIdModel = UserIdModel(userId)

        val usersApi = RetrofitHelper.getRetrofitInstance().create(UsersApi::class.java)

        usersApi.get(userIdModel.userId).enqueue(object : Callback<GetUserResponse>{

            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                if(response.isSuccessful) {
                    val simpleResponse = response.body()
                    simpleResponse.let {
                        if(it != null){
                            if(it.status_code == SimpleResponse.STATUS_OK.toLong()){
                                //render
                                renderLayoutWithUserData(it.data)

                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.e("CALL FAILED", call.toString())
            }
        })

    }

    private fun renderLayoutWithUserData(user: UserModel){

        Log.e("user data", user.toString())

        val nameSurname = String.format(resources.getString(R.string.space_between), user.name, user.surname)

        binding.mainScreenLayout.username.text = nameSurname
        binding.menu.usernameMenu.text = nameSurname

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}