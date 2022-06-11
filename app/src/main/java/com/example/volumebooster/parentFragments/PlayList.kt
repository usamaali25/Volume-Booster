package com.example.volumebooster.parentFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.volumebooster.R
import com.example.volumebooster.adapter.ViewPagerAdapter
import com.example.volumebooster.parentFragments.childFragments.Albums
import com.example.volumebooster.parentFragments.childFragments.Artists
import com.example.volumebooster.parentFragments.childFragments.Songs
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class PlayList : Fragment() {

    private lateinit var myFragment:View
    private lateinit var pager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        myFragment = inflater.inflate(R.layout.fragment_play_list, container, false)
        addFragment(myFragment)
        return myFragment
    }

    private fun addFragment(myFragment: View?) {
        pager = myFragment?.findViewById(R.id.view_pager) as ViewPager2
        tabLayout = myFragment.findViewById(R.id.tablelayout) as TabLayout
        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        with(viewPagerAdapter) {
            addFragment(Songs())
            addFragment(Artists())
            addFragment(Albums())
        }
        pager.adapter = viewPagerAdapter
        pager.isSaveEnabled = false
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            val titles = arrayOf("Songs", "Artist", "Albums")
            tab.text = titles[position]
        }.attach()

    }




}