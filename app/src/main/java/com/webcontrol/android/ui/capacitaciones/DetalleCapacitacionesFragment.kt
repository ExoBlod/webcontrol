package com.webcontrol.android.ui.capacitaciones

import android.os.Bundle
import android.util.Log
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
import com.webcontrol.android.data.model.Capacitaciones
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.ReserveCourseRequest
import com.webcontrol.android.databinding.FragmentDetalleCapacitacionesBinding
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
class DetalleCapacitacionesFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentDetalleCapacitacionesBinding


    private var capacitacion: Capacitaciones? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        capacitacion = requireArguments().getSerializable("itemCourse") as Capacitaciones?
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetalleCapacitacionesBinding.inflate(inflater, container, false)

        capacitacion!!.let {
            binding.lblCodeCourse.text = "${it.idCharla} - ${it.charla}"
            binding.txtFechaCurso.text = "Fecha Capacitacion: ${getNiceDate(it.fecha)}"
            binding.txtHoraCurso.text = "Hora Capacitacion: ${it.hora}"
            binding.txtUbicacion.text = "Codigo Division: ${it.divCodigo}"
            binding.txtSala.text = "Division: ${it.division}"


            binding.txtFechaReserva.text = "Fecha Vencimiento: ${getNiceDate(it.vencimiento)}"

            binding.txtTipo.text = "Asistio: ${it.asistio}"
            binding.txtDuracion3.text = "Nota: ${it.nota} "
            binding.txtDuracion2.text = "Estado: ${it.aprobo} "

            if (it.aprobo == "APROBADO") {
                binding.iconState.setImageResource(R.drawable.ic_check_circle_green_24dp)

            } else {
                binding.iconState.setImageResource(R.drawable.ic_cancel_red_24dp)

            }

        } ?: run {
            // La variable capacitacion es null, hacer algo para manejar este caso
            Log.e("NULL", "La variable capacitacion es null")
            return binding.root
        }

        binding.btnRegresar2.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.confirmSessionAbandon()
        return false
    }
}