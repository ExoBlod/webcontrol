package com.webcontrol.android.ui.newchecklist.views.consultinspections

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentConsultChecklistBinding
import com.webcontrol.android.ui.newchecklist.NewCheckListViewModel
import com.webcontrol.android.ui.newchecklist.data.NewCheckListScope
import com.webcontrol.android.ui.newchecklist.data.ScopesChecklist
import com.webcontrol.android.util.BaseFragment
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ConsultInspectionsFragment: BaseFragment<FragmentConsultChecklistBinding,ConsultInspectionsViewModel>() {

    private val parentViewModel: NewCheckListViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Consultar Inspecciones"
    }

    override fun getViewModelClass() = ConsultInspectionsViewModel::class.java
    override fun getViewBinding() =  FragmentConsultChecklistBinding.inflate (layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.lblFecha.text = currentDate

        binding.etDate.isFocusableInTouchMode = false
        binding.etDate.isFocusable = false

        if(parentViewModel.uiState.toHistory)
            findNavController().navigate(R.id.action_consultInspectionsFragment_to_historyFragment)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listHistory.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    ConsultInspectionsUIEvent.Error -> {

                    }
                    ConsultInspectionsUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }
                    ConsultInspectionsUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo historico")
                    }
                    is ConsultInspectionsUIEvent.Success -> {
                        if(event.listHistory.isNotEmpty()){
                            parentViewModel.uiState.isSearching = true
                            parentViewModel.setHistoryList(event.listHistory)
                            findNavController().navigate(R.id.action_consultInspectionsFragment_to_historyFragment)
                        }
                        else
                        {
                            Toast.makeText(
                                requireContext(),
                                "Usuario no tiene historico",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        binding.btnConsultByDNI.setOnClickListener {
            val date = binding.etDate.text.toString()
            viewModel.getHistoryByWorkerID(
                binding.etConsultCamp.text.toString(),
                SharedUtils.getUsuarioId(requireContext()),
                convertDateFormat(date),
                parentViewModel.uiState.filter
            )
        }

        binding.btnConsultByPlate.setOnClickListener {
            val date = binding.etDate.text.toString()
            viewModel.getHistoryByPlate(
                binding.etConsultCamp.text.toString(),
                SharedUtils.getUsuarioId(requireContext()),
                convertDateFormat(date),
                parentViewModel.uiState.filter
            )
        }

        binding.etDate.setOnClickListener(){
            showDatePickerDialog()
        }
        NewCheckListScope.scope = ScopesChecklist.CONSULT_INSPECTION
    }

    private fun showDatePickerDialog() {
        val year = viewModel.getYear()
        val month = viewModel.getMonth()
        val dayOfMonth = viewModel.getDayOfMonth()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.etDate.setText(selectedDate)
            }, year, month, dayOfMonth
        )

        datePickerDialog.show()
    }

    fun convertDateFormat(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
}