package com.webcontrol.android.ui.examenes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.Preguntas
import com.webcontrol.android.data.db.entity.Respuestas
import com.webcontrol.android.data.model.ResultadosExamen
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.FragmentExamenesEncuestasBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.RestClient.build
import com.webcontrol.android.util.SharedUtils.dismissLoader
import com.webcontrol.android.util.SharedUtils.getIMEI
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.isOnline
import com.webcontrol.android.util.SharedUtils.showLoader
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@AndroidEntryPoint
class ExamenesEncuestasFragment : Fragment(), RatingDialogListener, IOnBackPressed {
    private lateinit var binding: FragmentExamenesEncuestasBinding
    var sistemaId: String? = null
    var examenReservaId: String? = null
    var examenesId: String? = null
    var reservaId: String? = null
    var encuestaId: String? = null
    var cursoId: String? = null
    var tipoExamen: String? = null
    private var preguntasList: List<Preguntas?>? = null
    var respuestasList: List<Respuestas?>? = null
    private var preguntaActual: Preguntas? = null
    private var totalPreguntas = 0
    private var contadorPreguntas = 0
    private var totalPreguntasBD = 0
    private var respondida = false
    private var snackbar: Snackbar? = null
    private var nomEncuesta: String? = null
    private val TAG: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            sistemaId = requireArguments().getString("idSistema")
            examenesId = requireArguments().getString("idExamen")
            reservaId = requireArguments().getString("idReserva")
            examenReservaId = requireArguments().getString("IdExamenReserva")
            cursoId = requireArguments().getString("IdCurso")
            encuestaId = requireArguments().getString("idEncuesta")
            tipoExamen = requireArguments().getString("Tipo")
            requireActivity().title="Encuesta"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentExamenesEncuestasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val encuesta = App.db.examenesDao().getOne(sistemaId
                , examenReservaId!!.toInt(), examenesId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
        nomEncuesta = if (tipoExamen == "ENCUESTA") encuesta!!.nomExamen else encuesta!!.nomEncuesta
        fetch()
    }

    private fun showError(errorMessage: String) {
        val parentLayout = requireActivity().findViewById<View>(android.R.id.content)
        snackbar = Snackbar.make(parentLayout, errorMessage, Snackbar.LENGTH_INDEFINITE)
        snackbar!!.setAction("Reintentar") {
            hideError()
            enviarResultados()
        }
        snackbar!!.show()
    }

    private fun hideError() {
        if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
    }

    fun fetch() {
        if (isOnline(requireContext())) sync() else showToast(requireContext(), resources.getString(R.string.without_internet))
    }

    fun sync() {
        try {
            showLoader(context, "Procesando...")
            val api = build(sistemaId!!)
            val call = api.getPreguntas(encuestaId)
            call.enqueue(object : Callback<ApiResponse<Preguntas>?> {
                override fun onResponse(call: Call<ApiResponse<Preguntas>?>, response: Response<ApiResponse<Preguntas>?>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.dataNested.size > 0) {
                                for (i in result.dataNested.indices) {
                                    val preg = result.dataNested[i]
                                    val existPreg = App.db.preguntasDao().getOne(sistemaId, examenReservaId!!.toInt(), preg.idPregunta
                                            , encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                                    if (existPreg != null) {
                                        App.db.preguntasDao().updatePregunta(sistemaId, examenReservaId!!.toInt(), preg.idPregunta
                                                , encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                                                , preg.pregunta, preg.orden!!
                                                , preg.respOrdenadas)
                                        for (j in preg.listRespuestas.indices) {
                                            val resp = preg.listRespuestas[j]
                                            val existResp = App.db.respuestasDao().getOne(sistemaId, examenReservaId!!.toInt(), preg.idPregunta
                                                    , preg.idExamen
                                                    , reservaId!!.toInt(), getUsuarioId(context)
                                                    , resp!!.idRespuesta
                                            )
                                            if (resp != null) App.db.respuestasDao().updateRespuesta(sistemaId, examenReservaId!!.toInt(), preg.idPregunta
                                                    , encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                                                    , resp.idRespuesta
                                                    , resp.respuesta, resp.orden!!
                                            ) else {
                                                resp.idSistema = sistemaId!!
                                                resp.idExamenReserva = examenReservaId!!.toInt()
                                                resp.idReserva = reservaId!!.toInt()
                                                resp.idExamen = encuestaId!!.toInt()
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
                                            resp.idExamen = encuestaId!!.toInt()
                                            resp.rut = getUsuarioId(context)
                                            App.db.respuestasDao().insertar(resp)
                                        }
                                    }
                                }
                                dismissLoader(context)
                                IniciarEncuesta()
                            } else {
                                dismissLoader(context)
                                showToast(requireContext(), "Esta encuesta está vacía")
                            }
                        } else {
                            dismissLoader(context)
                            showToast(requireContext(), response.message())
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Preguntas>?>, t: Throwable) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                    dismissLoader(context)
                    showToast(requireContext(), TAG + " sync() " + t.message)
                }
            })
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    fun IniciarEncuesta() {
        try {
            val x = 1
            preguntasList = App.db.preguntasDao().getPreguntasPorEncuesta(sistemaId, examenReservaId!!.toInt(), encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
            totalPreguntas = preguntasList!!.size
            totalPreguntasBD = App.db.preguntasDao().getCountPreguntas(sistemaId, examenReservaId!!.toInt(), encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
            MostrarSiguientePregunta()
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    private fun MostrarSiguientePregunta() {
        try {
            if (contadorPreguntas < totalPreguntas) {
                preguntaActual = preguntasList!![contadorPreguntas]
                respuestasList = App.db.respuestasDao().getAllByRespuestaByPregunta(sistemaId, examenReservaId!!.toInt(), preguntaActual!!.idPregunta
                        , encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
                val respuestas = ArrayList<String>()
                for (i in respuestasList!!.indices) respuestas.add(respuestasList!![i]!!.respuesta!!)
                contadorPreguntas++
                AppRatingDialog.Builder()
                        .setPositiveButtonText(if (contadorPreguntas < totalPreguntas) "SIGUIENTE" else "FINALIZAR")
                        .setNoteDescriptions(respuestas)
                        .setDefaultRating(respuestas.size)
                        .setTitle(nomEncuesta!!)
                        .setDescription(preguntaActual!!.pregunta!!)
                        .setCommentInputEnabled(preguntaActual!!.isSecomenta)
                        .setHint("Escriba su comentario aqui ...")
                        .setCommentBackgroundColor(R.color.colorCommetBackground)
                        .setWindowAnimation(R.style.MyDialogFadeAnimation)
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .create((activity as MainActivity?)!!)
                        .setTargetFragment(this, FRAGMENT_TAG) // only if listener is implemented by fragment
                        .show()
                respondida = false
            } else {
                if (isOnline(requireContext())) {
                    enviarResultados()
                } else {
                    showToast(requireContext(), resources.getString(R.string.without_internet))
                }
            }
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    override fun onNegativeButtonClicked() {}
    override fun onNeutralButtonClicked() {}
    override fun onPositiveButtonClicked(i: Int, s: String) {
        try {
            App.db.respuestasDao().setRespuestaRespondida(sistemaId, examenReservaId!!.toInt(), preguntasList!![contadorPreguntas - 1]!!.idPregunta
                    , encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                    , respuestasList!![i - 1]!!.idRespuesta
            )
            App.db.preguntasDao().setPreguntaRespondida(sistemaId, examenReservaId!!.toInt(), preguntasList!![contadorPreguntas - 1]!!.idPregunta
                    , encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context)
                    , 1
                    , s ?: "")
            MostrarSiguientePregunta()
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    private fun enviarResultados() {
        showLoader(context, "Procesando...")
        val totalPreguntas = App.db.preguntasDao().getCountPreguntas(sistemaId, examenReservaId!!.toInt(), encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
        val api = build(sistemaId!!)
        val resultado = ResultadosExamen(examenReservaId!!.toInt(), reservaId!!.toInt(), encuestaId!!.toInt(),
                getUsuarioId(context),
                totalPreguntas,
                0,
                getIMEI(requireContext()),
                "ENCUESTA",
                -2
        )
        val respuestasMarcadas = App.db.respuestasDao().getAllRespuestasMarcadas(sistemaId, examenReservaId!!.toInt(), encuestaId!!.toInt(), reservaId!!.toInt(), getUsuarioId(context))
        resultado.setListRespuestas(respuestasMarcadas)
        val call: Call<ApiResponse<Any>> = api.updateResultado(resultado)
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                if (response.isSuccessful) {
                    dismissLoader(context)
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        showToast(requireContext(), "Datos registrados!")
                        when (tipoExamen) {
                            "EXAMEN" -> findNavController().navigate(R.id.examenesInicioFragment, bundleOf("sistemaId" to sistemaId,
                                "examenesId" to "0$examenesId","reservaId" to "0$reservaId","examenReservaId" to "0$examenReservaId",
                                "cursoId" to "0$cursoId")
                            )
                            "ENCUESTA" -> findNavController().navigate(R.id.examenesFragment)
                            else -> showToast(requireContext(), "Ha ocurrido un error contactese con el administrador")
                        }
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
                showError("Error de conexión")
                showToast(requireContext(), TAG + " enviarResultados() " + t.message)
            }
        })
    }

    override fun onBackPressed(): Boolean {
        findNavController().navigate(R.id.examenesFragment)
        return false
    }

    companion object {
        const val FRAGMENT_TAG = 4
        const val SISTEMA_ID = "SISTEMA_ID"
        const val EXAMEN_ID = "EXAMEN_ID"
        const val RESERVA_ID = "RESERVA_ID"
        const val ENCUESTA_ID = "ENCUESTA_ID"
        const val EXAMENRESERVA_ID = "EXAMENRESERVA_ID"
        const val CURSO_ID = "CURSO_ID"
        const val EXAMEN_TIPO = "EXAMEN_TIPO"

        fun newInstance(idSistema: String?, IdExamen: String?, IdReserva: String?, IdExamenReserva: String?, IdCurso: String?, IdEncuesta: String?, TipoExamen: String?): ExamenesEncuestasFragment {
            val fragment = ExamenesEncuestasFragment()
            val args = Bundle()
            args.putString(SISTEMA_ID, idSistema)
            args.putString(EXAMEN_ID, IdExamen)
            args.putString(RESERVA_ID, IdReserva)
            args.putString(EXAMENRESERVA_ID, IdExamenReserva)
            args.putString(CURSO_ID, IdCurso)
            args.putString(ENCUESTA_ID, IdEncuesta)
            args.putString(EXAMEN_TIPO, TipoExamen)
            fragment.arguments = args
            return fragment
        }
    }
}