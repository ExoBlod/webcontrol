package com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMineDetail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.FragmentPreaccessMinePassengersBinding
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PreAccessMineDetailFragment :
    BaseFragment<FragmentPreaccessMinePassengersBinding, PreAccessMineDetailViewModel>() {

    override fun getViewModelClass() = PreAccessMineDetailViewModel::class.java
    override fun getViewBinding() = FragmentPreaccessMinePassengersBinding.inflate(layoutInflater)

    private var idWorker: String? = ""
    override fun setUpViews() {
        super.setUpViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHistory(idWorker!!)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listHistory.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    PreAccessMineDetailUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo historico")
                    }

                    PreAccessMineDetailUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }

                    PreAccessMineDetailUIEvent.Error -> {

                    }

                    is PreAccessMineDetailUIEvent.Success -> {
                        if (event.listHistory.isNotEmpty()) {
                        } else {
                        }
                    }

                    is PreAccessMineDetailUIEvent.SuccessSearchByCheckingHead -> {
                    }
                }
            }
        }
    }

    private fun onShowSomethingButtonClick() {
        //!Create a new reservation
        findNavController().navigate(
            R.id.action_historyBookFragment_to_newBookCourses
        )
    }

    private fun getBookedFragment() {
        findNavController().navigate(
            R.id.action_historyBookFragment_to_bookedCourses
        )
    }
}