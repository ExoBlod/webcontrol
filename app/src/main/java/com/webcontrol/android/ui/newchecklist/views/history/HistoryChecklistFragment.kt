package com.webcontrol.android.ui.newchecklist.views.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentInspectionHistoryBinding
import com.webcontrol.android.ui.newchecklist.NewCheckListViewModel
import com.webcontrol.android.ui.newchecklist.adapters.HistoryCheckListAdapter
import com.webcontrol.android.ui.newchecklist.data.*
import com.webcontrol.android.ui.newchecklist.views.listchecklist.ListChecklistViewModel
import com.webcontrol.android.util.BaseFragment
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryChecklistFragment : BaseFragment<FragmentInspectionHistoryBinding,HistoryChecklistViewModel>(),HistoryCheckListAdapter.OnButtonClickListener {
    private val parentViewModel: NewCheckListViewModel by activityViewModels()
    private val questionModel : ListChecklistViewModel by activityViewModels()
    private val adapter: HistoryCheckListAdapter by lazy {
        HistoryCheckListAdapter(listOf(), this@HistoryChecklistFragment)
    }
    private var conHandler: Handler? = null
    private var checkConnection = object : Runnable {
        override fun run() {
            if (SharedUtils.isOnline(requireContext())){
                questionModel.savingAnswer(parentViewModel.uiListGroup)
            }
            conHandler!!.postDelayed(this, 600000)
        }
    }
    override fun onResume() {
        super.onResume()
        conHandler!!.post(checkConnection)
    }
    override fun onPause() {
        super.onPause()
        conHandler!!.removeCallbacks(checkConnection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Credencial"
    }


    override fun getViewModelClass() = HistoryChecklistViewModel::class.java

    override fun getViewBinding() = FragmentInspectionHistoryBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState:
    Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        conHandler = Handler()
        binding.rcvInspectionHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvInspectionHistory.adapter = adapter
        if (!SharedUtils.isOnline(requireContext())){
            showHistoryList(App.db.checkListBambaDao().getHistory(SharedUtils.getUsuarioId(context)).map { it.toMapper() })
        }
        else if(parentViewModel.uiState.isSearching){
            showHistoryList(parentViewModel.uiHistoryList)
        } else{
            viewModel.getHistory(SharedUtils.getUsuarioId(context))
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listHistory.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    HistoryCheckListUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo historico")
                    }
                    HistoryCheckListUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }
                    HistoryCheckListUIEvent.Error -> {

                    }
                    is HistoryCheckListUIEvent.Success -> {
                        if(event.listHistory.isNotEmpty()){
                            if (parentViewModel.uiHistoryList != event.listHistory){
                                parentViewModel.setHistoryList(event.listHistory)
                                showHistoryList(event.listHistory)
                                App.db.checkListBambaDao().insertHistory(event.listHistory.map { it.toMapper()})
                            }
                        }
                        else{
                            showEmptyHistory()

                        }
                    }
                    is HistoryCheckListUIEvent.SuccessSearchByCheckingHead -> {
                        parentViewModel.setListQuestions(event.listHistory)
                        parentViewModel.setEnableBtn(false)
                        findNavController().navigate(R.id.action_historyFragment_to_listCheckListFragment)
                    }
                }
            }
        }

        binding.btnNewInspection.setOnClickListener {
            findNavController().navigate(R.id.action_historyFragment_to_swornDeclarationFragment)
        }
        NewCheckListScope.scope = ScopesChecklist.HISTORY
    }


    private fun showEmptyHistory() = with(binding) {
        animationInspectionHistory.visibility = View.VISIBLE
        tvNoRegister.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistoryList(listHistory: List<NewCheckListHistory>) = with(binding) {
        animationInspectionHistory.visibility = View.GONE
        tvNoRegister.visibility = View.GONE
        adapter.setList(listHistory)
    }

    override fun onButtonClick(data: NewCheckListHistory) {
        viewModel.searchbyChechingHead(data.checklistInstanceId)
    }

}