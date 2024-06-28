package com.webcontrol.angloamerican.ui.checklistpreuso.views.inputdata

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.FragmentPreUsoVehicularInspectionBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoActivity
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoViewModel
import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListScope
import com.webcontrol.angloamerican.ui.checklistpreuso.data.ScopesChecklist
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.AppDataBase
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.entity.InspeccionVehicular
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.VehicleInformationResponse
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class InputDataFragment : BaseFragment<FragmentPreUsoVehicularInspectionBinding, InputDataViewModel>() {

    private val parentViewModel: CheckListPreUsoViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Datos de Checklist"
    }
    val emptyString = String()
    val adapterList = listOf("DIA", "NOCHE")
    val adaterFuel = listOf("0","1/4","2/4","3/4","4/4")

    private val db: AppDataBase by inject()
    override fun getViewModelClass() = InputDataViewModel::class.java
    override fun getViewBinding() = FragmentPreUsoVehicularInspectionBinding.inflate(layoutInflater)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Disable onBackPressed for right navigation
                Toast.makeText(requireContext(), "No puede volver hacia atrás", Toast.LENGTH_SHORT).show()
            }
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputMayus()
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.lblDate.text = currentDate

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding.lblHour.text = currentTime

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventUIInputData.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    InputDataUIEvent.ShowLoading -> {
                        (requireActivity() as CheckListPreUsoActivity).showLoading(true)
                        SharedUtils.showLoader(requireContext(), "Obteniendo datos del vehículo")
                    }
                    InputDataUIEvent.HideLoading -> {
                        (requireActivity() as CheckListPreUsoActivity).showLoading(false)
                        SharedUtils.dismissLoader(requireContext())
                    }
                    InputDataUIEvent.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Hubo un error en el ingreso de la información",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is InputDataUIEvent.Success -> {
                        if(event.listVehicleInformationResponse.isNotEmpty()){
                            val vehicle = event.listVehicleInformationResponse.first()
                            parentViewModel.setVehicleInformation(vehicle)
                            showModel(vehicle)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No se encuentran datos para la placa ingresada",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    is InputDataUIEvent.SuccessValidate -> {
                        parentViewModel.cleanQuestionHistory()
                        val checkingHead = event.answerCheckingHead[0].checkingHead
                        parentViewModel.setCheckingHead(checkingHead.toInt())
                        //parentViewModel.setListQuestions(listOf())
                        //parentViewModel.uiListGroup[0].checkIdHead = checkingHead.toInt()
                        findNavController().navigate(R.id.action_inputDataPreUsoFragment_to_listCheckListPreUsoFragment)
                        //findNavController().navigate(R.id.action_inputDataFragment_to_listCheckListFragment)
                    }
                }
            }
        }
        NewCheckListScope.scope = ScopesChecklist.INPUT_DATA
        setupPlateEditText()
        binding.etPlate.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.textInputLayoutPlate.helperText = getString(R.string.especial_caracteres)
            } else {
                binding.textInputLayoutPlate.helperText = emptyString
            }
        }
    }

    private fun setupPlateEditText() {
        val filter = InputFilter { source, _, _, _, _, _ ->
            if (source.toString().matches("[A-Za-z0-9 ]+".toRegex())) {
                null
            } else {
                emptyString
            }
        }
        binding.etPlate.filters = arrayOf(filter)
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun setUpViews() {
        super.setUpViews()

        binding.etPlate.setText("")
        binding.etOdometer.setText("")
        binding.etModel.setText("")

        binding.textInputLayoutPlate.setEndIconOnClickListener {
            val textClean = binding.etPlate.text.toString().trim()
            if (textClean.length >= 5) {
                viewModel.getVehicleInformation(textClean)
                requireActivity().hideKeyboard(it)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Ingrese una placa correcta",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (SharedUtils.isOnline(requireContext())){
            binding.etSupervisorName.setText(parentViewModel.uiCheckListPreUso.supervisorName)
            parentViewModel.uiVehicle.let {
                binding.etPlate.setText(it.plate)
            }
            binding.etDriverName.setText(SharedUtils.getUsuario(requireContext()))
        }
        else{
            var inspeccion = db.checkListPreUso().getTnpeccionVehicular()
            binding.etSupervisorName.setText(inspeccion.supervisor)
            binding.etPlate.setText(inspeccion.placa)
            binding.etDriverName.setText(inspeccion.operador)
            binding.etModel.setText(inspeccion.marca)
            binding.spinnerShiftType.setText(inspeccion.turno)
        }

        binding.btnContinueInsp.setOnClickListener {
            if (validateInputData()) {
                if (!SharedUtils.isOnline(requireContext())){
                    db.checkListPreUso().insertInpeccionVehicular(InspeccionVehicular(
                        operador   = SharedUtils.getUsuario(requireContext()),
                        supervisor = parentViewModel.uiState.supervisorName,
                        marca =  binding.etModel.text.toString(),
                        placa = binding.etPlate.text.toString(),
                        turno = binding.spinnerShiftType.text.toString(),
                        odometria = binding.etOdometer.text.toString().toInt()
                    ))
                }
                else{
                    viewModel.validateInputData(
                        InsertInspectionRequest(
                            workerId = SharedUtils.getUsuarioId(requireContext()),
                            plate = binding.etPlate.text.toString(),
                            model = parentViewModel.uiVehicle.model,
                            brand =  parentViewModel.uiVehicle.brand,
                            turn = binding.spinnerShiftType.text.toString(),
                            odometer = binding.etOdometer.text.toString().toInt(),
                            typeVehicle =parentViewModel.uiVehicle.typeVehicle,
                            maintenance = binding.etManteinance.text.toString().toInt(),
                            fuelLevel = binding.spinnerFuel.text.toString()
                        )
                        /*workerID = SharedUtils.getUsuarioId(requireContext()),
                        plate = binding.etPlate.text.toString(),
                        model = binding.etModel.text.toString(),
                        turn = binding.spinnerShiftType.text.toString(),
                        odometer = binding.etOdometer.text.toString().toInt()*/
                    )
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
        arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.shift_spinner_item, adapterList
        )
        binding.spinnerShiftType.setAdapter(arrayAdapter)

        val fuelLevel: ArrayAdapter<*>
        fuelLevel = ArrayAdapter(
            requireContext(),
            R.layout.shift_spinner_item, adaterFuel
        )
        binding.spinnerFuel.setAdapter(fuelLevel)
    }

    private fun validateInputData() = binding.etModel.text.toString().isNotEmpty()
            && binding.spinnerShiftType.text.toString().isNotEmpty()
            && binding.etOdometer.text.toString().isNotEmpty()
            && binding.etPlate.text.toString().isNotEmpty()
            && binding.spinnerFuel.text.isNotEmpty()
            && binding.etManteinance.text.toString().isNotEmpty()

    private fun showModel(vehicleInformation: VehicleInformationResponse) = with(binding) {
        val brandAndModel = "${vehicleInformation.brand} - ${vehicleInformation.model}"
        etModel.setText(brandAndModel)
    }

    private fun inputMayus(){
        binding.etPlate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                binding.etPlate.removeTextChangedListener(this)
                val textInUpperCase = editable.toString().toUpperCase()
                binding.etPlate.setText(textInUpperCase)
                binding.etPlate.setSelection(textInUpperCase.length)
                binding.etPlate.addTextChangedListener(this)
            }
        })
    }

}