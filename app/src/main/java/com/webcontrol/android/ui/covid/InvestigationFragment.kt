package com.webcontrol.android.ui.covid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.webcontrol.android.databinding.FragmentInvestigationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvestigationFragment : Fragment() {
    private lateinit var binding: FragmentInvestigationBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentInvestigationBinding.inflate(inflater, container, false)
        return binding.root
    }

}
