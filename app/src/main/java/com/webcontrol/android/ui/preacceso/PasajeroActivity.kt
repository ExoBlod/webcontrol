package com.webcontrol.android.ui.preacceso

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Preacceso
import com.webcontrol.android.data.db.entity.PreaccesoDetalle
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.network.WorkerRequest
import com.webcontrol.android.databinding.FragmentPreaccesoPasajerosBinding
import com.webcontrol.android.ui.common.LoadingView
import com.webcontrol.android.ui.common.adapters.PreaccesoPasajerosAdapter
import com.webcontrol.android.ui.settings.ScannerActivity
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.DateUtils.getDate
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.isOnline
import com.webcontrol.android.util.SharedUtils.showToast
import com.webcontrol.android.util.SharedUtils.time
import com.webcontrol.android.util.SyncStatus
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@AndroidEntryPoint
class PasajeroActivity : AppCompatActivity() {
    private lateinit var binding: FragmentPreaccesoPasajerosBinding
    private val pasajeroViewModel by viewModel<PassengerViewModel> { parametersOf(client) }
    private var lastPreaccess: Preacceso? = null
    private var actualPassenger: WorkerAnglo? = null
    private lateinit var loader: LoadingView
    private lateinit var preaccesoDetalleList: List<PreaccesoDetalle>
    private var pasajerosAdapter: PreaccesoPasajerosAdapter? = null
    private var id: Int = 0
    private lateinit var client: String
    private lateinit var preaccesoDetalle: PreaccesoDetalle
    private var isCorrect = false
    private var pasajerosNoAut = false
    private var isParticularPreAccess = false
    private var uuid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPreaccesoPasajerosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.fragment_preacceso_pasajeros)
        loader = LoadingView(this)
        preaccesoDetalleList = ArrayList()
        preaccesoDetalle = PreaccesoDetalle()
        id = intent.getIntExtra(CabeceraActivity.PREACCESS_ID, 0)
        client = intent.getStringExtra(ControlPreaccesoFragment.CLIENT)!!
        isParticularPreAccess = intent.getBooleanExtra(CabeceraActivity.IS_PARTICULAR, false)
        uuid = intent.getStringExtra(CabeceraActivity.ID_GUIA)!!
        setRecyclerView()
        setObservers()
        setUIListeners()
    }

    private fun setUIListeners() {
        checkPassenger()
        launchScanner()
        finishPreaccess()
        retry()
    }

    private fun retry() {
        binding.btnInternet.setOnClickListener {
            verificarConexion()
        }
    }

    private fun finishPreaccess() {
        binding.btnFinalizar.setOnClickListener {
            if (verificarConexion()) {
                MaterialDialog.Builder(this@PasajeroActivity)
                    .title(getString(R.string.warning))
                    .content(R.string.Mensaje_finalizacion)
                    .positiveText(getString(R.string.yes))
                    .negativeText(getString(R.string.no))
                    .autoDismiss(true)
                    .onPositive { _, _ -> enviar() }
                    .show()

                if (pasajerosNoAut) {
                    MaterialDialog.Builder(this)
                        .title(getString(R.string.atencion_titulo))
                        .content(R.string.Mensaje_pasajero_autorizacion)
                        .positiveText(getString(R.string.buttonOk))
                        .autoDismiss(true)
                        .onPositive { _, _ -> mensajeFinalizar() }
                        .show()
                }

            }
        }
    }

    private fun mensajeFinalizar() {
        MaterialDialog.Builder(this@PasajeroActivity)
            .title(getString(R.string.warning))
            .content(R.string.Mensaje_finalizacion)
            .positiveText(getString(R.string.yes))
            .negativeText(getString(R.string.no))
            .autoDismiss(true)
            .onPositive { _, _ -> enviar() }
            .show()
    }

    private fun launchScanner() {
        binding.scan.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            startActivityForResult(intent, 100)
        }
    }

    private fun checkPassenger() {
        binding.check.setOnClickListener {
            if (binding.editRut.text.toString().equals(getUsuarioId(this), ignoreCase = true)) {
                MaterialDialog.Builder(this)
                    .title(getString(R.string.error))
                    .content(R.string.Mensaje_conductor_pasajero)
                    .positiveText(getString(R.string.buttonOk))
                    .autoDismiss(true)
                    .show()
            } else {
                if (isParticularPreAccess) {
                    MaterialDialog.Builder(this)
                        .title(getString(R.string.warning))
                        .content(R.string.Mensaje_vehiculo_particular)
                        .positiveText(getString(R.string.buttonOk))
                        .autoDismiss(true)
                        .onPositive { _, _ ->
                            pasajeroViewModel.getWorkerInfo(
                                WorkerRequest(
                                    workerId = binding.editRut.text.toString(),
                                    divisionId = lastPreaccess?.division
                                )
                            )
                        }
                        .show()
                } else {
                    pasajeroViewModel.getWorkerInfo(
                        WorkerRequest(
                            workerId = binding.editRut.text.toString(),
                            divisionId = lastPreaccess?.division
                        )
                    )
                }
            }
        }
    }

    private fun setObservers() {
        observePreaccessDetailList()
        observeLastPreaccess()
        observeWorkerInfo()
        observePreaccessRegList()
        observeSendPreaccessList()
    }

    private fun observePreaccessRegList() {
        pasajeroViewModel.preaccessRegList.observe(this) {
            if (it.isNotEmpty()) {
                for (i in it.indices){
                    it[i].idGuia = uuid
                    it[i].date =getDate("yyyyMMdd")
                }
                pasajeroViewModel.sendPreaccessRegList(it)
            }
        }
    }

    private fun observeSendPreaccessList() {
        pasajeroViewModel.sendPreaccessState().observe(this) {
            if (it.isLoading) {
                loader.show()
            } else {
                loader.dismiss()
                if (it.data != null && it.error == null) {
                    if (it.data.isSuccess) {
                        showToast(
                            this@PasajeroActivity,
                            getString(R.string.preaccess_record_successfully_inserted)
                        )
                        pasajeroViewModel.updatePreaccessStatus(id)
                        finish()
                    } else {
                        MaterialDialog.Builder(this@PasajeroActivity)
                            .title(getString(R.string.warning))
                            .content(R.string.Mensaje_preacceso_no)
                            .positiveText(getString(R.string.buttonOk))
                            .autoDismiss(true)
                            .show()
                    }
                } else if (it.data == null && it.error != null) {
                    MaterialDialog.Builder(this@PasajeroActivity)
                        .title(getString(R.string.warning))
                        .content(R.string.Mensaje_preacceso_no)
                        .positiveText(getString(R.string.retry))
                        .negativeText(getString(R.string.buttonCancel))
                        .onPositive { _, _ -> enviar() }
                        .autoDismiss(true)
                        .show()
                }
            }
        }
    }

    private fun observeWorkerInfo() {
        pasajeroViewModel.workerInfoState().observe(this) {
            if (it.isLoading) {
                loader.show()
            } else {
                loader.dismiss()
                if (it.data != null && it.error == null) {
                    val response = it.data
                    if (response.isSuccess && response.data != null) {
                        actualPassenger = response.data
                        when (client) {
                            Companies.ANGLO.valor -> {
                                var checkList = "TPA"
                                var nomCheckList = "Test de PreAcceso"
                                if (isParticularPreAccess) {
                                    checkList = "VTP"
                                    nomCheckList = "Validacion Test de PreAcceso"
                                }
                                if (actualPassenger!!.stateCertification != null) {
                                    if (actualPassenger!!.stateCertification.length > 0) {
                                        MaterialDialog.Builder(this)
                                            .title(getString(R.string.no_authorized))
                                            .content(actualPassenger!!.stateCertification)
                                            .positiveText(getString(R.string.buttonOk))
                                            .autoDismiss(true)
                                            .show()
                                    }
                                }

                                guardar()
                            }

                            Companies.CH.valor -> {
                                actualPassenger!!.validated = true
                                guardar()
                            }
                        }
                    } else {
                        showToast(this@PasajeroActivity, "RUT incorrecto")
                        binding.iconState.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@PasajeroActivity,
                                R.drawable.ic_cancel_red_24dp
                            )
                        )
                        isCorrect = false
                    }
                } else if (it.data == null && it.error != null) {
                    Snackbar.make(
                        binding.view,
                        getString(R.string.error_query_passenger),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.retry)) { checkPassenger() }.show()
                }
            }
        }
    }

    private fun observeLastPreaccess() {
        pasajeroViewModel.lastPreaccess.observe(this) {
            if (it != null) {
                lastPreaccess = it
            }
        }
    }

    private fun observePreaccessDetailList() {
        pasajeroViewModel.getPreaccessDetailList(id).observe(this) {
            if (it.isEmpty()) {
                binding.listaPasajeros.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
            } else {
                preaccesoDetalleList = it
                binding.listaPasajeros.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
                pasajerosAdapter = PreaccesoPasajerosAdapter(preaccesoDetalleList, this)
                binding.listaPasajeros.adapter = pasajerosAdapter
                binding.txtNumeroRegistros.text = preaccesoDetalleList.size.toString()
            }
        }
    }

    private fun setRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))
        binding.listaPasajeros.addItemDecoration(divider)
        binding.listaPasajeros.layoutManager = layoutManager
    }

    private fun verificarConexion(): Boolean {
        return if (!isOnline(this)) {
            binding.view.visibility = View.GONE
            binding.includeviewoff.visibility = View.VISIBLE
            false
        } else {
            binding.includeviewoff.visibility = View.GONE
            binding.view.visibility = View.VISIBLE
            true
        }
    }

    private fun guardar() {
        preaccesoDetalle.rut = actualPassenger?.id ?: ""
        preaccesoDetalle.ost = actualPassenger?.ost
        preaccesoDetalle.tipoPase = actualPassenger?.tipoPase
        preaccesoDetalle.centroCosto = actualPassenger?.centroCosto
        preaccesoDetalle.companiaId = actualPassenger?.companiaId ?: ""
        preaccesoDetalle.companiaNombre = actualPassenger?.companiaNombre
        preaccesoDetalle.nombreWorker = actualPassenger?.nombre ?: ""
        preaccesoDetalle.apellidoWorker = actualPassenger?.apellidos
        preaccesoDetalle.isAutor = actualPassenger?.autor ?: false
        preaccesoDetalle.isValidated = actualPassenger?.validated ?: true
        preaccesoDetalle.error = validaPassengerAutor()
        preaccesoDetalle.detalleAutor = actualPassenger?.stateCertification ?: ""
        binding.iconState.setImageDrawable(
            ContextCompat.getDrawable(
                this@PasajeroActivity,
                R.drawable.ic_check_circle_green_24dp
            )
        )
        preaccesoDetalle.preaccesoId = id
        preaccesoDetalle.estado = SyncStatus.P.toString()
        preaccesoDetalle.hora = time
        preaccesoDetalle.createdAt = System.currentTimeMillis().toString()
        when (client) {
            Companies.ANGLO.valor -> {
                if (isParticularPreAccess) {
                    val date = Date()
                    val currentDate = Date(date.time + (1000 * 60 * 60 * 24))
                    preaccesoDetalle.createdAt = currentDate.time.toString()
                    preaccesoDetalle.hora = "00:01"
                }
            }
        }
        preaccesoDetalle.updatedAt = ""
        preaccesoDetalle.vehiculo = "N"
        binding.editRut.setText("")
        if (preaccesoDetalleList.find { it.rut == preaccesoDetalle.rut } == null) {
            pasajeroViewModel.insertPreaccessDetail(preaccesoDetalle)
        } else {
            MaterialDialog.Builder(this)
                .title(getString(R.string.warning))
                .content(R.string.Mensaje_rut_repetido)
                .positiveText(getString(R.string.buttonOk))
                .autoDismiss(true)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SCANNER_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val barcode = data?.getStringExtra("codBar") ?: ""
                    if (barcode.isNotEmpty()) {
                        binding.editRut.setText(SharedUtils.getRutString(barcode))
                        checkPassenger()
                    } else {
                        showToast(this, getString(R.string.identifier_not_received))
                    }
                } else {
                    showToast(this, getString(R.string.code_not_scanned))
                }
            }
            CHECKLIST_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val respuesta = data?.getStringExtra("Ok") ?: ""
                    if (respuesta.isNotEmpty()) {
                        guardar()
                    }
                } else {
                    showToast(this, getString(R.string.passenger_not_allowed))
                }
            }
        }
    }

    private fun enviar() {
        pasajeroViewModel.getPreaccessRegList(id)
    }

    override fun onBackPressed() {
        MaterialDialog.Builder(this@PasajeroActivity)
            .title(getString(R.string.confirmation))
            .content(R.string.Mensaje_preacceso_incompleto)
            .positiveText(getString(R.string.yes))
            .negativeText(getString(R.string.no))
            .autoDismiss(false)
            .cancelable(false)
            .onPositive { dialog, _ ->
                dialog.dismiss()
                lastPreaccess?.let { pasajeroViewModel.deletePreaccess(it) }
                super@PasajeroActivity.onBackPressed()
            }
            .onNegative { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun validaPassengerAutor(): String {
        return if (preaccesoDetalle.isAutor) {
            ""
        } else {
            pasajerosNoAut = true
            "189"
        }
    }

    companion object {
        const val CHECKLIST_ACTIVITY_REQUEST_CODE = 0
        const val SCANNER_ACTIVITY_REQUEST_CODE = 100
    }
}