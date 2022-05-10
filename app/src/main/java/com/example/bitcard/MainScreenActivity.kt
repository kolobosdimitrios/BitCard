package com.example.bitcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.bitcard.databinding.ActivityMainScreenWNavDrawerBinding
import com.example.bitcard.databinding.MainScreenMenuBinding
import com.google.firebase.auth.FirebaseAuth

class MainScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreenWNavDrawerBinding
    private lateinit var menuBind : MainScreenMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenWNavDrawerBinding.inflate(layoutInflater)
        menuBind = MainScreenMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerToggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
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

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser == null){
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }else{
            binding.mainScreenLayout.username.text = firebaseUser.email
        }

    }
}