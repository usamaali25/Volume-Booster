package com.example.volumebooster.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(supportFragmentManager: FragmentManager?, lifecycle: Lifecycle?) : FragmentStateAdapter(supportFragmentManager!!, lifecycle!!) {
    private val fragmentArrayList = ArrayList<Fragment>()
    override fun createFragment(position: Int): Fragment {
        return fragmentArrayList[position]
    }

    override fun getItemCount(): Int {
        return fragmentArrayList.size
    }

    fun addFragment(fragment: Fragment) {
        fragmentArrayList.add(fragment)
    }
}