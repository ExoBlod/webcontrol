package com.webcontrol.angloamerican.ui.approvemovements.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.webcontrol.angloamerican.ui.approvemovements.ui.allmovements.AllMovementsFragment
import com.webcontrol.angloamerican.ui.approvemovements.ui.ApproveMovementsFragment.Companion.ALL_MOVEMENTS
import com.webcontrol.angloamerican.ui.approvemovements.ui.ApproveMovementsFragment.Companion.NUM_TABS

class ApproveMovementsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = NUM_TABS

    override fun createFragment(position: Int): Fragment {
        return when(position){
            ALL_MOVEMENTS -> AllMovementsFragment()
            else -> throw IllegalArgumentException("Fragment at position: $position is not valid")
        }
    }
}