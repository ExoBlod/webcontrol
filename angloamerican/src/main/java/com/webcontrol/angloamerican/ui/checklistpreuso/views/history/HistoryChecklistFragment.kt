package com.webcontrol.angloamerican.ui.checklistpreuso.views.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.FragmentPreUsoInspectionHistoryBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoActivity
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoViewModel
import com.webcontrol.angloamerican.ui.checklistpreuso.adapters.HistoryCheckListPreUsoAdapter
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.AppDataBase
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryResponse
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pre_uso_inspection_history.*
import kotlinx.android.synthetic.main.fragment_pre_uso_test_checklist_vehicular_inspection.*
import kotlinx.android.synthetic.main.fragment_test_booking_course.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class HistoryChecklistFragment : BaseFragment<FragmentPreUsoInspectionHistoryBinding, HistoryChecklistViewModel>(),
    HistoryCheckListPreUsoAdapter.OnButtonClickListener{

    private val parentViewModel: CheckListPreUsoViewModel by activityViewModels()

    private val db: AppDataBase by inject()

    private val adapter: HistoryCheckListPreUsoAdapter by lazy {
        HistoryCheckListPreUsoAdapter(listOf(), this@HistoryChecklistFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Credencial"
    }


    override fun getViewModelClass() = HistoryChecklistViewModel::class.java

    override fun getViewBinding() = FragmentPreUsoInspectionHistoryBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(parentViewModel.isSearching){
            binding.btnNewInspection.isEnabled = false
        }

        binding.btnNewInspection.setOnClickListener {
            parentViewModel.setIsConsulting(false)
            parentViewModel.setCleanHistory(true)
            findNavController().navigate(R.id.action_historyPreUsoFragment_to_swornDeclarationPreUsoFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (parentViewModel.isSearching) {
                    findNavController().navigate(R.id.action_historyPreUsoFragment_to_consultInspectionFragment)
                } else {
                    activity?.finish()
                }
            }
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.rcvInspectionHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvInspectionHistory.adapter = adapter

        if (!SharedUtils.isOnline(requireContext())){
            showHistoryList(db.checkListPreUso().getHistory(SharedUtils.getUsuarioId(context)).map { it.toMapper() })
        }
        else if(parentViewModel.isSearching){
            showHistoryList(parentViewModel.uiHistoryList)
        } else{
            viewModel.getHistory(SharedUtils.getUsuarioId(context))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listHistory.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    HistoryCheckListUIEvent.ShowLoading -> {
                        //SharedUtils.showLoader(requireContext(),"Cargando")
                        (requireActivity() as CheckListPreUsoActivity).showLoading(true, "Cargando Historial")
                    }
                    HistoryCheckListUIEvent.HideLoading -> {
                        (requireActivity() as CheckListPreUsoActivity).showLoading(false)
                        //SharedUtils.dismissLoader(requireContext())
                    }
                    HistoryCheckListUIEvent.Error -> {}
                    is HistoryCheckListUIEvent.ErrorString -> {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${event.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("ERROR", "ERROR HISTORIAL: ${event.message}")
                    }
                    is HistoryCheckListUIEvent.Success  -> {
                        if(event.listHistory.isNotEmpty()){
                            if (parentViewModel.uiHistoryList != event.listHistory){
                                parentViewModel.setHistoryList(event.listHistory)
                                showHistoryList(event.listHistory)
                                parentViewModel.setIsConsulting(true)
                                db.checkListPreUso().insertHistory(event.listHistory.map { it.toMapper() })
                            } else {
                                showHistoryList(event.listHistory)
                            }
                        }
                        else{
                            showEmptyHistory()
                        }
                    }
                    is HistoryCheckListUIEvent.SuccessSearchByCheckingHeadId  -> {
                        parentViewModel.setListQuestions(event.questionListResponse)
                        parentViewModel.setIsConsulting(true)
                        findNavController().navigate(R.id.action_historyPreUsoFragment_to_listCheckListPreUsoFragment)
                    }
                }
            }
        }
    }

    private fun showEmptyHistory() = with(binding) {
        animationInspectionHistory.visibility = View.VISIBLE
        tvNoRegister.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistoryList(listHistory: List<HistoryResponse>) = with(binding) {
        animationInspectionHistory.visibility = View.GONE
        tvNoRegister.visibility = View.GONE
        adapter.setList(listHistory)
    }

    override fun onButtonClick(data: HistoryResponse) {
        Log.d(TAG, "Item selected ${data.checkingInHead}")
        viewModel.searchbyChechingHead(data.checkingInHead)
        parentViewModel.setCheckingHead(data.checkingInHead)
        parentViewModel.setCheckListStatus(data.checklistStatus)
    }

    companion object{
        val TAG = HistoryChecklistFragment::class.java.simpleName
    }
}