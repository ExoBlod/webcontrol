package com.webcontrol.android.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Division
import com.webcontrol.android.data.network.AttendanceRequest
import com.webcontrol.android.data.network.AttendanceResponse
import com.webcontrol.android.databinding.FragmentAttendanceBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttendanceFragment: Fragment() {
    private lateinit var binding: FragmentAttendanceBinding
    private lateinit var viewModel: AttendanceViewModel
    private var divisionList: ArrayList<Division> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AttendanceViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupUI()
        getData()
    }

    private fun getData() {
        viewModel.getDivisionList(SharedUtils.getUsuarioId(requireContext()))
    }

    private fun setupUI() {
        binding.formContainer.visibility = View.VISIBLE
        binding.resultContainer.visibility = View.GONE

        binding.cboDivision.setOnItemClickListener { _, _, position, _ ->
            SharedUtils.setBarrickDivisionPref(requireContext(), divisionList[position].id)
        }

        binding.lblUsername.text = SharedUtils.getUsuario(requireContext())
        binding.lblRut.text = SharedUtils.getUsuarioId(requireContext())

        var urlPhoto = "%suser/%s/foto"
        urlPhoto = String.format(urlPhoto, getString(R.string.ws_url_mensajeria), SharedUtils.getUsuarioId(requireContext()))
        GlideApp.with(this)
                .load(urlPhoto)
                .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
                .error(R.drawable.ic_account_circle_materialgrey_240dp)
                .circleCrop()
                .into( binding.imgUser)

        binding.btnIngreso.setOnClickListener {
            sendAttendance("INGRE")
        }
        binding.btnSalida.setOnClickListener {
            sendAttendance("SALID")
        }
        binding.btnBack.setOnClickListener {
            binding.lblResultInout.text = ""
            binding.lblResultDate.text = ""
            binding.lblResultTime.text = ""
            binding.resultContainer.visibility = View.GONE
            binding.formContainer.visibility = View.VISIBLE
        }
    }

    private fun setupObservers() {
        observeDivisionList()
        observeAttendanceResult()
    }

    private fun observeAttendanceResult() {
        viewModel.attendanceResultState().observe(viewLifecycleOwner) {
            if (it.isLoading) {
                showLoader()
            } else {
                dismissLoader()
                if (it.data != null && it.error == null) {
                    val response = it.data
                    if (response.isSuccess) {
                        val result = response.data
                        showResult(result)
                    } else {
                        SharedUtils.showToast(
                            requireContext(),
                            "Ocurrió un error durante el registro. Por favor intente nuevamente."
                        )
                    }
                } else if (it.data == null && it.error != null) {
                    SharedUtils.showToast(
                        requireContext(),
                        "Ocurrió un error durante el registro. Por favor intente nuevamente."
                    )
                }
            }
        }
    }

    private fun observeDivisionList() {
        viewModel.divisionListState().observe(viewLifecycleOwner) {
            if (it.isLoading) {
                showLoader()
            } else {
                dismissLoader()
                if (it.data != null && it.error == null) {
                    val response = it.data
                    if (response.isSuccess) {
                        if (response.data.isNotEmpty()) {
                            divisionList = response.data
                            val prefDivision = SharedUtils.getBarrickDivisionPref(requireContext())
                            divisionList.find { division -> division.id == prefDivision }
                                ?.let { division ->
                                    binding.cboDivision.setText(division.nombre)
                                }
                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.support_simple_spinner_dropdown_item,
                                divisionList
                            )
                            binding.cboDivision.setAdapter(adapter)
                        }
                    }
                } else if (it.data == null && it.error != null) {
                    SharedUtils.showToast(requireContext(), it.error)
                }
            }
        }
    }

    private fun sendAttendance(inout: String) {
        val prefDivision = SharedUtils.getBarrickDivisionPref(requireContext())
        val division = divisionList.find { it.id == prefDivision }

        if (division != null) {
            SharedUtils.showDialog(
                context = requireContext(),
                title = "CONFIRMAR ${if (inout == "INGRE") "INGRESO" else "SALIDA"}",
                content = "¿Desea guardar su registro de asistencia?",
                positiveText = "ACEPTAR",
                positiveAction = { dialog, _ ->
                    val request = AttendanceRequest (
                            workerId = SharedUtils.getUsuarioId(requireContext()),
                            date = SharedUtils.wCDate,
                            time = SharedUtils.time,
                            divisionId = division.id,
                            country = division.pais,
                            inout = inout
                    )
                    viewModel.registerAttendance(request)
                    dialog.dismiss()
                },
                negativeText = "CANCELAR",
                negativeAction = { dialog, _ ->
                    dialog.dismiss()
                },
                dismiss = false
            )

        } else {
            SharedUtils.showDialog(
                context = requireContext(),
                title = "Alerta",
                content = "Debe escoger la faena para registrar su asistencia.",
                positiveText = "ACEPTAR",
                positiveAction = { dialog, _ ->
                    dialog.dismiss()
                }
            )
        }
    }

    private fun showResult(response: AttendanceResponse) {
        if (response.status) {
            SharedUtils.showDialog(
                context = requireContext(),
                title = "AVISO",
                content = response.result ?: "Sin resultado",
                positiveText = "ACEPTAR",
                positiveAction = { dialog, _ ->
                    dialog.dismiss()
                    prepareResultView(response)
                },
                dismiss = false
            )
        } else {
            SharedUtils.showDialog(
                context = requireContext(),
                title = "ALERTA",
                content = response.result ?: "Sin resultado",
                positiveText = "ACEPTAR",
                positiveAction = { dialog, _ ->
                    dialog.dismiss()
                },
                dismiss = false
            )
        }
    }

    private fun prepareResultView(response: AttendanceResponse) {
        if (response.inout == "INGRE") {
            binding.lblResultInout.text =  "INGRESO"
            binding.lblResultInout.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        } else {
            binding.lblResultInout.text =  "SALIDA"
            binding.lblResultInout.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        }
        response.markDate?.let {
            binding.lblResultDate.text = SharedUtils.parseStringDate(it, "yyyyMMdd", "dd/MM/yyyy")
        }

        binding.lblResultTime.text = response.markTime

        binding.formContainer.visibility = View.GONE
        binding.resultContainer.visibility = View.VISIBLE
    }

    private fun showLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(requireContext(), R.raw.loaddinglottie, "Cargando...", 0, 500, 200)
    }

    private fun dismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(activity)
    }
}