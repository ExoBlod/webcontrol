package com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMinePassengers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.zxing.integration.android.IntentIntegrator
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.data.model.WorkerAnglo
import com.webcontrol.angloamerican.data.model.WorkerRequest
import com.webcontrol.angloamerican.databinding.FragmentPreaccessMinePassengersBinding
import com.webcontrol.angloamerican.ui.preaccessmine.PreAccessMineViewModel
import com.webcontrol.angloamerican.ui.preaccessmine.adapters.PreAccesoMinePassengersAdapter
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.common.widgets.LoadingView
import com.webcontrol.core.utils.SharedUtils
import com.webcontrol.core.utils.SharedUtils.isOnline
import com.webcontrol.core.utils.SharedUtils.time
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PreAccessMinePassengersFragment :
    BaseFragment<FragmentPreaccessMinePassengersBinding, PreAccessMinePassengersViewModel>(),
    PreAccesoMinePassengersAdapter.OnButtonClickListener {
    private lateinit var callback: OnBackPressedCallback
    private var lastPreaccess: PreaccesoMina? = null
    private lateinit var preaccesoDetalle: PreaccesoDetalleMina
    private var actualPassenger: WorkerAnglo? = null
    private var pasajerosNoAut = false
    private var isCorrect = false
    var txtNumeroRegistros: TextView? = null
    private lateinit var preaccesoDetalleList: List<PreaccesoDetalleMina>
    private val adapter: PreAccesoMinePassengersAdapter by lazy {
        PreAccesoMinePassengersAdapter(mutableListOf(), this@PreAccessMinePassengersFragment)
    }
    private var uuid = ""
    private lateinit var loader: LoadingView

    private val parentViewModel: PreAccessMineViewModel by activityViewModels()
    override fun getViewModelClass() = PreAccessMinePassengersViewModel::class.java
    override fun getViewBinding() = FragmentPreaccessMinePassengersBinding.inflate(layoutInflater)

    private var idWorker: String? = ""
    override fun setUpViews() {
        super.setUpViews()
        preaccesoDetalleList = ArrayList()
        preaccesoDetalle = PreaccesoDetalleMina()
        binding.listaPasajeros.layoutManager = LinearLayoutManager(requireContext())
        binding.listaPasajeros.adapter = adapter
    }

    override fun onButtonClick(data: PreaccesoDetalleMina) {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showConfirmationDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listHistory.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    PreAccessMinePassengersUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo historico")
                    }

                    PreAccessMinePassengersUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }

                    is PreAccessMinePassengersUIEvent.Error -> {

                    }

                    is PreAccessMinePassengersUIEvent.SuccessWorkerInfo -> {
                        actualPassenger = event.workerInfo

                        if (adapter.rutsMostrados.contains(actualPassenger!!.id)) {
                            MaterialDialog.Builder(requireContext())
                                .title("Trabajador ya ingresado")
                                .positiveText(getString(R.string.buttonOk))
                                .autoDismiss(true)
                                .show()
                        }
                        else if (actualPassenger!!.nombre.isEmpty()) {
                            MaterialDialog.Builder(requireContext())
                                .title("Ingrese ID correcto")
                                .positiveText(getString(R.string.buttonOk))
                                .autoDismiss(true)
                                .show()
                        }
                        else if (!actualPassenger!!.autor) {
                            MaterialDialog.Builder(requireContext())
                                .title(getString(R.string.no_authorized))
                                .content(actualPassenger!!.stateCertification)
                                .positiveText(getString(R.string.buttonOk))
                                .autoDismiss(true)
                                .show()
                            actualPassenger
                            guardar()
                        } else {
                            guardar()
                        }
                        binding.txtNumeroRegistros.text = adapter.rutsMostrados.size.toString()
                    }

                    is PreAccessMinePassengersUIEvent.SuccessInsertPreaccessDetailMine -> {
                        adapter.addPreAccessDetailMine(preaccesoDetalle)
                        adapter.notifyItemInserted(adapter.itemCount)
                        if (adapter.itemCount > 0) {
                            binding.listaPasajeros.visibility = View.VISIBLE
                            binding.emptyState.visibility = View.GONE
                        }
                        binding.editRut.setText("")
                    }
                }
            }
        }
        setRecyclerView()
        setUIListeners()
    }

    private fun setUIListeners() {
        checkPassenger()
        launchScanner()
        retry()
        finishPreaccess()
    }

    private fun retry() {
        binding.btnInternet.setOnClickListener {
            verificarConexion()
        }
    }

    private fun launchScanner() {
        binding.scan.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Escanea un código QR")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }
    }

    private fun checkPassenger() {
        binding.check.setOnClickListener {
            if (binding.editRut.text.toString()
                    .equals(parentViewModel.workerId.workerId, ignoreCase = true)
            ) {
                MaterialDialog.Builder(requireContext())
                    .title(getString(R.string.error))
                    .content(R.string.Mensaje_conductor_pasajero)
                    .positiveText(getString(R.string.buttonOk))
                    .autoDismiss(true)
                    .show()
            } else {
                viewModel.getWorkerInfo(
                    WorkerRequest(
                        workerId = binding.editRut.text.toString(),
                        divisionId = lastPreaccess?.division
                    )
                )

            }
        }
    }

    private fun setRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))
        binding.listaPasajeros.addItemDecoration(divider)
        binding.listaPasajeros.layoutManager = layoutManager
    }

    private fun verificarConexion(): Boolean {
        return if (!isOnline(requireContext())) {
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
                requireContext(),
                R.drawable.ic_check_circle_green_24dp
            )
        )
        preaccesoDetalle.preaccesoId = parentViewModel.preAccessMineId.toInt()
        preaccesoDetalle.estado = STATUS_P
        preaccesoDetalle.hora = time
        preaccesoDetalle.createdAt = System.currentTimeMillis().toString()
        preaccesoDetalle.updatedAt = ""
        preaccesoDetalle.vehiculo = "N"
        binding.editRut.setText("")
        if (preaccesoDetalleList.find { it.rut == preaccesoDetalle.rut } == null) {
            viewModel.insertPreaccessDetail(preaccesoDetalle)
        } else {
            MaterialDialog.Builder(requireContext())
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
                        showToast(requireContext(), getString(R.string.identifier_not_received))
                    }
                } else {
                    showToast(requireContext(), getString(R.string.code_not_scanned))
                }
            }
            CHECKLIST_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val respuesta = data?.getStringExtra("Ok") ?: ""
                    if (respuesta.isNotEmpty()) {
                        guardar()
                    }
                } else {
                    showToast(requireContext(), getString(R.string.passenger_not_allowed))
                }
            }
        }
    }

    fun showToast(context: Context, message: String? = "Desconocido") {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun enviar() {
            viewModel.postReservationPreaccessDetailMine()
            viewModel.postReservationPreaccessMine()
    }

    private fun showConfirmationDialog() {
        MaterialDialog.Builder(requireContext())
            .title(getString(R.string.confirmation))
            .content(R.string.Mensaje_preacceso_incompleto)
            .positiveText(getString(R.string.yes))
            .negativeText(getString(R.string.no))
            .autoDismiss(true)
            .cancelable(false)
            .onPositive { dialog, _ ->
                viewModel.deletePreaccess(parentViewModel.preAccessMineId.toInt())
                findNavController().popBackStack()
                dialog.dismiss()
            }
            .onNegative { dialog, _ ->
                dialog.dismiss()
            }
            .dismissListener {
                callback.isEnabled = true
            }
            .show()
    }

    private fun finishPreaccess() {
        binding.btnFinalizar.setOnClickListener{
            if (verificarConexion()) {
                MaterialDialog.Builder(requireContext())
                    .title(getString(R.string.warning))
                    .content(R.string.Mensaje_finalizacion)
                    .positiveText(getString(R.string.yes))
                    .negativeText(getString(R.string.no))
                    .autoDismiss(true)
                    .onPositive { dialog, _ ->
                        enviar()
                        dialog.dismiss()
                            findNavController().navigate(R.id.action_preAccessMinePassengers_to_historyPreaccesMine)
                    }
                    .onNegative { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

                if (pasajerosNoAut) {
                    MaterialDialog.Builder(requireContext())
                        .title(getString(R.string.atencion_titulo))
                        .content(R.string.Mensaje_pasajero_autorizacion)
                        .positiveText(getString(R.string.buttonOk))
                        .autoDismiss(true)
                        .onPositive { dialog, _ ->
                            mensajeFinalizar()
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }

    private fun mensajeFinalizar() {
        MaterialDialog.Builder(requireContext())
            .title(getString(R.string.warning))
            .content(R.string.Mensaje_finalizacion)
            .positiveText(getString(R.string.yes))
            .negativeText(getString(R.string.no))
            .autoDismiss(true)
            .onPositive { dialog, _ ->
                enviar()
                dialog.dismiss()
                findNavController().navigate(R.id.action_preAccessMinePassengers_to_historyPreaccesMine)
            }
            .onNegative { dialog, _ ->
                dialog.dismiss()
            }
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


    fun getDate(pattern: String? = "yyyy-MM-dd"): String {
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.US)
        val date = Date()
        return dateFormat.format(date)
    }

    companion object {
        const val CHECKLIST_ACTIVITY_REQUEST_CODE = 0
        const val SCANNER_ACTIVITY_REQUEST_CODE = 100
        const val STATUS_P = "P"
        const val STATUS_S = "S"
    }
}
