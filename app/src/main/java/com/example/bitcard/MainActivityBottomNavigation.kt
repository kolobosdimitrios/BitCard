package com.example.bitcard

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.bitcard.databinding.ActivityMainBottomNavigationBinding
import com.example.bitcard.ui.home.HomeFragment
import com.example.bitcard.ui.home.HomeFragmentViewModel
import com.example.bitcard.ui.purchases.PurchasesFragment
import com.example.bitcard.ui.settings.SettingsFragment
import com.example.bitcard.ui.shops.ShopsFragment

class MainActivityBottomNavigation : AppCompatActivity() {

private lateinit var binding: ActivityMainBottomNavigationBinding

    private val homeFragmentViewModel : HomeFragmentViewModel by viewModels() {defaultViewModelProviderFactory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityMainBottomNavigationBinding.inflate(layoutInflater)
         setContentView(binding.root)

        val homeFragment = HomeFragment()
        val shopsFragment = ShopsFragment()
        val purchasesFragment = PurchasesFragment()
        val settingsFragment = SettingsFragment()

    }
}