package com.webcontrol.android.ui.examenes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.Preguntas
import com.webcontrol.android.data.model.ResultadosExamen
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.FragmentExamenesInicioBinding
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
class ExamenesInicioFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentExamenesInicioBinding
    var sistemaId: String? = null
    var examenesId: String? = null
    var reservaId: String? = null
    var examenReservaId: String? = null
    var cursoId: String? = null
    var totalPreguntas = 0
    private var TAG: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sistemaId = requireArguments().getString("sistemaId")
        examenesId = requireArguments().getString("examenesId")
        reservaId = requireArguments().getString("reservaId")
        examenReservaId = requireArguments().getString("examenReservaId")
        cursoId = requireArguments().getString("cursoId")
        requireActivity().title="Inicio Examen"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentExamenesInicioBinding.inflate(inflater, container, false)
        TAG = ExamenesInicioFragment::class.java.simpleName
        binding.btnIniciar.setOnClickListener {
            btnIniciar()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetch()
    }

    fun btnIniciar() {
        showLoader(context, "Procesando...")
        try {
            if (isOnline(requireContext())) {
                totalPreguntas = App.db.preguntasDao().getCountPreguntas(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                if (totalPreguntas <= 0) {
                    dismissLoader(context)
                    Toast.makeText(context, "Este examen no tiene preguntas", Toast.LENGTH_SHORT).show()
                } else {
                    val api = build(sistemaId!!)
                    val resultado = ResultadosExamen(examenReservaId!!.toInt(), reservaId!!.toInt(), examenesId!!.toInt(),
                            getUsuarioId(context),
                            0,
                            0,
                            getIMEI(requireContext()),
                            "EXAMEN",
                            -2
                    )
                    val call = api.validarResultadoByIMEI(resultado)
                    call.enqueue(object : Callback<ApiResponse<List<ResultadosExamen>>?> {
                        override fun onResponse(call: Call<ApiResponse<List<ResultadosExamen>>?>, response: Response<ApiResponse<List<ResultadosExamen>>?>) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                if (result != null && result.isSuccess) {
                                    if (result.data.size > 0) {
                                        when (result.data[0].respuesta) {
                                            1 -> {
                                                dismissLoader(context)
                                                findNavController().navigate(R.id.examenesPreguntasFragment,
                                                    bundleOf("sistemaId" to sistemaId, "examenesId" to "0$examenesId","reservaId" to "0$reservaId",
                                                    "examenReservaId" to "0$examenReservaId","cursoId" to "0$cursoId"))
                                            }
                                            2 -> {
                                                dismissLoader(context)
                                                Toast.makeText(context, "Este examen esta activo en otro dispositivo", Toast.LENGTH_SHORT).show()
                                            }
                                            3, 4 -> {
                                                dismissLoader(context)
                                                findNavController().navigate(R.id.examenesPreguntasFragment,
                                                    bundleOf("sistemaId" to sistemaId, "examenesId" to "0$examenesId","reservaId" to "0$reservaId",
                                                        "examenReservaId" to "0$examenReservaId","cursoId" to "0$cursoId","respuesta" to "0" + result.data[0].respuesta,
                                                    "numCorrectas" to "0" + result.data[0].numCorrectas,"numPreguntas" to "0" + result.data[0].numPreguntas))
                                            }
                                            5 -> {
                                                dismissLoader(context)
                                                Toast.makeText(context, "Este examen no esta disponible en este momento", Toast.LENGTH_SHORT).show()
                                            }
                                            else -> {
                                                dismissLoader(context)
                                                Toast.makeText(context, "Ha ocurrido un error contactese con el administrador", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        dismissLoader(context)
                                        IniciarExamen()
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse<List<ResultadosExamen>>?>, t: Throwable) {
                            dismissLoader(context)
                            showToast(requireContext(), TAG + " btnIniciar() " + t.message)
                        }
                    })
                }
            } else {
                dismissLoader(context)
                showToast(requireContext(), resources.getString(R.string.without_internet))
            }
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    private fun IniciarExamen() {
        showLoader(context, "Procesando...")
        totalPreguntas = App.db.preguntasDao().getCountPreguntas(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
        val api = build(sistemaId!!)
        val resultado = ResultadosExamen(examenReservaId!!.toInt(), reservaId!!.toInt(), examenesId!!.toInt(),
                getUsuarioId(context),
                totalPreguntas,
                0,
                getIMEI(requireContext()),
                "EXAMEN",
                -2
        )
        val call: Call<ApiResponse<Any>> = api.insertResultado(resultado)
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        if (result.isSuccess) {
                            App.db.examenesDao().setExamenIniciado(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                            dismissLoader(context)
                            findNavController().navigate(R.id.examenesPreguntasFragment,
                                bundleOf("sistemaId" to sistemaId, "examenesId" to "0$examenesId","reservaId" to "0$reservaId",
                                    "examenReservaId" to "0$examenReservaId","cursoId" to "0$cursoId"))
                        } else dismissLoader(context)
                    }
                } else {
                    dismissLoader(context)
                    showToast(requireContext(), response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                dismissLoader(context)
                showToast(requireContext(), TAG + " iniciarExamen() " + t.message)
            }
        })
    }

    fun cargarExamen() {
        try {
            showLoader(context, "Procesando...")
            val examen = App.db.examenesDao().getOne(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                    ?: return
            binding.lblExamen.text = examen.nomExamen
            binding.lblFecha.text = getNiceDate(examen.fecha_programada)
            val curso = App.db.cursosDao().getOne(sistemaId, cursoId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
            if (curso != null) binding.lblOrador.text = curso.orador
            totalPreguntas = App.db.preguntasDao().getCountPreguntas(sistemaId, examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
            binding.lblNumPreguntas.text = "$totalPreguntas preguntas"
            val units = examen.tiempoTotal!!.split(":").toTypedArray()
            val horas = units[0].toInt()
            val minutos = units[1].toInt()
            binding.lblDuracion.text = (if (horas < 10) "0$horas" else horas).toString() + " hras " + (if (minutos < 10) "0$minutos" else minutos) + " min"
            binding.lblDescripcion.text = examen.descExamen
            dismissLoader(context)
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    fun fetch() {
        if (isOnline(requireContext())) sync() else showToast(requireContext(), resources.getString(R.string.without_internet))
    }

    fun sync() {
        try {
            showLoader(context, "Procesando...")
            val api = build(sistemaId!!)
            val call = api.getPreguntas(examenesId)
            call.enqueue(object : Callback<ApiResponse<Preguntas>?> {
                override fun onResponse(call: Call<ApiResponse<Preguntas>?>, response: Response<ApiResponse<Preguntas>?>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            for (i in result.dataNested.indices) {
                                val preg = result.dataNested[i]
                                val existPreg = App.db.preguntasDao().getOne(sistemaId, examenReservaId!!.toInt(), preg.idPregunta
                                        , examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                                if (existPreg != null) {
                                    App.db.preguntasDao().updatePregunta(sistemaId, examenReservaId!!.toInt(), preg.idPregunta
                                            , examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                                            , preg.pregunta, preg.orden!!
                                            , preg.respOrdenadas)
                                    for (j in preg.listRespuestas.indices) {
                                        val resp = preg.listRespuestas[j]
                                        val existResp = App.db.respuestasDao().getOne(sistemaId, examenReservaId!!.toInt(), preg.idPregunta
                                                , examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                                                , resp.idRespuesta
                                        )
                                        if (existResp != null) App.db.respuestasDao().updateRespuesta(sistemaId, examenReservaId!!.toInt(), preg.idPregunta
                                                , examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                                                , resp.idRespuesta
                                                , resp.respuesta, resp.orden!!
                                        ) else {
                                            resp.idSistema = sistemaId!!
                                            resp.idExamenReserva = examenReservaId!!.toInt()
                                            resp.idReserva = reservaId!!.toInt()
                                            resp.idExamen = examenesId!!.toInt()
                                            resp.rut = getUsuarioId(context)
                                            App.db.respuestasDao().insertar(resp)
                                        }
                                    }
                                } else {
                                    preg.idSistema = sistemaId!!
                                    preg.idExamenReserva = examenReservaId!!.toInt()
                                    preg.idReserva = reservaId!!.toInt()
                                    preg.rut = getUsuarioId(context)
                                    App.db.preguntasDao().insertar(preg)
                                    for (j in preg.listRespuestas.indices) {
                                        val resp = preg.listRespuestas[j]
                                        resp.idSistema = sistemaId!!
                                        resp.idExamenReserva = examenReservaId!!.toInt()
                                        resp.idReserva = reservaId!!.toInt()
                                        resp.idExamen = examenesId!!.toInt()
                                        resp.rut = getUsuarioId(context)
                                        App.db.respuestasDao().insertar(resp)
                                    }
                                }
                            }
                            dismissLoader(context)
                            cargarExamen()
                        } else {
                            dismissLoader(context)
                            showToast(requireContext(), response.message())
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Preguntas>?>, t: Throwable) {
                    dismissLoader(context)
                    showToast(requireContext(), TAG + " sync() " + t.message)
                }
            })
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

        fun newInstance(idSistema: String?, IdExamen: String?, IdReserva: String?, IdExamenReserva: String?, IdCurso: String?): ExamenesInicioFragment {
            val fragment = ExamenesInicioFragment()
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