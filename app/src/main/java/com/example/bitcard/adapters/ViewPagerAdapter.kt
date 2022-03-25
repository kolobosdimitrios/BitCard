package com.example.bitcard.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle

) : FragmentStateAdapter(
    fragmentManager, lifecycle
){

    val fragments = ArrayList<Fragment>()


    override fun getItemCount(): Int {
        Log.i("View pager adapter", "Item count ${fragments.size}")
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment){
        fragments.add(fragment)
    }

}