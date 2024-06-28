package com.webcontrol.android.ui.covid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.webcontrol.android.databinding.FragmentFollowUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowUpFragment : Fragment() {
    private lateinit var binding: FragmentFollowUpBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowUpBinding.inflate(inflater, container, false)
        return binding.root
    }

}
