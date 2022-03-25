package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bitcard.adapters.ViewPagerAdapter
import com.example.bitcard.databinding.ActivityShopsBinding
import com.example.bitcard.fragments.ShopsListFragment
import com.example.bitcard.fragments.ShopsMapFragment
import com.google.android.material.tabs.TabLayoutMediator

class ShopsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
        viewPagerAdapter.addFragment(ShopsListFragment())
        viewPagerAdapter.addFragment(ShopsMapFragment())

        binding.viewPager.adapter = viewPagerAdapter


        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
            tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.ShopsListFragmentTitle)
                1 -> tab.text = getString(R.string.ShopsMapsFragmentTitle)
            }
        }.attach()
    }
}