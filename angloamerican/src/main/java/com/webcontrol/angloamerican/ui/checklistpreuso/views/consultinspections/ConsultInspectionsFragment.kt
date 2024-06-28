package com.webcontrol.angloamerican.ui.checklistpreuso.views.consultinspections

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.FragmentPreUsoConsultChecklistBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoViewModel
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByPlateRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByWorkerIdRequest
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.angloamerican.utils.Utils
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ConsultInspectionsFragment: BaseFragment<FragmentPreUsoConsultChecklistBinding, ConsultInspectionsViewModel>() {

    private val parentViewModel: CheckListPreUsoViewModel by sharedViewModel()
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Consultar Inspecciones"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getViewModelClass() = ConsultInspectionsViewModel::class.java
    override fun getViewBinding() =  FragmentPreUsoConsultChecklistBinding.inflate (layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lblHour.text = currentTime
        binding.etDate.isFocusableInTouchMode = false
        binding.etDate.isFocusable = false
        inputMayus()
        if(!parentViewModel.uiCheckListPreUso.isConsultInspection){
            findNavController().navigate(R.id.action_consultInspectionsPreUsoFragment_to_historyPreUsoFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listHistory.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    is ConsultInspectionsUIEvent.ErrorLog -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        Log.e("ErrorConsult",event.message)
                    }
                    ConsultInspectionsUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }
                    ConsultInspectionsUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo historico")
                    }
                    is ConsultInspectionsUIEvent.Success -> {
                        if(event.listHistory.isNotEmpty()){
                            parentViewModel.setIsSearching(true)
                            parentViewModel.setHistoryList(event.listHistory)
                            parentViewModel.setIsConsulting(true)
                            findNavController().navigate(R.id.action_consultInspectionsPreUsoFragment_to_historyPreUsoFragment)
                        }
                        else
                        {
                            Utils.showNoRecordsDialog(requireContext())
                        }
                    }
                }
            }
        }
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        binding.lblFecha.text = currentDate

        binding.btnConsult.setOnClickListener {
            val date = binding.etDate.text.toString()
            val plate = binding.etConsultCamp.text.toString()
            if (date.isNotEmpty() && plate.isNotEmpty() ) {
                val request = HistoryByWorkerIdRequest(plate , convertDateFormat(date))
                if (binding.spinnerType.text.toString() == "DNI")
                viewModel.getHistoryByWorkerID(request)
                else {
                    val requestPlate = HistoryByPlateRequest(plate , convertDateFormat(date))
                    viewModel.getHistoryByPlate(requestPlate)
                }
            } else {
                Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etDate.setOnClickListener {
            showDatePickerDialog()
        }

        setSpinnerData()
    }

    private fun setSpinnerData(){
        val arrayAdapter: ArrayAdapter<*>

        val adapterList = listOf(
            "PLACA",
            "DNI"
        )

        arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item, adapterList
        )
        binding.spinnerType.setAdapter(arrayAdapter)

        binding.spinnerType.setOnItemClickListener { _, _, position, _ ->
            when(position){
                PLATE -> binding.textInputLayoutPlate.hint = "Placa"
                DNI -> binding.textInputLayoutPlate.hint = "DNI"
            }
        }
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

    private fun inputMayus(){
        binding.etConsultCamp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                binding.etConsultCamp.removeTextChangedListener(this)
                val textInUpperCase = editable.toString().toUpperCase()
                binding.etConsultCamp.setText(textInUpperCase)
                binding.etConsultCamp.setSelection(textInUpperCase.length)
                binding.etConsultCamp.addTextChangedListener(this)
            }
        })
    }

    companion object{
        val PLATE = 0
        val DNI = 1
    }
}