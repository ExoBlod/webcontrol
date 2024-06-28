package com.webcontrol.angloamerican.ui.approvemovements.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.ui.approvemovements.adapters.ApproveMovementsPagerAdapter

class ApproveMovementsFragment : Fragment() {

    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_approve_movements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)
        val pagerAdapter = ApproveMovementsPagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            tab.text = when(position){
                ALL_MOVEMENTS -> ALL_PASSES
                else -> throw IllegalArgumentException(getString(R.string.invalid_tab_position, position.toString()))
            }
        }.attach()
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        const val NUM_TABS = 1
        const val ALL_MOVEMENTS = 0
        const val ALL_PASSES = "Listado de Pases"
        fun newInstance() = ApproveMovementsFragment()
    }
}