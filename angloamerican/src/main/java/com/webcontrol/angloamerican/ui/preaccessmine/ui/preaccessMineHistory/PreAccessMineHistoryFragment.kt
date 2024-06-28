package com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMineHistory

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.databinding.FragmentPreaccessMineHistoryBinding
import com.webcontrol.angloamerican.ui.preaccessmine.adapters.PreAccesoMineAdapter
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreAccessMineHistoryFragment :
    BaseFragment<FragmentPreaccessMineHistoryBinding, PreAccessMineHistoryViewModel>(),
    PreAccesoMineAdapter.OnButtonClickListener {

    override fun getViewModelClass() = PreAccessMineHistoryViewModel::class.java
    override fun getViewBinding() = FragmentPreaccessMineHistoryBinding.inflate(layoutInflater)

    private val adapter: PreAccesoMineAdapter by lazy {
        PreAccesoMineAdapter(listOf(), this@PreAccessMineHistoryFragment)
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.rvPreaccessList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPreaccessList.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.postReservationPreaccessMine()
        viewModel.postReservationPreaccessDetailMine()
        viewModel.getHistory()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listHistory.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    PreAccessMineHistoryUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo historico")
                    }

                    PreAccessMineHistoryUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }

                    is PreAccessMineHistoryUIEvent.Error -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }

                    is PreAccessMineHistoryUIEvent.Success -> {
                        if (event.listHistory.isNotEmpty()) {
                            showHistoryList(event.listHistory)
                            binding.txtNumeroRegistros.text = event.listHistory.size.toString()
                        } else {
                            showEmptyHistory()
                        }
                    }

                    is PreAccessMineHistoryUIEvent.SuccessSearchByCheckingHead -> {
                    }
                }
            }
        }
        navigateToNewPreAccess()
    }

    private fun navigateToNewPreAccess() {
        binding.btnNuevo.setOnClickListener() {
            findNavController().navigate(
                R.id.action_historyPreaccesMine_to_newPreAccess
            )
        }
    }

    private fun showHistoryList(listHistory: List<PreaccesoMina>) = with(binding) {
        binding.emptyState.visibility = View.GONE
        binding.rvPreaccessList.visibility = View.VISIBLE
        adapter.setList(listHistory)
    }

    private fun showBookHistoryList(listHistory: List<PreaccesoMina>) = with(binding) {
    }

    private fun showEmptyHistory() = with(binding) {
    }

    override fun onButtonClick(data: PreaccesoMina) {
        TODO("Not yet implemented")
    }
}