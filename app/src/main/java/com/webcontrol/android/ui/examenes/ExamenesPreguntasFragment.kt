package com.webcontrol.android.ui.examenes

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.Preguntas
import com.webcontrol.android.data.db.entity.Respuestas
import com.webcontrol.android.data.model.ResultadosExamen
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.FragmentExamenesPreguntasBinding
import com.webcontrol.android.util.RestClient.build
import com.webcontrol.android.util.SharedUtils.dismissLoader
import com.webcontrol.android.util.SharedUtils.getIMEI
import com.webcontrol.android.util.SharedUtils.getNiceDate
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.isOnline
import com.webcontrol.android.util.SharedUtils.showLoader
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class ExamenesPreguntasFragment : Fragment(), IOnBackPressed {
    var sistemaId: String? = null
    var examenesId: String? = null
    var reservaId: String? = null
    var examenReservaId: String? = null
    var cursoId: String? = null
    var respuestasList: List<Respuestas?>? = null

    private lateinit var binding: FragmentExamenesPreguntasBinding
    private var preguntasList: List<Preguntas?>? = null
    private var preguntaActual: Preguntas? = null
    private var totalPreguntas = 0
    private var contadorPreguntas = 0
    private var totalPreguntasBD = 0
    private var respondida = false
    private var snackbar: Snackbar? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sistemaId = requireArguments().getString("sistemaId")
        examenesId = requireArguments().getString("examenesId")
        reservaId = requireArguments().getString("reservaId")
        examenReservaId = requireArguments().getString("examenReservaId")
        cursoId = requireArguments().getString("cursoId")
        requireActivity().title="Examen"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentExamenesPreguntasBinding.inflate(inflater, container, false)

        binding.btnSiguienteFp.setOnClickListener {
            btnSiguiente_fp()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MostrarInformacion()
        preguntasList = App.db.preguntasDao().getPreguntasPorExamen(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
        totalPreguntas = preguntasList!!.size
        totalPreguntasBD = App.db.preguntasDao().getCountPreguntas(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
        MostrarSiguientePregunta()
    }

    fun btnSiguiente_fp() {
        try {
            if (!respondida) {
                val IdRespuesta = binding.rgRespuestasFp.checkedRadioButtonId
                if (IdRespuesta > 0) {
                    App.db.respuestasDao().setRespuestaRespondida(sistemaId, examenReservaId!!.toInt(), preguntasList!![contadorPreguntas - 1]!!.idPregunta
                            , examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                            , IdRespuesta)
                    App.db.preguntasDao().setPreguntaRespondida(sistemaId, examenReservaId!!.toInt(), preguntasList!![contadorPreguntas - 1]!!.idPregunta
                            , examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                            , 1
                            , "")
                    MostrarSiguientePregunta()
                } else {
                    Toast.makeText(context, "Seleccione una respuesta", Toast.LENGTH_SHORT).show()
                }
            } else {
                MostrarSiguientePregunta()
            }
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    private fun showError(errorMessage: String) {
        val parentLayout = requireActivity().findViewById<View>(android.R.id.content)
        snackbar = Snackbar.make(parentLayout, errorMessage, Snackbar.LENGTH_LONG)
        snackbar!!.setAction("Reintentar") {
            hideError()
            enviarResultados()
        }
        snackbar!!.show()
    }

    private fun hideError() {
        if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
    }

    private fun MostrarInformacion() {
        try {
            val examen = App.db.examenesDao().getOne(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
            val curso = App.db.cursosDao().getOne(sistemaId, cursoId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
            if (curso != null) binding.lblExpositorFp.text = curso.orador
            binding.lblFechaFp.text = getNiceDate(examen!!.fecha_programada) + " " + examen.hora_programada
            binding.lblTimeCountDownFp.text = examen.tiempoTotal
            IniciarCountDown(examen.tiempoTotal!!)
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    private fun IniciarCountDown(tiempo: String) {
        val units = tiempo.split(":").toTypedArray()
        val horas = units[0].toInt()
        val minutos = units[1].toInt()
        val duration = 3600 * horas + minutos * 60
        countDownTimer = object : CountDownTimer((duration * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.lblTimeCountDownFp.text = ("Restante " + (if (millisUntilFinished / 1000 / 3600 < 10) "0" + millisUntilFinished / 1000 / 3600 else millisUntilFinished / 1000 / 3600)
                        + ":" + (if (millisUntilFinished / 1000 / 60 % 60 < 10) "0" + millisUntilFinished / 1000 / 60 % 60 else millisUntilFinished / 1000 / 60 % 60)
                        + ":" + if (millisUntilFinished / 1000 % 60 < 10) "0" + millisUntilFinished / 1000 % 60 else millisUntilFinished / 1000 % 60)
            }

            override fun onFinish() {
                preguntasList = App.db.preguntasDao().getPreguntasPorExamen(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                for (i in preguntasList!!.indices) App.db.preguntasDao().setPreguntaRespondida(sistemaId, examenReservaId!!.toInt(), preguntasList!![i]!!.idPregunta
                        , examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                        , -1
                        , "")
                if (isOnline(context!!)) {
                    enviarResultados()
                } else {
                    showToast(requireContext(), resources.getString(R.string.without_internet))
                    findNavController().navigate(R.id.examenesFragment)
                }
            }
        }.start()
    }

    private fun enviarResultados() {
        showLoader(context, "Procesando...")
        val totalPreguntas = App.db.preguntasDao().getCountPreguntas(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
        val acertadas = App.db.preguntasDao().getCountPreguntasAcertadas(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
        val api = build(sistemaId!!)
        val resultado = ResultadosExamen(examenReservaId!!.toInt(), reservaId!!.toInt(), examenesId!!.toInt(),
                getUsuarioId(context),
                totalPreguntas,
                acertadas,
                getIMEI(requireContext()),
                "EXAMEN",
                -2
        )
        val respuestasMarcadas = App.db.respuestasDao().getAllRespuestasMarcadas(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
        resultado.setListRespuestas(respuestasMarcadas)
        val call: Call<ApiResponse<Any>> = api.updateResultado(resultado)
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    dismissLoader(context)
                    if (result != null && result.isSuccess) {
                        showToast(requireContext(), "Datos registrados!")
                        findNavController().navigate(R.id.examenesResultadosFragment, bundleOf("sistemaId" to sistemaId,
                        "examenesId" to "0$examenesId","reservaId" to "0$reservaId","examenReservaId" to "0$examenReservaId", "cursoId" to "0$cursoId",
                        "respuesta" to "0","numCorrectas" to "0","numPreguntas" to "0"))
                    } else {
                        showError("Error de conexión")
                    }
                } else {
                    dismissLoader(context)
                    showError("Error de conexión")
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                dismissLoader(context)
                showError(ExamenesPreguntasFragment::class.java.simpleName + " sync() " + "Error de conexión")
            }
        })
    }

    private fun MostrarSiguientePregunta() {
        try {
            if (contadorPreguntas < totalPreguntas) {
                binding.rgRespuestasFp.clearCheck()
                binding.rgRespuestasFp.removeAllViews()
                binding.lblPreguntaFp.text = ""
                preguntaActual = preguntasList!![contadorPreguntas]
                binding.lblPreguntaFp.text = (totalPreguntasBD - totalPreguntas + contadorPreguntas + 1).toString() + ". " + preguntaActual!!.pregunta
                respuestasList = if (preguntaActual!!.respOrdenadas) {
                    App.db.respuestasDao().getRespuestasOrdenadasByPregunta(sistemaId, examenReservaId!!.toInt(), preguntaActual!!.idPregunta
                            , examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                } else {
                    App.db.respuestasDao().getRespuestasByPregunta(sistemaId, examenReservaId!!.toInt(), preguntaActual!!.idPregunta
                            , examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                }
                for (i in respuestasList!!.indices) {
                    val layoutParams = RadioGroup.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)
                    val rdbtn = RadioButton(context)
                    rdbtn.id = respuestasList!![i]!!.idRespuesta
                    rdbtn.text = respuestasList!![i]!!.respuesta
                    layoutParams.setMargins(15, 25, 15, 15)
                    rdbtn.layoutParams = layoutParams
                    binding.rgRespuestasFp.addView(rdbtn)
                }
                contadorPreguntas++
                if (contadorPreguntas < totalPreguntas) {
                    binding.btnSiguienteFp.text = "SIGUIENTE"
                } else {
                    binding.btnSiguienteFp.text = "FINALIZAR"
                }
                binding.lblContadorFp.text = "Pregunta: " + (totalPreguntasBD - totalPreguntas + contadorPreguntas) + "/" + totalPreguntasBD
                respondida = false
            } else {
                if (isOnline(requireContext())) {
                    countDownTimer!!.cancel()
                    enviarResultados()
                } else showToast(requireContext(), resources.getString(R.string.without_internet))
            }
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
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
        fun newInstance(idSistema: String?, IdExamen: String?, IdReserva: String?, IdExamenReserva: String?, IdCurso: String?): ExamenesPreguntasFragment {
            val fragment = ExamenesPreguntasFragment()
            val args = Bundle()
            args.putString(SISTEMA_ID, idSistema)
            args.putString(EXAMEN_ID, IdExamen)
            args.putString(RESERVA_ID, IdReserva)
            args.putString(EXAMENRESERVA_ID, IdExamenReserva)
            args.putString(CURSO_ID, IdCurso)
            fragment.arguments = args
            return fragment
        }
    }
}