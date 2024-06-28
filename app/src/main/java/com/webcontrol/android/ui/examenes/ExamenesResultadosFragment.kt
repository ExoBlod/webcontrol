package com.webcontrol.android.ui.examenes

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.databinding.FragmentExamenesResultadosBinding
import com.webcontrol.android.util.SharedUtils.dismissLoader
import com.webcontrol.android.util.SharedUtils.getUsuario
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

@AndroidEntryPoint
class ExamenesResultadosFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentExamenesResultadosBinding
    var sistemaId: String? = null
    var examenesId: String? = null
    var reservaId: String? = null
    var examenReservaId: String? = null
    var cursoId: String? = null
    var totalPreguntas = 0
    var preguntasAcertadas = 0
    var respuesta = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sistemaId = requireArguments().getString("sistemaId")
        examenesId = requireArguments().getString("examenesId")
        reservaId = requireArguments().getString("reservaId")
        examenReservaId = requireArguments().getString("examenReservaId")
        cursoId = requireArguments().getString("cursoId")
        respuesta = requireArguments().getString("respuesta")!!.toInt()
        preguntasAcertadas = requireArguments().getString("numCorrectas")!!.toInt()
        totalPreguntas = requireArguments().getString("numPreguntas")!!.toInt()
        requireActivity().title= "Resultados Examen"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentExamenesResultadosBinding.inflate(inflater, container, false)
        binding.btnRegresar.setOnClickListener {
            btnRegresar()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dismissLoader(context)
        MostrarInformacion()
    }

    private fun MostrarInformacion() {
        try {
            val examen = App.db.examenesDao().getOne(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
            if (respuesta < 3) {
                totalPreguntas = App.db.preguntasDao().getCountPreguntas(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                preguntasAcertadas = App.db.preguntasDao().getCountPreguntasAcertadas(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
            }
            binding.lblNombreUsuarioFr.text = getUsuario(context)
            binding.lblDescripcionFr.text = examen!!.nomExamen
            binding.lblTotalPreguntasFr.text = totalPreguntas.toString() + ""
            binding.lblTotalPreguntasAcertadasFr.text = preguntasAcertadas.toString() + ""
            val resultado = preguntasAcertadas * 100 / totalPreguntas * 1.0
            binding.lblPorcentajeAciertoFr.text = Math.floor(resultado).toInt().toString() + " %"
            val curso = App.db.cursosDao().getOne(sistemaId, cursoId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
            if (curso!!.minAprobacion!!.toDouble() <= resultado) {
                binding.lblMensajeFr.text = "Felicidades usted ha Aprobado este examen."
                binding.lblMensajeFr.setTextColor(ContextCompat.getColor(requireContext(), R.color.successful_exam))
                binding.imgResultadoFr.setImageResource(R.drawable.cup)
                binding.viewKonfettiFr.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED, Color.BLUE)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(3000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(Size(12, 5F))
                        .setPosition(-50f, binding.viewKonfettiFr!!.width + 50f, -50f, -50f)
                        .streamFor(300, 7000L)
            } else {
                binding.lblMensajeFr.text = "Usted ha Reprobado este examen."
                binding.lblMensajeFr.setTextColor(ContextCompat.getColor(requireContext(), R.color.failed_exame))
                binding.imgResultadoFr.setImageResource(R.drawable.bad)
            }
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    fun btnRegresar() {
        findNavController().navigate(R.id.examenesFragment)
    }

    override fun onBackPressed(): Boolean {
        findNavController().navigate(R.id.examenesFragment)
        return false
    }

    companion object {
        const val SISTEMA_ID = "SISTEMA_ID"
        const val EXAMEN_ID = "EXAMEN_ID"
        const val RESERVA_ID = "RESERVA_ID"
        const val EXAMENRESERVA_ID = "EXAMENRESERVA_ID"
        const val CURSO_ID = "CURSO_ID"
        const val RESPUESTA = "RESPUESTA"
        const val NUMCORRECTAS = "NUMCORRECTAS"
        const val NUMPREGUNTAS = "NUMPREGUNTAS"
        fun newInstance(idSistema: String?, IdExamen: String?, IdReserva: String?, idExamenReserva: String?, idCurso: String?, respuesta: String?, numCorrectas: String?, numPreguntas: String?): ExamenesResultadosFragment {
            val fragment = ExamenesResultadosFragment()
            val args = Bundle()
            args.putString(SISTEMA_ID, idSistema)
            args.putString(EXAMEN_ID, IdExamen)
            args.putString(RESERVA_ID, IdReserva)
            args.putString(EXAMENRESERVA_ID, idExamenReserva)
            args.putString(CURSO_ID, idCurso)
            args.putString(RESPUESTA, respuesta)
            args.putString(NUMCORRECTAS, numCorrectas)
            args.putString(NUMPREGUNTAS, numPreguntas)
            fragment.arguments = args
            return fragment
        }
    }
}