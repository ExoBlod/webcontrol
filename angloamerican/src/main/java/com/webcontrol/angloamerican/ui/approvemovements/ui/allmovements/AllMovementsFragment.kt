package com.webcontrol.angloamerican.ui.approvemovements.ui.allmovements

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.angloamerican.data.model.Movement
import com.webcontrol.angloamerican.databinding.FragmentAllMovementsBinding
import com.webcontrol.angloamerican.ui.approvemovements.adapters.PendingMovementsAdapter
import com.webcontrol.angloamerican.ui.approvemovements.ui.ApproveMovementsActivity
import com.webcontrol.angloamerican.ui.approvemovements.ui.movementdetail.DialogDismissListener
import com.webcontrol.angloamerican.ui.approvemovements.ui.movementdetail.MovementDetailFragment
import com.webcontrol.angloamerican.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllMovementsFragment : BaseFragment<FragmentAllMovementsBinding, AllMovementsViewModel>(),
    DialogDismissListener {
    private val pendingMovementsAdapter by lazy {
        PendingMovementsAdapter(requireActivity(), listener)
    }

    override fun getViewModelClass() = AllMovementsViewModel::class.java

    override fun getViewBinding() = FragmentAllMovementsBinding.inflate(layoutInflater)

    override fun setUpViews() {
        super.setUpViews()

        binding.rvwPendingMovements.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pendingMovementsAdapter
        }
    }

    override fun observeData() {
        super.observeData()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allMovements.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    AllMovementsUIEvent.ShowLoading -> {
                        (requireActivity() as ApproveMovementsActivity).showLoading(true)
                    }
                    AllMovementsUIEvent.HideLoading -> {
                        (requireActivity() as ApproveMovementsActivity).showLoading(false)
                    }
                    AllMovementsUIEvent.Error -> {}
                    is AllMovementsUIEvent.Success -> {
                        if (event.allMovements.isNotEmpty()) {
                            pendingMovementsAdapter.movements = event.allMovements
                        } else {
                            pendingMovementsAdapter.movements = emptyList()
                            Toast.makeText(
                                requireContext(),
                                "No se encontraron movimientos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private val listener = object: PendingMovementsAdapter.PendingMovementsListener{
        override fun onClickItem(movement: Movement) {
            val fragment = MovementDetailFragment.newInstance(movement.batchId.toString(), activity!!)
            fragment.setDismissListener(this@AllMovementsFragment)
            fragment.show(childFragmentManager, "Tag")
        }
    }

    override fun onDialogDismissed() {
        viewModel.getAllMovements()
    }
}