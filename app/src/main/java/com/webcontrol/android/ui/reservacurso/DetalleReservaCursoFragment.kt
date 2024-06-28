package com.webcontrol.android.ui.reservacurso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.ReservaCurso
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.ReserveCourseRequest
import com.webcontrol.android.databinding.FragmentDetalleReservaCursoBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.getNiceDate
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class DetalleReservaCursoFragment: Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentDetalleReservaCursoBinding
    fun nuevo(){
        if (SharedUtils.isOnline(requireContext())){
            if(reservaType == TYPE_COURSE_NO_RESERVADO){
                findNavController().navigate(R.id.reservaCursoFragment)
            } else {
                findNavController().navigate(R.id.historialReservaCursoFragment)
            }
        } else {
            findNavController().navigate(R.id.historialReservaCursoFragment)
        }
    }

    fun pressCancelReserve() {
        MaterialDialog.Builder(requireContext())
            .title("Alerta!")
            .content("Esta seguro de cancelar la Reserva?")
            .positiveText("Aceptar")
            .negativeText("Cancelar")
            .autoDismiss(true)
            .onPositive { dialog, which ->
                cancelReserve()
            }
            .onNegative { dialog, which -> dialog.dismiss() }
            .show()
    }

    fun showMessageNoCancelReserve() {
        MaterialDialog.Builder(requireContext())
            .title("Alerta!")
            .content("No se pudo cancelar Reserva.")
            .positiveText("Aceptar")
            .autoDismiss(true)
            .onPositive { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun cancelReserve() {
        ShowLoader()
        val call: Call<ApiResponseAnglo<String>>
        var request = ReserveCourseRequest(
            reservaCurso!!.codeReserve,
            "${SharedUtils.getUsuarioId(requireContext())}"
        )

        val api = RestClient.buildAnglo()
        call = api.deleteReserve(request)
        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<String>>,
                response: Response<ApiResponseAnglo<String>>
            ) {

                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess && response.body()!!.data == "OK") {
                        DismissLoader()
                        findNavController().navigate(R.id.historialReservaCursoFragment)

                    } else {
                        showMessageNoCancelReserve()
                        DismissLoader()
                    }
                } else {
                    showMessageNoCancelReserve()
                    DismissLoader()
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Ocurrio un error en Cancelar la Reserva.",
                    Toast.LENGTH_SHORT
                ).show()
                DismissLoader()
            }
        })
    }

    private var reservaCurso: ReservaCurso? = null
    private var reservaType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reservaCurso = requireArguments().getSerializable("itemReserva") as ReservaCurso?
        reservaType = requireArguments().getInt("type")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetalleReservaCursoBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.fragment_detalle_reserva_curso, container, false)

        reservaCurso!!.let {
            binding.lblCodeCourse.text = "${it.codeReserve} - ${it.nameCourse}"
            binding.txtFechaCurso.text = "Fecha Curso: ${getNiceDate(it.dateCourse)}"
            binding.txtHoraCurso.text = "Hora Curso: ${it.timeCourse}"

            binding.txtDuracion.text = "Duración: ${it.duration} Hrs."
            binding.txtLugar.text = "Lugar: ${it.place}"
            binding.txtUbicacion.text = "Ubicacion: ${it.location}"
            binding.txtSala.text = "Sala: ${it.sala}"
            binding.txtTipo.text =
                if (it.required == "SI") "Tipo: Obligatorio" else "Tipo: Opcional"

            if(reservaType== TYPE_COURSE_RESERVADO) {
                binding.txtFechaReserva.text = "Fecha Reserva: ${getNiceDate(it.dateReserve)} ${it.timeReserve}"
                if (it!!.statusReserve == "R") {
                    binding.iconState.setImageResource(R.drawable.ic_check_circle_green_24dp)
                    binding.btnCancelarReservar.visibility = View.VISIBLE
                } else {
                    binding.iconState.setImageResource(R.drawable.ic_cancel_red_24dp)
                    binding.btnCancelarReservar.visibility = View.INVISIBLE
                }
            } else {
                binding.iconState.visibility = View.INVISIBLE
                binding.btnCancelarReservar.visibility = View.INVISIBLE
                binding.txtFechaReserva.visibility = View.INVISIBLE
            }
        }
        binding.btnRegresar2.setOnClickListener {
            nuevo()
        }
        binding.btnCancelarReservar.setOnClickListener {
            pressCancelReserve()
        }
        return binding.root
    }

    companion object {
        const val TYPE_COURSE_RESERVADO = 1
        const val TYPE_COURSE_NO_RESERVADO = 0

    }

    fun DismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(context)
    }

    private fun ShowLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(
            context,
            R.raw.loaddinglottie,
            "Cargando...",
            0,
            500,
            200
        )
    }


    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.confirmSessionAbandon()
        return false
    }
}