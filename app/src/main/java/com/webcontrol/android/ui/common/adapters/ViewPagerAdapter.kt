package com.webcontrol.android.ui.common.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.webcontrol.android.ui.covid.initialdata.*

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                PersonalesFragment()
            }
            1 -> {
                ContactoFragment()
            }
            2 -> {
                ResidenciaFragment()
            }
            3 -> {
                LaboralFragment()
            }
            else -> {
                AntecedentesFragment()
            }
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}