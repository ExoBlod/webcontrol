package com.webcontrol.angloamerican.ui.approvemovements.ui.pendingmovements

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.dto.Movement
import com.webcontrol.angloamerican.databinding.FragmentPendingMovementsBinding
import com.webcontrol.angloamerican.ui.approvemovements.adapters.PendingMovementsAdapter
import com.webcontrol.angloamerican.ui.approvemovements.ui.movementdetail.MovementDetailFragment
import com.webcontrol.angloamerican.utils.BaseFragment

class PendingMovementsFragment : BaseFragment<FragmentPendingMovementsBinding, PendingMovementsViewModel>() {
    override fun getViewModelClass() = PendingMovementsViewModel::class.java

    override fun getViewBinding() = FragmentPendingMovementsBinding.inflate(layoutInflater)

    override fun setUpViews() {
        super.setUpViews()

        binding.rvwPendingMovements.apply {
            layoutManager = LinearLayoutManager(context)
        }
    }
}