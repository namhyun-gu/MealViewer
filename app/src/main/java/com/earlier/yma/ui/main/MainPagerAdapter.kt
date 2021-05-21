package com.earlier.yma.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.earlier.yma.util.Constants

class MainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val types = arrayOf(
        Constants.TYPE_LUNCH,
        Constants.TYPE_DINNER
    )

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return MealFragment.newInstance(types[position])
    }
}