package com.webcontrol.android.ui.newchecklist.views.inputdata

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.InspeccionVehicular
import com.webcontrol.android.databinding.FragmentVehicularInspectionBinding
import com.webcontrol.android.ui.newchecklist.NewCheckListViewModel
import com.webcontrol.android.ui.newchecklist.data.NewCheckListScope
import com.webcontrol.android.ui.newchecklist.data.ScopesChecklist
import com.webcontrol.android.ui.newchecklist.data.VehicleInformation
import com.webcontrol.android.ui.newchecklist.data.WorkerVehicleInformation
import com.webcontrol.android.util.BaseFragment
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InputDataFragment : BaseFragment<FragmentVehicularInspectionBinding, InputDataViewModel>() {


    private val parentViewModel: NewCheckListViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Datos de Checklist"
    }

    override fun getViewModelClass() = InputDataViewModel::class.java
    override fun getViewBinding() = FragmentVehicularInspectionBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.lblDate.text = currentDate

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding.lblHour.text = currentTime

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventUIInputData.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    InputDataUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo datos del vehículo")
                    }
                    InputDataUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }
                    InputDataUIEvent.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Hubo un error en el ingreso de la información",
                            Toast.LENGTH_SHORT
                        )
                    }
                    is InputDataUIEvent.Success -> {
                        if (event.listVehicleInformation.idVEHICULO != 0){
                            parentViewModel.setVehicleInformation(event.listVehicleInformation)
                            showModel(event.listVehicleInformation)
                        }
                        else
                            Toast.makeText(
                                requireContext(),
                                "No se encuentran datos para la placa ingresada",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                    is InputDataUIEvent.SuccessValidate -> {
                        parentViewModel.setListQuestions(listOf())
                        parentViewModel.setCheckingHead(event.answerCheckingHead)
                        findNavController().navigate(R.id.action_inputDataFragment_to_listCheckListFragment)
                    }
                }
            }
        }
        NewCheckListScope.scope = ScopesChecklist.INPUT_DATA
    }

    override fun setUpViews() {
        super.setUpViews()

        binding.etPlate.setText("")
        binding.etOdometer.setText("")
        binding.etModel.setText("")

        binding.btnSearchPlate.setOnClickListener {
            val textClean = binding.etPlate.text.toString().trim()
            if (textClean.length > 5) {
                viewModel.getInfoCar(textClean)
            }
        }
        if (SharedUtils.isOnline(requireContext())){
            binding.btnSearchPlate.setOnClickListener {
                val textClean = binding.etPlate.text.toString().trim()
                if (textClean.length > 5) {
                    viewModel.getInfoCar(textClean)
                }
            }

            binding.etSupervisorName.setText(parentViewModel.uiState.supervisorName)
            parentViewModel.uiVehicle.let {
                binding.etPlate.setText(it.placapatente)
            }
            binding.etDriverName.setText(SharedUtils.getUsuario(requireContext()))
        }
        else{
            var inspeccion = App.db.checkListBambaDao().getTnpeccionVehicular()
            binding.etSupervisorName.setText(inspeccion.supervisor)
            binding.etPlate.setText(inspeccion.placa)
            binding.etDriverName.setText(inspeccion.operador)
            binding.etModel.setText(inspeccion.marca)
            binding.spinnerShiftType.setText(inspeccion.turno)
        }


        binding.btnContinueInsp.setOnClickListener {
            parentViewModel.setEnableBtn(true)
            if (validateInputData()) {
                if (SharedUtils.isOnline(requireContext())){
                    viewModel.validateInputData(
                        WorkerVehicleInformation(
                            workerID = SharedUtils.getUsuarioId(requireContext()),
                            plate = binding.etPlate.text.toString(),
                            model = binding.etModel.text.toString(),
                            turn = binding.spinnerShiftType.text.toString(),
                            odometer = binding.etOdometer.text.toString().toInt()
                        )
                    )
                    App.db.checkListBambaDao().insertInpeccionVehicular(InspeccionVehicular(
                        operador   = SharedUtils.getUsuario(requireContext()),
                        supervisor = parentViewModel.uiState.supervisorName,
                        marca =  binding.etModel.text.toString(),
                        placa = binding.etPlate.text.toString(),
                        turno = binding.spinnerShiftType.text.toString(),
                        odometria = binding.etOdometer.text.toString().toInt()
                    ))
                }
                else{
                    findNavController().navigate(R.id.action_inputDataFragment_to_listCheckListFragment)
                }


            } else {
                Toast.makeText(
                    requireContext(),
                    "Debe completar todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val arrayAdapter: ArrayAdapter<*>
        val adapterList = listOf(
            "Mañana",
            "Tarde"
        )
        arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item, adapterList
        )
        binding.spinnerShiftType.setAdapter(arrayAdapter)
    }

    private fun validateInputData() = binding.etModel.text.toString().isNotEmpty()
        && binding.spinnerShiftType.text.toString().isNotEmpty()
        && binding.etOdometer.text.toString().isNotEmpty()
        && binding.etPlate.text.toString().isNotEmpty()

    private fun showModel(vehicleInformation: VehicleInformation) = with(binding) {
        val brandAndModel = "${vehicleInformation.marca?:"NA"} - ${vehicleInformation.modelo?:"NA"}"
        etModel.setText(brandAndModel)
    }
}