package com.webcontrol.angloamerican.ui.preaccessmine.ui.newPreaccesMine

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.db.entity.CheckListTest
import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.data.model.*
import com.webcontrol.angloamerican.databinding.FragmentPreaccessMineBinding
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesViewModel
import com.webcontrol.angloamerican.ui.preaccessmine.PreAccessMineActivity
import com.webcontrol.angloamerican.ui.preaccessmine.PreAccessMineViewModel
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.utils.SharedUtils
import com.webcontrol.core.utils.SharedUtils.time
import com.webcontrol.core.utils.SharedUtils.wCDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class NewPreAccessMineFragment :
    BaseFragment<FragmentPreaccessMineBinding, NewPreAccessMineViewModel>() {

    private val parentViewModel: PreAccessMineViewModel by activityViewModels()
    private val divisionList by lazy {
        mutableListOf<Division>()
    }
    private val localList by lazy {
        mutableListOf<Local>()
    }
    private lateinit var ddlDivision: AutoCompleteTextView
    private lateinit var ddlLocal: AutoCompleteTextView
    private lateinit var ddlSentido: AutoCompleteTextView
    private lateinit var txtPatente: TextInputEditText
    private var isAuthorized = false
    private var isAnimationInProgress = false
    private var divisionFlag = false
    private var error: String? = null
    private var vehiculo: Vehiculo? = null
    private var currentIdDb: Int? = null
    private var shouldInsertParkingUsage = false
    private var worker: WorkerAnglo? = null
    val calendar: Calendar = Calendar.getInstance()
    private val preacceso by lazy {
        PreaccesoMina()
    }

    override fun getViewModelClass() = NewPreAccessMineViewModel::class.java
    override fun getViewBinding() = FragmentPreaccessMineBinding.inflate(layoutInflater)

    override fun setUpViews() {
        super.setUpViews()
        (requireActivity() as PreAccessMineActivity).isVisibleToolBar(false)
        setWorkerInfo()
        setUserPhoto()
        setDivisionSpinner()
        setLocalSpinner()
        setSentidoSpinner()
        textChangedListeners()

        preacceso.patente = "ZZZ999"

        binding.check.setOnClickListener {
            binding.check.isEnabled = false
            binding.ddlDivision.isClickable = false
            binding.ddlDivision.dismissDropDown()
            binding.ddlSentido.isClickable = false
            binding.ddlLocal.isClickable = false
            binding.txtPatente.isEnabled = false
            binding.check.visibility = View.INVISIBLE
            binding.animationView.setAnimation(R.raw.loader_rest)
            binding.animationView.visibility = View.VISIBLE
            binding.animationView.repeatCount = ValueAnimator.INFINITE
            binding.animationView.playAnimation()
            viewModel.getVehicle(
                VehicleRequest(
                    vehicleId = binding.txtPatente.text.toString(),
                    divisionId = preacceso.division
                )
            )
        }
    }

    private fun setDivisionSpinner() {
        divisionList.add(Division("SIN_DIVISION", "SELECCIONE DIVISION"))
        val adapter =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                divisionList
            )
        binding.ddlDivision.setAdapter(adapter)

        binding.ddlDivision.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                preacceso.division = divisionList[position].id
                preacceso.divisionNombre = binding.ddlDivision.text.toString()
                isAuthorized = false
                binding.check.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_bottom)
                binding.animationView.visibility = View.GONE
                binding.check.visibility = View.VISIBLE
                binding.mySpinnerDivision.error = null

                viewModel.getLocals(
                    LocalRequest(
                        divisionId = preacceso.division,
                        localType = LOCAL_TYPE_ACCESO
                    )
                )
            }

        viewModel.getDivisions()
    }

    private fun setLocalSpinner() {
        localList.add(Local("SIN_LOCAL", "SIN LOCAL"))
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            localList
        )
        binding.ddlLocal.setAdapter(adapter)

        binding.ddlLocal.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val localId = localList[position].id
                preacceso.local = localId
                preacceso.localNombre = binding.ddlLocal.text.toString()
                binding.mySpinnerLocal.error = null
            }
    }

    private fun setSentidoSpinner() {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, SENTIDO)
        binding.ddlSentido.setAdapter(adapter)
        binding.ddlSentido.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            binding.mySpinnerSentido.error = null
        }
    }

    private fun setWorkerInfo() = with(binding) {
        txtNombre.text = parentViewModel.workerId.workerName
        txtRut.text = parentViewModel.workerId.workerId
    }

    private fun setUserPhoto() {
        var urlPhoto = "%suser/%s/foto"
        urlPhoto =
            String.format(
                urlPhoto,
                "http://m1.webcontrolcorp.com/ws/webcontrol/api/api/",
                parentViewModel.workerId.workerId
            )
        Glide.with(this)
            .load(urlPhoto)
            .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
            .error(R.drawable.ic_account_circle_materialgrey_240dp)
            .circleCrop()
            .into(binding.imgProfile)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listHistory.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    NewPreAccessMineUIEvent.ShowLoading -> {
                        (requireActivity() as PreAccessMineActivity).isLoading(true, "Cargando")
                    }

                    NewPreAccessMineUIEvent.HideLoading -> {
                        (requireActivity() as PreAccessMineActivity).isLoading(false)
                    }

                    is NewPreAccessMineUIEvent.Error -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }

                    is NewPreAccessMineUIEvent.Success -> {
                        Log.i("Parking", event.message)
                    }

                    is NewPreAccessMineUIEvent.SuccessDivision -> {
                        if (event.listDivision.isNotEmpty()) {
                            divisionList.clear()
                            divisionList.addAll(event.listDivision.filter {
                                it.id == "LB"
                            })

                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.support_simple_spinner_dropdown_item,
                                divisionList
                            )
                            binding.ddlDivision.setAdapter(adapter)
                            divisionFlag = true
                        }
                    }

                    is NewPreAccessMineUIEvent.SuccessCheckList -> {
                        if (event.pair.first == ChecklistType.TDV) {

                        } else {

                        }
                    }

                    is NewPreAccessMineUIEvent.SuccessLocal -> {
                        if (event.listLocal.isNotEmpty()) {
                            localList.clear()
                            localList.addAll(event.listLocal)
                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.support_simple_spinner_dropdown_item,
                                localList
                            )
                            binding.ddlLocal.setAdapter(adapter)
                        }
                    }

                    is NewPreAccessMineUIEvent.SuccessValidation -> {
                        if(event.validate){
                            viewModel.insertPreaccessMine(preacceso)
                        }
                        else{
                            Toast.makeText(requireContext(), "Local no permitido", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is NewPreAccessMineUIEvent.SuccessWorkerInfo -> {
                        worker = event.workerInfo
                        binding.txtEmpresa.text = worker?.companiaNombre
                        insertPreAccess(parentViewModel.preAccessMineId)
                        when (binding.questionGroup.checkedRadioButtonId) {
                            binding.radioButtonYes.id -> {
                                findNavController().navigate(
                                    R.id.action_newPreAccess_to_preAccessMinePassengers
                                )
                            }
                            binding.radioButtonNo.id -> {
                                viewModel.postReservationPreaccessMine()
                                findNavController().navigate(
                                    R.id.action_newPreAccess_to_historyPreaccesMine
                                )
                            }
                        }
                    }

                    is NewPreAccessMineUIEvent.SuccessInsertPreAcceso -> {
                        parentViewModel.setPreAccessMineId(event.id)
                        viewModel.getWorkerInfo(
                            WorkerRequest(
                                workerId = parentViewModel.workerId.workerId,
                                divisionId = preacceso.division
                            )
                        )
                    }
                    is NewPreAccessMineUIEvent.SuccessVehicle -> {
                        if (event.vehicle.id == "") {
                            Toast.makeText(
                                requireContext(),
                                "Patente no encontrada",
                                Toast.LENGTH_SHORT
                            ).show()
                            error = ERROR_314
                            showValidationError()
                            savePreaccess()
                        } else {
                            if (event.vehicle.isAutor) {
                                error = ""
                                vehiculo = event.vehicle
                                showOk()
                                validateChecklists()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Vehiculo no autorizado",
                                    Toast.LENGTH_SHORT
                                ).show()
                                error = ERROR_314
                                showValidationError()
                                savePreaccess()
                            }
                        }
                    }
                }
            }
        }
        navigateToNewPreAccessPassengers()
    }

    private fun textChangedListeners() {
        binding.txtPatente.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (preacceso.division != null) {
                    if (s.isNotEmpty() && preacceso.division.isNotEmpty()) {
                        binding.check.isEnabled = true
                        binding.check.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.round_bottom
                            )
                    } else {
                        binding.check.isEnabled = false
                        binding.check.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.round_bottom_disabled
                        )
                    }
                }
                isAuthorized = false
                binding.tilPatente.error = null
                binding.animationView.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun savePreaccessTest() {
        val checkListTest = CheckListTest()
        checkListTest.workerId = worker?.id ?: ""
        checkListTest.fechaSubmit = wCDate
        checkListTest.horaSubmit = time
        checkListTest.tipoTest = ChecklistType.TPA.name
        checkListTest.estadoInterno = 0
        checkListTest.divisionId = preacceso.division
        checkListTest.companyId = worker!!.companiaId
        checkListTest.vehicleId = binding.txtPatente.text.toString().uppercase(Locale.getDefault())
        checkListTest.divisionName = preacceso.divisionNombre
        checkListTest.localId = preacceso.local
        checkListTest.localName = preacceso.localNombre
        viewModel.postCheckList(checkListTest)
    }

    private fun insertPreAccess(id: Long) {
        if (isAuthorized) {
            savePreaccessTest()
            savePreaccessDetail(id)
        } else if (error != "") {
            savePreaccessDetail(id)
        } else {
            Toast.makeText(requireContext(), "Ocurrió un error al insertar el registro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateChecklists() {
        viewModel.getChecklist(
            type = ChecklistType.TFS,
            workerId = parentViewModel.workerId.workerId,
            vehicleId = binding.txtPatente.text.toString(),
            date = wCDate
        )
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val dateBefore = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.time)

        viewModel.getChecklist(
            type = ChecklistType.TDV,
            workerId = parentViewModel.workerId.workerId,
            vehicleId = binding.txtPatente.text.toString(),
            date = dateBefore
        )
        viewModel.getChecklistDetail(
            idDb = currentIdDb
        )

    }

    private fun savePreaccess() {
        preacceso.fecha = wCDate
        preacceso.hora = time
        preacceso.conductor = parentViewModel.workerId.workerId
        preacceso.patente = binding.txtPatente.text.toString().toUpperCase(Locale.getDefault())
        preacceso.sentido = if (binding.ddlSentido.text.toString() == "INGRESO") "IN" else "OUT"

        if (binding.radioButtonYes.isChecked) {
            preacceso.passengers = "SI"
        } else if (binding.radioButtonNo.isChecked) {
            preacceso.passengers = "NO"
        }
        preacceso.estado = STATUS_P
        preacceso.createdAt = System.currentTimeMillis().toString()
        val viaje = String.format(
            TRIPID_FORMAT,
            preacceso.fecha,
            preacceso.hora.replace(":", ""),
            preacceso.patente
        )
        preacceso.viaje = viaje

        if (shouldInsertParkingUsage) {
            insertParkingUsage()
        }
    }

    private fun validateDat(fecha: String): String {
        return try {
            val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            dateFormat.parse(fecha).time.toString()
        } catch (e: Exception) {
            ""
        }
    }
    private fun savePreaccessDetail(id: Long) {
        val chofer = PreaccesoDetalleMina(
            id.toInt(),
            preacceso.conductor,
            preacceso.hora,
            worker!!.companiaId,
            worker!!.companiaNombre,
            error,
            worker!!.centroCosto,
            worker!!.ost,
            worker!!.tipoPase,
            STATUS_P,
            validateDat(preacceso.fecha),
            "",
            worker!!.nombre,
            worker!!.apellidos,
            worker!!.autor,
            "S", "",
            worker!!.validated ?: true
        )
        viewModel.insertPreaccessDetail(chofer)
    }

    private fun insertParkingUsage() {
        viewModel.insertParking(
            ParkingUsage(
                preacceso.conductor,
                preacceso.patente,
                preacceso.fecha,
                preacceso.division,
                preacceso.local,
                preacceso.sentido
            )
        )
    }

    fun showOk() {
        isAuthorized = true
        binding.check.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_bottom_green)
        binding.txtPatente.isEnabled = true
        binding.animationView.setAnimation(R.raw.spinner_done)
        binding.animationView.repeatCount = 0
        binding.animationView.playAnimation()
        binding.animationView.removeAllAnimatorListeners()
        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                binding.check.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                binding.check.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        Toast.makeText(requireContext(), "Patente verificada", Toast.LENGTH_SHORT).show()
    }

    private fun showValidationError() {
        isAuthorized = false
        binding.check.background = ContextCompat.getDrawable(requireContext(), R.drawable.round_bottom_red)
        binding.animationView.setAnimation(R.raw.spinner_error)
        binding.txtPatente.isEnabled = true
        binding.animationView.repeatCount = 0
        binding.animationView.playAnimation()
        binding.animationView.removeAllAnimatorListeners()
        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                binding.check.visibility = View.INVISIBLE
                isAnimationInProgress = true
            }

            override fun onAnimationEnd(animation: Animator) {
                binding.check.visibility = View.VISIBLE
                isAnimationInProgress = false
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun navigateToNewPreAccessPassengers() {
        binding.btnSiguiente.setOnClickListener {
            val divisionSelected = binding.ddlDivision.text.toString()
            val localSelected = binding.ddlLocal.text.toString()
            val sentidoSelected = binding.ddlSentido.text.toString()
            val patenteText = binding.txtPatente.text.toString()

            savePreaccess()

            if (divisionSelected.isEmpty() || localSelected.isEmpty()) {
                Toast.makeText(context, "Los campos División y Local son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (sentidoSelected.isEmpty()) {
                Toast.makeText(context, "El campo Sentido es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (patenteText.isEmpty()) {
                Toast.makeText(context, "El campo Patente es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.questionGroup.checkedRadioButtonId == -1) {
                Toast.makeText(context, "Debe seleccionar si incluye pasajero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.getValidationByWorkerId(parentViewModel.workerId.workerId, preacceso.local)
        }
    }

    companion object {
        const val LOCAL_TYPE_ACCESO = "ACCESO"
        private val SENTIDO = arrayOf("INGRESO", "SALIDA")
        const val ERROR_314 = "314"
        const val STATUS_P = "P"
        const val STATUS_S = "S"
        const val TRIPID_FORMAT = "%sT%s%s"

        enum class ChecklistType {
            TFS,
            TDV,
            TPA,
            VTP
        }
    }
}