package com.webcontrol.android.ui.preacceso

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.db.entity.Preacceso
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.model.enum.ChecklistType
import com.webcontrol.android.data.network.*
import com.webcontrol.android.databinding.FragmentPreaccesoBinding
import com.webcontrol.android.ui.common.LoadingView
import com.webcontrol.android.ui.owndoc.ImageController
import com.webcontrol.android.util.*
import com.webcontrol.android.util.RestClient.buildAnglo
import com.webcontrol.android.util.SharedUtils.getUsuario
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.showToast
import com.webcontrol.android.util.SharedUtils.time
import com.webcontrol.android.util.SharedUtils.wCDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_update_doc.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val SELECT_ACTIVITY = 50

@AndroidEntryPoint
class CabeceraActivity : AppCompatActivity() {
    private val cabeceraViewModel by viewModel<CabeceraViewModel> { parametersOf(client) }
    private val parkingViewModel by viewModel<ParkingViewModel>()
    var typeFileSelected = false
    var byteArrayFile: ByteArray? = null

    private lateinit var binding: FragmentPreaccesoBinding
    private var empresaList: List<Company>? = null
    private lateinit var divisionList: MutableList<Division>
    private lateinit var localList: MutableList<Local>
    private lateinit var preacceso: Preacceso
    private var isAuthorized = false
    private var isParticularVehicle = false
    private var divisionFlag = false
    private var isParticularPreAccess = false
    private var activeDispatch = false
    private lateinit var loader: LoadingView
    private lateinit var client: String
    private lateinit var timeServer: String
    private var uuid: String = ""
    private var worker: WorkerAnglo? = null
    private var vehiculo: Vehiculo? = null
    private var totalParkingSpaces = 0
    private var shouldInsertParkingUsage = false
    private var vehicleLicences: String? = null
    private var localSelected: String? = null
    private var error: String? = null

    private var currentIdDb: Int? = null
    val calendar: Calendar = Calendar.getInstance()
    private val particularVehicleTypes =
        listOf("PARTICULAR/ESE", "VEHI ACERCAMIENTO", "PARTICULAR/AA")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPreaccesoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.fragment_preacceso)
        client = intent.getStringExtra(ControlPreaccesoFragment.CLIENT)!!
        loader = LoadingView(this)
        setWorkerInfoUI()
        preacceso = Preacceso()
        preacceso.patente = "ZZZ999"
        empresaList = ArrayList()
        divisionList = ArrayList()
        localList = ArrayList()
        cabeceraViewModel.getTime()

        etUpdateFile.setOnClickListener {
            ImageController.selectFileFromGallery2(this, SELECT_ACTIVITY)
        }
        //activeDispatch=true
        if (activeDispatch) {
            tilUpdateFile.visibility = View.VISIBLE
            txtUpdateDoc.visibility = View.VISIBLE
        } else {
            tilUpdateFile.visibility = View.GONE
            txtUpdateDoc.visibility = View.GONE

        }
        setObservers()
        setUISpinners()
        setUIListeners()

        binding.animationView.enableMergePathsForKitKatAndAbove(true)
        /*binding.btnSiguiente.setOnClickListener {
            val uuid = UUID.randomUUID().toString()
            val fileFront = MultipartBody.Part.createFormData(
                uuid, "${etUpdateFile.text}",
                byteArrayFile!!.toRequestBody("application/pdf".toMediaType(), 0)
            )
            sendFile(uuid,1,fileFront,this@CabeceraActivity )
        }*/

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == SELECT_ACTIVITY) {
                typeFileSelected = true
                if (data == null && byteArrayFile == null) {
                    typeFileSelected = false
                    SharedUtils.showToast(this, getString(R.string.no_file_selected))
                    return
                }
                val uri = data!!.data!!
                val iStream: InputStream? = this.contentResolver.openInputStream(uri)
                var name = ""
                uri.let {
                    this.contentResolver.query(it, null, null, null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    name = cursor.getString(nameIndex)
                }
                etUpdateFile.setText(name)
                byteArrayFile = getBytes(iStream!!)
            }
        } catch (ex: Exception) {
            SharedUtils.showToast(this, getString(R.string.unrecognized_file))
        }
    }


    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    private fun setUIListeners() {
        textChangedListeners()
        checkPatente()
        checkDriver()
    }

    private fun checkDriver() {
        binding.btnSiguiente.setOnClickListener {
            if (isValid) {
                savePreaccess()
                if (activeDispatch) {
                    uuid = UUID.randomUUID().toString()
                    val fileFront = MultipartBody.Part.createFormData(
                        uuid, "${etUpdateFile.text}",
                        byteArrayFile!!.toRequestBody("application/pdf".toMediaType(), 0)
                    )
                    sendFile(uuid, 1, fileFront, this@CabeceraActivity)
                }
                lifecycleScope.launch {
                    runCatching {
                        cabeceraViewModel.insertPreaccess(preacceso)
                    }.onSuccess {
                        insertPreAccess(it)
                        cabeceraViewModel.getWorkerInfo(
                            WorkerRequest(
                                workerId = getUsuarioId(this@CabeceraActivity),
                                divisionId = preacceso.division
                            )
                        )


                    }.onFailure {
                        showToast(
                            this@CabeceraActivity,
                            "Sucedio un error cuando al insertar los datos."
                        )
                    }
                }
            } else {
                showToast(this@CabeceraActivity, "Verifique campos.")
            }

        }
    }

    private fun DismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(this)
    }

    private fun sendFile(
        idFile: String,
        cara: Int,
        image: MultipartBody.Part,
        context: Context
    ) {
        val api = RestClient.buildAnglo()
        val call = api.insertFileOwnDocs(idFile, cara, image)
        val filename = if (etTakePic != null) etTakePic.text.toString() else ""
        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                SharedUtils.showToast(
                    context,
                    getString(R.string.error_sending_information)
                )
                DismissLoader()
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<String>>,
                response: Response<ApiResponseAnglo<String>>
            ) {
                if (response.isSuccessful) {
                    SharedUtils.showToast(
                        context,
                        context.getString(R.string.file_sent_successfully)
                    )
                    /*if (byteArrayBack != null){
                        val fileBack = MultipartBody.Part.createFormData(
                            idFile,
                            filename,
                            byteArrayBack!!.toRequestBody("image/jpeg".toMediaType(),
                                0)
                        )
                        byteArrayFront = null
                        byteArrayBack = null
                        sendFile(idFile, 2, fileBack, context)
                    }*/
                }
            }
        })
    }

    private fun checkPatente() {
        var sentido = ""
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
            sentido = if (binding.ddlSentido.text.toString() == "INGRESO") "IN" else "OUT"
            cabeceraViewModel.getWorkerInfo(
                WorkerRequest(
                    workerId = getUsuarioId(this),
                    divisionId = preacceso.division
                )
            )
            cabeceraViewModel.getDispatchGuideState(
                ActiveDispatchGuideRequest(
                    DivisionId = preacceso.division,
                    VehicleID = binding.txtPatente.text.toString().toUpperCase(Locale.getDefault()),
                    InOut = sentido
                )
            )
            cabeceraViewModel.getVehicle(
                VehicleRequest(
                    vehicleId = binding.txtPatente.text.toString(),
                    divisionId = preacceso.division
                )
            )
        }
    }

    private fun setUISpinners() {
        setLocalSpinner()
        setDivisionSpinner()
        setSentidoSpinner()
    }

    private fun setDivisionSpinner() {
        divisionList.add(Division("SIN_DIVISION", "SELECCIONE DIVISION"))
        val adapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, divisionList)
        binding.ddlDivision.setAdapter(adapter)

        binding.ddlDivision.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                preacceso.division = divisionList[position].id
                preacceso.divisionNombre = binding.ddlDivision.text.toString()
                isAuthorized = false
                binding.check.background =
                    ContextCompat.getDrawable(this@CabeceraActivity, R.drawable.round_bottom)
                binding.animationView.visibility = View.GONE
                binding.check.visibility = View.VISIBLE
                binding.mySpinnerDivision.error = null

                cabeceraViewModel.getLocals(
                    LocalRequest(
                        divisionId = preacceso.division,
                        localType = Constants.LOCAL_TYPE_INGRESO
                    )
                )
            }

        cabeceraViewModel.getDivisions()
    }

    private fun setLocalSpinner() {
        localList.add(Local("SIN_LOCAL", "SIN LOCAL"))
        val adapter = ArrayAdapter(
            this@CabeceraActivity,
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
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, SENTIDO)
        binding.ddlSentido.setAdapter(adapter)
        binding.ddlSentido.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            binding.mySpinnerSentido.error = null
        }
    }

    private fun setObservers() {
        observeTime()
        observeWorkerInfo()
        observeLastPreaccess()
        observeDivisionList()
        observeLocalList()
        observeVehicle()
        observeChecklistTFS()
        observeChecklistDetailTFS()
        observeChecklistTDV()
        observeParkingAvailability()
        observerDispatch()

    }

    private fun observerDispatch() {
        cabeceraViewModel.dispatchGuideState().observe(this) {
            if (it.data == null) {
                activeDispatch = false
                tilUpdateFile.visibility = View.GONE
                txtUpdateDoc.visibility = View.GONE
            } else {
                if (it.data.data[0].Valido) {
                    activeDispatch = true
                    tilUpdateFile.visibility = View.VISIBLE
                    txtUpdateDoc.visibility = View.VISIBLE
                }

            }
        }
    }

    private fun observeTime() {
        cabeceraViewModel.timeValue.observe(this) {
            if (!it.isNullOrEmpty()) {
                timeServer = it
            }
        }
    }


    private fun insertPreAccess(id: Long) {
        if (isAuthorized) {
            savePreaccessTest()
            savePreaccessDetail(id)
            goToPassengerActivity(id)
        } else if (error != "") {
            savePreaccessDetail(id)
        } else {
            runOnUiThread(Runnable {
                showToast(this, "Ocurrió un error al insertar el registro")
            })
        }
    }

    private fun observeChecklistTDV() {
        cabeceraViewModel.checklistTDV.observe(this) {checklistTDV->
            if (checklistTDV != null && error == "") {
                checkWorkerAuthorized()
            } else {
                showValidationError()
                MaterialDialog.Builder(this)
                    .title("Error")
                    .content(R.string.Mensaje_test_vehiculo)
                    .positiveText("Aceptar")
                    .autoDismiss(true)
                    .show()
            }
        }
    }

    private fun observeChecklistTFS() {
        cabeceraViewModel.checklistTFS.observe(this) { checklist ->
            if (checklist != null) {
                currentIdDb = checklist.idDb
            } else {
                showValidationError()
                MaterialDialog.Builder(this@CabeceraActivity)
                    .title("Error")
                    .content(R.string.Mensaje_test_fatiga)
                    .positiveText("Aceptar")
                    .autoDismiss(true)
                    .show()
            }
        }
    }

    private fun observeChecklistDetailTFS() {
        cabeceraViewModel.checklistDetailTFS.observe(this) { checklistDetailTFS ->
            if (checklistDetailTFS == null) {
                cabeceraViewModel.getChecklistDetail(
                    idDb = currentIdDb
                )
            } else {
                showValidationError()
                MaterialDialog.Builder(this@CabeceraActivity)
                    .title("Error")
                    .content(R.string.Mensaje_test_fatiga2)
                    .positiveText("Aceptar")
                    .autoDismiss(true)
                    .show()
            }
        }

    }

    private fun observeVehicle() {
        cabeceraViewModel.vehicleState().observe(this) { response ->
            if (response.isLoading) {
                loader.show()
            } else {
                loader.dismiss()
                if (response.data != null && response.error == null) {
                    if (response.data.isSuccess && response.data.data != null) {
                        val vehicle = response.data.data
                        isParticularVehicle = particularVehicleTypes.contains(vehicle!!.vehicleType)
                        if (vehicle.isAutor) {
                            error = ""
                            vehiculo = vehicle
                            if (isParticularVehicle) {
                                if ((preacceso.division == "LB" || preacceso.division == "LT")) {
                                    checkWorkerAuthorized()
                                }
                                if (preacceso.division == AngloDivisionsEnum.LB.value && client == Companies.ANGLO.valor && vehicle.vehicleType.contains(
                                        "BUS"
                                    )
                                ) {
                                    verificarRutaDia()
                                } else if (client == Companies.ANGLO.valor && isParticularVehicle) {
                                    if (binding.ddlSentido.text.toString() == "INGRESO") {
                                        parkingViewModel.getParkingCapacity()
                                    } else {
                                        shouldInsertParkingUsage = true
                                        showOk()
                                    }
                                } else {
                                    showOk()
                                }
                            } else {
                                showOk()
                                validateChecklists()
                            }
                        } else {
                            showToast(this@CabeceraActivity, "Vehiculo no autorizado")
                            error = Constants.ERROR_314
                            showValidationError()
                            savePreaccess()
                        }
                    } else {
                        showToast(this@CabeceraActivity, "Patente no encontrada")
                        error = Constants.ERROR_314
                        showValidationError()
                        savePreaccess()
                    }
                } else if (response.data == null && response.error != null) {
                    binding.check.isEnabled = true
                    binding.check.visibility = View.VISIBLE
                    binding.txtPatente.isEnabled = true
                    Snackbar.make(binding.view, "Error de red", Snackbar.LENGTH_LONG)
                        .setAction("Reintentar") {
                            cabeceraViewModel.getVehicle(
                                VehicleRequest(
                                    vehicleId = binding.txtPatente.text.toString(),
                                    divisionId = preacceso.division
                                )
                            )
                        }.show()
                }
            }
        }
    }

    private fun insertParkingUsage() {
        parkingViewModel.insertParking(
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

    private fun observeParkingAvailability() {
        parkingViewModel.parkingCapacity.observe(this) {
            if (it != null) {
                totalParkingSpaces = it[0].value.toInt()
                parkingViewModel.getAvailableParkingSpaces()
            } else {
                showValidationError()
                Toast.makeText(this, "No hay estacionamientos disponibles", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        //obtener cupos disponibles y validar
        parkingViewModel.availableParkingSpaces.observe(this) {
            if (it.taken < totalParkingSpaces) {
                shouldInsertParkingUsage = true
                showOk()
                val availableParkingSpaces = totalParkingSpaces - it.taken
                Toast.makeText(
                    this,
                    "Quedan $availableParkingSpaces estacionamientos disponibles",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showValidationError()
                Toast.makeText(
                    this,
                    "No quedan estacionamientos disponibles para vehículos particulares",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //continuar
        parkingViewModel.insertParkingUsage.observe(this) {
            Log.i("Parking", it.toString())
        }
    }

    private fun observeLocalList() {
        cabeceraViewModel.localListState().observe(this) {
            if (it.isLoading) {
                localList.clear()
                setLocalSpinner()
                loader.show()
            } else {
                loader.dismiss()
                if (it.data != null && it.error == null) {
                    val response = it.data
                    if (response.isSuccess && response.data.isNotEmpty()) {
                        localList.clear()
                        localList = response.data.toMutableList()
                        val adapter = ArrayAdapter(
                            this@CabeceraActivity,
                            R.layout.support_simple_spinner_dropdown_item,
                            localList
                        )
                        binding.ddlLocal.setAdapter(adapter)

                    }
                } else if (it.data == null && it.error != null) {
                    showToast(this, "Error al obtener locales: ${it.error}")
                }
            }
        }
    }

    private fun observeDivisionList() {
        cabeceraViewModel.divisionListState().observe(this) {
            if (it.isLoading) {
                loader.show()
            } else {
                loader.dismiss()
                if (it.data != null && it.error == null) {
                    val response = it.data
                    if (response.isSuccess && response.data.isNotEmpty()) {
                        divisionList.clear()
                        divisionList = response.data.toMutableList()
                        if (client == Companies.ANGLO.valor) {
                            var flag = 0
                            var i = 0
                            do {
                                if (divisionList[i].id != "LT" && divisionList[i].id != "LB" && divisionList[i].id != "ST") {
                                    divisionList.removeAt(i)
                                    if (i == divisionList.size - 1) {
                                        flag = 1
                                    }
                                } else {
                                    i++
                                }
                            } while (flag == 0)
                        }
                        val adapter = ArrayAdapter(
                            this@CabeceraActivity,
                            R.layout.support_simple_spinner_dropdown_item,
                            divisionList
                        )
                        binding.ddlDivision.setAdapter(adapter)
                        divisionFlag = true

                    }
                } else if (it.data == null && it.error != null) {
                    showToast(this, "Error al obtener divisiones: ${it.error}")
                }
            }
        }
    }

    private fun observeWorkerInfo() {
        cabeceraViewModel.workerInfoState().observe(this) { response ->
            if (response.isLoading) {
                loader.show()
            } else {
                loader.dismiss()
                if (response.data != null && response.error == null) {
                    if (response.data.isSuccess) {
                        worker = response.data.data
                        binding.includeheader.txtEmpresa.text = worker?.companiaNombre
                    }
                } else if (response.data == null && response.error != null) {
                    showToast(this, "Error al obtener datos de funcionario: ${response.error}")
                }
            }
        }
    }

    private fun setWorkerInfoUI() {
        binding.includeheader.txtNombre.text = getUsuario(this)
        binding.includeheader.txtRut.text = getUsuarioId(this)
        setUserPhoto()
    }

    private fun setUserPhoto() {
        var urlPhoto = "%suser/%s/foto"
        urlPhoto =
            String.format(urlPhoto, getString(R.string.ws_url_mensajeria), getUsuarioId(this))
        GlideApp.with(this)
            .load(urlPhoto)
            .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
            .error(R.drawable.ic_account_circle_materialgrey_240dp)
            .circleCrop()
            .into(binding.includeheader.imgProfile)
    }

    private fun observeLastPreaccess() {
        cabeceraViewModel.lastPreaccess.observe(this) {
            if (it != null) {
                preacceso.divisionNombre = it.divisionNombre
                preacceso.division = it.division
                preacceso.localNombre = it.localNombre
                preacceso.local = it.local
                binding.ddlDivision.setText(it.divisionNombre, false)
                binding.txtPatente.setText(it.patente)
                binding.ddlLocal.setText(it.localNombre, false)
                if (it.sentido == "IN")
                    binding.ddlSentido.setText("INGRESO", false)
                else
                    binding.ddlSentido.setText("SALIDA", false)
                binding.check.isEnabled = true
                binding.check.background =
                    ContextCompat.getDrawable(this@CabeceraActivity, R.drawable.round_bottom)

                if (binding.radioButtonYes.isChecked) {
                    it.passengers = "SI"
                } else if (binding.radioButtonNo.isChecked) {
                    it.passengers = "NO"
                }

                cabeceraViewModel.getLocals(
                    LocalRequest(
                        divisionId = preacceso.division,
                        localType = Constants.LOCAL_TYPE_INGRESO
                    )
                )

            }
        }
    }

    fun textChangedListeners() {
        binding.txtPatente.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (preacceso.division != null) {
                    if (s.isNotEmpty() && preacceso.division.isNotEmpty()) {
                        if (s.toString().contains("-") ||
                            s.toString().contains(".") ||
                            s.toString().contains(" ")) {
                            binding.tilPatente.isErrorEnabled = true
                            binding.tilPatente.error = "No debe ingresar caracteres especiales ni espacios"
                            binding.tilPatente.errorIconDrawable = null
                            binding.check.isEnabled = false
                            binding.check.background = ContextCompat.getDrawable(
                                this@CabeceraActivity,
                                R.drawable.round_bottom_disabled
                            )

                        } else {
                            binding.tilPatente.isErrorEnabled = false
                            binding.tilPatente.error = null
                            binding.check.isEnabled = true
                            binding.check.background =
                                ContextCompat.getDrawable(
                                    this@CabeceraActivity,
                                    R.drawable.round_bottom
                                )
                        }
                    } else {
                        binding.check.isEnabled = false
                        binding.check.background = ContextCompat.getDrawable(
                            this@CabeceraActivity,
                            R.drawable.round_bottom_disabled
                        )
                    }
                }
                isAuthorized = false
                isParticularVehicle = false
                binding.animationView.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun validateChecklists() {
        cabeceraViewModel.getChecklist(
            type = ChecklistType.TFS,
            workerId = getUsuarioId(this),
            vehicleId = binding.txtPatente.text.toString(),
            date = wCDate
        )
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val dateBefore = SharedUtils.getStringFromDate(calendar.time)

        cabeceraViewModel.getChecklist(
            type = ChecklistType.TDV,
            workerId = getUsuarioId(this),
            vehicleId = binding.txtPatente.text.toString(),
            date = dateBefore
        )
        cabeceraViewModel.getChecklistDetail(
            idDb = currentIdDb
        )

    }

    private fun showValidationError() {
        isAuthorized = false
        binding.check.background = ContextCompat.getDrawable(this@CabeceraActivity, R.drawable.round_bottom_red)
        binding.animationView.setAnimation(R.raw.spinner_error)
        binding.txtPatente.isEnabled = true
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
    }

    fun verificarRutaDia() {
        val api = buildAnglo()
        val call = api.getRutaDia(
            object : HashMap<String?, String?>() {
                init {
                    put("Patente", binding.txtPatente.text.toString())
                    put("DivisionId", preacceso.division)
                    put("InitialDate", SimpleDateFormat("yyyyMMdd").format(Date()))
                    //put("InitialDate", "20200218");
                    put(
                        "Movimiento",
                        if (binding.ddlSentido.text.toString()
                                .compareTo("INGRESO") == 0
                        ) "IN" else "OUT"
                    )
                }
            }
        )
        call.enqueue(object : Callback<ApiResponseAnglo<List<Ruta>>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<List<Ruta>>>,
                response: Response<ApiResponseAnglo<List<Ruta>>>
            ) {
                if (preacceso.local != "DISIPADORA") {
                    if (response.isSuccessful) {
                        if (response.body()!!.isSuccess) {
                            if (response.body()!!.data.isNotEmpty()) {
                                if (response.body()!!.data[0].authorized.compareTo("SI") == 0) {
                                    showOk()
                                } else {
                                    showToast(this@CabeceraActivity, "Acceso ruta no autorizada")
                                    showValidationError()
                                }
                            } else {
                                showToast(this@CabeceraActivity, "Acceso ruta no encontrada")
                                showValidationError()
                            }
                        } else {
                            showToast(this@CabeceraActivity, "Acceso ruta no encontrada")
                            showValidationError()
                        }
                    }
                } else {
                    showOk()
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<List<Ruta>>>, t: Throwable) {
                t.printStackTrace()
                binding.check.isEnabled = true
                binding.check.visibility = View.VISIBLE
                binding.tilPatente.isEnabled = true
                Snackbar.make(binding.view, "Error de red", Snackbar.LENGTH_LONG)
                    .setAction("Reintentar") {
                        verificarRutaDia()
                    }.show()
            }
        })
    }

    fun showOk() {
        isAuthorized = true
        binding.check.background =
            ContextCompat.getDrawable(this@CabeceraActivity, R.drawable.round_bottom_green)
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

        showToast(this@CabeceraActivity, "Patente verificada")
    }

    private fun checkWorkerAuthorized() {
        if (worker != null && worker!!.autor && (isParticularVehicle || worker!!.driver)) {
            when (client) {
                Companies.ANGLO.valor -> {
                    val hours: Int = timeServer.split(":")[0].toInt()
                    if (!worker!!.validated) {
                        showUnaccredited()
                    } else {
                        if (isParticularVehicle) {
                            if (hours >= 18 && binding.ddlSentido.text.toString() == "INGRESO") {
                                isParticularPreAccess = true
                                MaterialDialog.Builder(this)
                                    .title("AVISO")
                                    .content(R.string.Mensaje_vehiculo_particular)
                                    .positiveText("Aceptar")
                                    .autoDismiss(true)
                                    .onPositive { _, _ -> savePreaccess() }
                                    .show()
                            } else {
                                savePreaccess()
                            }
                        } else {
                            if (validateLicence(worker!!)) {
                                savePreaccess()
                            } else {
                                if (worker!!.licencias.isNullOrEmpty()) {
                                    showInvalidLicence("No cuenta con alguna Licencia en esta division")
                                } else {
                                    showInvalidLicence("No puede conducir este vehiculo con Licencia: ${worker!!.licencias}")
                                }
                            }
                        }
                    }
                }

                Companies.CH.valor -> {
                    savePreaccess()
                }
            }
        } else {
            showError()
        }
    }

    private fun validateLicence(worker: WorkerAnglo): Boolean {
        vehicleLicences = vehiculo!!.licencias
        var tieneLicencia: Boolean = false
        if (!worker.licencias.isNullOrEmpty()) {
            if (vehicleLicences != null) {
                if (vehicleLicences!!.isNotEmpty()) {
                    val licenciasArr = vehicleLicences!!.splitToSequence(",")
                    for (item in licenciasArr) {
                        if (worker.licencias != null) {
                            if (worker.licencias!!.contains(item)) {
                                tieneLicencia = true
                                break
                            }
                        } else {
                            tieneLicencia = false
                        }
                    }
                } else {
                    tieneLicencia = false
                }
            } else {
                tieneLicencia = false
            }
        } else {
            tieneLicencia = false
        }
        return tieneLicencia
    }

    private fun savePreaccess() {
        preacceso.fecha = wCDate
        preacceso.hora = time
        preacceso.conductor = getUsuarioId(this)
        preacceso.patente = binding.txtPatente.text.toString().toUpperCase(Locale.getDefault())
        if (binding.ddlSentido.text.toString() == "INGRESO") {
            preacceso.sentido = "IN"
            when (client) {
                Companies.ANGLO.valor -> {
                    if (isParticularPreAccess) {
                        val date = Date()
                        val currentDate = Date(date.time + (1000 * 60 * 60 * 24))
                        val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
                        preacceso.fecha = dateFormat.format(currentDate)
                        preacceso.hora = "00:01"
                    }
                }
            }
        } else {
            preacceso.sentido = "OUT"
        }
        if (binding.radioButtonYes.isChecked) {
            preacceso.passengers = "SI"
        } else if (binding.radioButtonNo.isChecked) {
            preacceso.passengers = "NO"
        }
        preacceso.estado = SyncStatus.P.toString()
        preacceso.createdAt = System.currentTimeMillis().toString()
        val viaje = String.format(
            Constants.TRIPID_FORMAT,
            preacceso.fecha,
            preacceso.hora.replace(":", ""),
            preacceso.patente
        )
        preacceso.viaje = viaje

        if (shouldInsertParkingUsage) {
            insertParkingUsage()
        }

    }

    private fun goToPassengerActivity(preaccessId: Long) {
        when (client) {
            Companies.ANGLO.valor -> {
                var checkList = "TPA"
                var nomCheckList = "Test de PreAcceso"
                if (isParticularPreAccess) {
                    checkList = "VTP"
                    nomCheckList = "Validacion Test de PreAcceso"
                }
                val preaccessIdAux = if (preaccessId.toString().isNullOrEmpty())
                    0
                else
                    preaccessId.toInt()
                val intent = Intent(this@CabeceraActivity, PasajeroActivity::class.java)
                intent.putExtra("PREACCESS_ID", preaccessIdAux)
                intent.putExtra("CLIENT", Companies.ANGLO.valor)
                intent.putExtra("IS_PARTICULAR", isParticularPreAccess)
                intent.putExtra("ID_GUIA", uuid)
                startActivity(intent)
                finish()
            }

            Companies.CH.valor -> {
                val intent = Intent(this@CabeceraActivity, PasajeroActivity::class.java)
                intent.putExtra(PREACCESS_ID, preaccessId.toInt())
                intent.putExtra(ControlPreaccesoFragment.CLIENT, client)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun validateDat(fecha: String): String {
        try {
            val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            return dateFormat.parse(fecha).time.toString()
        } catch (e: Exception) {
            return ""
        }
    }

    private fun savePreaccessDetail(id: Long) {
        val chofer = PreaccesoDetalle(
            id.toInt(),
            preacceso.conductor,
            preacceso.hora,
            worker!!.companiaId,
            worker!!.companiaNombre,
            error,
            worker!!.centroCosto,
            worker!!.ost,
            worker!!.tipoPase,
            SyncStatus.P.toString(),
            validateDat(preacceso.fecha),
            "",
            worker!!.nombre,
            worker!!.apellidos,
            worker!!.autor,
            "S", "",
            worker!!.validated ?: true
        )
        cabeceraViewModel.insertPreaccessDetail(chofer)
    }

    private fun savePreaccessTest() {
        val checkListTest = CheckListTest()
        checkListTest.workerId = worker?.id ?: ""
        checkListTest.fechaSubmit = wCDate
        checkListTest.horaSubmit = time
        checkListTest.tipoTest = ChecklistType.TPA.name
        when (client) {
            Companies.ANGLO.valor -> {
                if (isParticularPreAccess) {
                    checkListTest.tipoTest = ChecklistType.VTP.name
                }
            }
        }
        checkListTest.estadoInterno = 0
        checkListTest.divisionId = preacceso.division
        checkListTest.companyId = worker!!.companiaId
        checkListTest.vehicleId = binding.txtPatente.text.toString().uppercase(Locale.getDefault())
        checkListTest.divisionName = preacceso.divisionNombre
        checkListTest.localId = preacceso.local
        checkListTest.localName = preacceso.localNombre
        cabeceraViewModel.insertChecklistTest(checkListTest)
    }

    private fun showError() {
        showValidationError()
        MaterialDialog.Builder(this)
            .title("Error")
            .content("Conductor no autorizado.")
            .positiveText("Aceptar")
            .autoDismiss(true)
            .show()
    }

    private fun showUnaccredited() {
        MaterialDialog.Builder(this)
            .title("Error")
            .content("Su usuario no se registra como acreditado para esta Division.")
            .positiveText("Aceptar")
            .autoDismiss(true)
            .show()
    }

    private fun showInvalidLicence(licence: String) {
        MaterialDialog.Builder(this)
            .title("Error")
            .content("$licence")
            .positiveText("Aceptar")
            .autoDismiss(true)
            .show()
    }

    val isValid: Boolean
        get() {
            var counter = 0
            if (binding.txtPatente.text.isNullOrEmpty()) {
                binding.tilPatente.error = "Campo obligatorio"
                counter++
            }
            if (binding.ddlDivision.text.isEmpty()) {
                binding.mySpinnerDivision.error = "Campo obligatorio"
                counter++
            }
            if (binding.ddlLocal.text.isEmpty()) {
                binding.mySpinnerLocal.error = "Campo obligatorio"
                counter++
            }
            if (binding.ddlSentido.text.isEmpty()) {
                binding.mySpinnerSentido.error = "Campo obligatorio"
                counter++
            }
            if (binding.etUpdateFile.text.isEmpty() and activeDispatch == true) {
                binding.tilUpdateFile.error = "Campo obligatorio"
                counter++
            }
            if (!binding.radioButtonYes.isChecked and !binding.radioButtonNo.isChecked) {
                MaterialDialog.Builder(this)
                    .title("Alerta")
                    .content(R.string.txt_alert)
                    .positiveText("Aceptar")
                    .autoDismiss(true)
                    .show()
                counter++
            }
            if (!isAuthorized) {
                counter++
            }
            return counter == 0
        }

    companion object {
        private val SENTIDO = arrayOf("INGRESO", "SALIDA")
        const val PREACCESS_ID = "PREACCESS_ID"
        const val IS_PARTICULAR = "IS_PARTICULAR"
        const val ID_GUIA = "ID_GUIA"
    }
}