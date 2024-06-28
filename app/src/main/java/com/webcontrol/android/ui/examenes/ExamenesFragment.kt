package com.webcontrol.android.ui.examenes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.Cursos
import com.webcontrol.android.data.db.entity.Examenes
import com.webcontrol.android.data.model.ResultadosExamen
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.FragmentExamenesBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.common.adapters.ExpandableCustomListAdapter
import com.webcontrol.android.util.Constants
import com.webcontrol.android.util.RestClient.build
import com.webcontrol.android.util.RestClient.buildRx
import com.webcontrol.android.util.SharedUtils.dismissLoader
import com.webcontrol.android.util.SharedUtils.getIMEI
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.isOnline
import com.webcontrol.android.util.SharedUtils.showLoader
import com.webcontrol.android.util.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@AndroidEntryPoint
class ExamenesFragment : Fragment(), IOnBackPressed {

    private lateinit var binding: FragmentExamenesBinding
    private var cursosList: MutableList<Cursos>? = null
    private var adapter: ExpandableListAdapter? = null
    private var TAG: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title="Examenes"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentExamenesBinding.inflate(inflater, container, false)
        cursosList = ArrayList()
        adapter = ExpandableCustomListAdapter(requireContext(), cursosList!!)
        binding.expandableExamenes.setAdapter(adapter)
        binding.expandableExamenes.emptyView = binding.lblEmptyView
        TAG = ExamenesFragment::class.java.simpleName
        binding.expandableExamenes.setGroupIndicator(null)
        binding.expandableExamenes.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            onItemClick(cursosList!![groupPosition].listExamenes[childPosition])
            false
        }
        fetch()
        binding.loaderExamenes.setOnRefreshListener {
            fetch()
        }
        return binding.root
    }

    fun fetch() {
        if (isOnline(requireContext())) {
            cursosList!!.clear()
            adapter = ExpandableCustomListAdapter(requireContext(), ArrayList())
            binding.expandableExamenes.setAdapter(adapter)
            syncCourses()
        } else {
            binding.loaderExamenes.isRefreshing = false
            showToast(requireContext(), resources.getString(R.string.without_internet))
        }
    }

    private val listExamsFromQBlanca: Observable<ApiResponse<Cursos>>
        get() {
            val api = buildRx(Constants.QBLANCA)
            return api.getListCursos(getUsuarioId(context))
        }

    private fun insertCourses(sistema: String, response: ApiResponse<Cursos>): List<Cursos> {
        val cursos: MutableList<Cursos> = ArrayList()
        for (i in response.dataNested.indices) {
            val curso = response.dataNested[i]
            curso.idSistema = sistema
            val existeCurso = App.db.cursosDao().getOne(sistema, curso.idCurso, curso.idReserva, curso.rut)
            if (existeCurso != null) {
                App.db.cursosDao().updateCurso(sistema, curso.idCurso
                        , curso.idReserva
                        , curso.rut
                        , curso.nomCurso
                        , curso.orador
                        , curso.minAprobacion
                        , curso.fechaHoraCurso
                        , curso.dia
                        , curso.fechaExamen
                )
            } else {
                curso.idSistema = sistema
                App.db.cursosDao().insertar(curso)
            }
            for (j in curso.listExamenes.indices) {
                val examen = curso.listExamenes[j]
                examen.idSistema = sistema
                examen.idCurso = curso.idCurso
                examen.rut = curso.rut
                val exist = App.db.examenesDao().getOne(sistema, examen.idExamenReserva, examen.idExamen, examen.idReserva, examen.rut)
                if (exist != null) {
                    App.db.examenesDao().updateExamenes(sistema, examen.idExamenReserva
                            , examen.idExamen
                            , examen.idReserva
                            , examen.rut
                            , examen.nomExamen
                            , examen.idEncuesta
                            , examen.nomEncuesta
                            , examen.descExamen
                            , examen.fecha_programada
                            , examen.hora_programada
                            , examen.tiempoTotal
                            , examen.aprobo
                            , examen.estado
                            , examen.tipo
                            , examen.orden
                            , examen.examenFinal
                            , examen.dia
                            , examen.fechaExamen)
                } else {
                    App.db.examenesDao().insertar(examen)
                }
            }
            curso.listExamenes.sortedWith { e1, e2 -> if (e1.orden < e2.orden) 1 else 0 }
            //curso.listExamenes.sortWith(Comparator { e1, e2 -> if (e1.orden < e2.orden) 1 else 0 })
            if (curso.listExamenes.size > 0) cursos.add(curso)
        }
        return cursos
    }
    private fun syncCourses() {
        binding.loaderExamenes.isRefreshing = true
        listExamsFromQBlanca
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ApiResponse<Cursos>> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(response: ApiResponse<Cursos>) {
                        val cursos = response.dataNested
                        //TODO: error de null segun firebase -->
                        if (cursos != null && cursos.isNotEmpty() && response.isSuccess) {
                            adapter = ExpandableCustomListAdapter(context!!, cursos)
                            binding.expandableExamenes.setAdapter(adapter)
                            cursosList = cursos.toMutableList()
                            binding.loaderExamenes.isRefreshing = false
                        } else {
                            Toast.makeText(context, "No se han encontrado Examenes.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onError(e: Throwable) {
                        binding.loaderExamenes.isRefreshing = false
                    }

                    override fun onComplete() {
                        binding.loaderExamenes.isRefreshing = false
                    }
                })
    }

    fun onItemClick(examen: Examenes) {
        try {
            if (isOnline(requireContext())) {
                when (examen.tipo) {
                    "EXAMEN" -> if (examen.idEncuesta <= 0) {
                        validarInicioExamen(examen.idSistema, examen.idExamenReserva, examen.idCurso, examen.idReserva, examen.rut, examen.idExamen)
                    } else {
                        showLoader(context, "Procesando...")
                        val api = build(examen.idSistema)
                        val resultado = ResultadosExamen(
                                examen.idExamenReserva,
                                examen.idReserva,
                                examen.idEncuesta,
                                getUsuarioId(context),
                                0,
                                0,
                                getIMEI(requireContext()),
                                "ENCUESTA",
                                -2
                        )
                        val call = api.validarResultadoByIMEI(resultado)
                        call.enqueue(object : Callback<ApiResponse<List<ResultadosExamen>>?> {
                            override fun onResponse(call: Call<ApiResponse<List<ResultadosExamen>>?>, response: Response<ApiResponse<List<ResultadosExamen>>?>) {
                                if (response.isSuccessful) {
                                    val result = response.body()
                                    if (result != null && result.isSuccess) {
                                        if (result.data.isNotEmpty()) {
                                            when (result.data[0].respuesta) {
                                                1 -> {
                                                    dismissLoader(context)
                                                    findNavController().navigate(R.id.examenesEncuestasFragment,
                                                        bundleOf("idSistema" to examen.idSistema,"idExamen" to "0" + examen.idExamen,
                                                        "idReserva" to "0" + examen.idReserva,"IdExamenReserva" to "0" + examen.idExamenReserva,
                                                        "IdCurso" to "0" + examen.idCurso,"idEncuesta" to "0" + examen.idExamen,"Tipo" to examen.tipo
                                                        ))
                                                }
                                                2 -> {
                                                    dismissLoader(context)
                                                    Toast.makeText(context, "Esta encuesta esta activa en otro dispositivo", Toast.LENGTH_SHORT).show()
                                                }
                                                3, 4 -> {
                                                    dismissLoader(context)
                                                    validarInicioExamen(examen.idSistema, examen.idExamenReserva, examen.idCurso, examen.idReserva, examen.rut, examen.idExamen)
                                                }
                                                5 -> {
                                                    dismissLoader(context)
                                                    Toast.makeText(context, "Este examen no esta disponible en este momento", Toast.LENGTH_SHORT).show()
                                                }
                                                else -> {
                                                    dismissLoader(context)
                                                    Toast.makeText(context, "Ha Ocurrido un error contactese con el administrador", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            dismissLoader(context)
                                            IniciarEncuesta(examen.idSistema, examen.idExamenReserva, examen.idCurso, examen.idEncuesta, examen.idReserva, examen.idExamen,
                                                examen.tipo!!
                                            )
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ApiResponse<List<ResultadosExamen>>?>, t: Throwable) {
                                dismissLoader(context)
                                showToast(requireContext(), TAG + " onItemClick() " + t.message)
                            }
                        })
                    }
                    "ENCUESTA" -> {
                        showLoader(context, "Procesando...")
                        val api = build(examen.idSistema)
                        val resultado = ResultadosExamen(
                                examen.idExamenReserva,
                                examen.idReserva,
                                examen.idExamen,
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
                                        if (result.data.isNotEmpty()) {
                                            when (result.data[0].respuesta) {
                                                1 -> {
                                                    dismissLoader(context)
                                                    findNavController().navigate(R.id.examenesEncuestasFragment,
                                                        bundleOf("idSistema" to examen.idSistema,"idExamen" to "0" + examen.idExamen,
                                                            "idReserva" to "0" + examen.idReserva,"IdExamenReserva" to "0" + examen.idExamenReserva,
                                                            "IdCurso" to "0" + examen.idCurso,"idEncuesta" to "0" + examen.idExamen,"Tipo" to examen.tipo
                                                        ))
                                                }
                                                2 -> {
                                                    dismissLoader(context)
                                                    Toast.makeText(context, "Esta encuesta esta activa en otro dispositivo", Toast.LENGTH_SHORT).show()
                                                }
                                                3, 4 -> {
                                                    dismissLoader(context)
                                                    Toast.makeText(context, "Esta encuesta ya fue realizada", Toast.LENGTH_SHORT).show()
                                                }
                                                5 -> {
                                                    dismissLoader(context)
                                                    Toast.makeText(context, "Esta encuesta no esta disponible en este momento", Toast.LENGTH_SHORT).show()
                                                }
                                                else -> {
                                                    dismissLoader(context)
                                                    Toast.makeText(context, "Ha Ocurrido un error contactese con el administrador", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            dismissLoader(context)
                                            IniciarEncuesta(examen.idSistema, examen.idExamenReserva, examen.idCurso, examen.idExamen, examen.idReserva, examen.idExamen,
                                                examen.tipo!!
                                            )
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ApiResponse<List<ResultadosExamen>>?>, t: Throwable) {
                                dismissLoader(context)
                                showToast(requireContext(), TAG + " onItemClicked()  case encuesta" + t.message)
                            }
                        })
                    }
                }
            } else showToast(requireContext(), resources.getString(R.string.without_internet))
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message)
        }
    }

    fun validarInicioExamen(sistema: String?, idExamenReserva: Int, idCurso: Int, IdReserva: Int, rut: String?, IdExamen: Int) {
        showLoader(activity, "Procesando...")
        val api = build(sistema!!)
        val resultado = ResultadosExamen(
                idExamenReserva,
                IdReserva,
                IdExamen,
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
                        if (result.data.isNotEmpty()) {
                            when (result.data[0].respuesta) {
                                1 -> {
                                    dismissLoader(context)
                                    findNavController().navigate(R.id.examenesInicioFragment, bundleOf("sistemaId" to sistema,
                                        "examenesId" to "0$IdExamen","reservaId" to "0$IdReserva","examenReservaId" to "0$idExamenReserva",
                                        "cursoId" to "0$idCurso")
                                    )
                                }
                                2 -> {
                                    dismissLoader(context)
                                    Toast.makeText(context, "Este examen esta activo en otro dispositivo", Toast.LENGTH_SHORT).show()
                                }
                                3, 4 -> {
                                    dismissLoader(activity)
                                    findNavController().navigate(R.id.examenesResultadosFragment, bundleOf("sistemaId" to sistema,
                                        "examenesId" to "0$IdExamen","reservaId" to "0$IdReserva","examenReservaId" to "0$idExamenReserva", "cursoId" to "0$idCurso",
                                        "respuesta" to "0" + result.data[0].respuesta,"numCorrectas" to "0" + result.data[0].numCorrectas,"numPreguntas" to "0" + result.data[0].numPreguntas))
                                }
                                5 -> {
                                    dismissLoader(context)
                                    Toast.makeText(context, "Este examen no esta disponible en este momento", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    dismissLoader(context)
                                    Toast.makeText(context, "Ha Ocurrido un error contactese con el administrador", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            dismissLoader(context)
                            findNavController().navigate(R.id.examenesInicioFragment, bundleOf("sistemaId" to sistema,
                            "examenesId" to "0$IdExamen","reservaId" to "0$IdReserva","examenReservaId" to "0$idExamenReserva",
                            "cursoId" to "0$idCurso"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<ResultadosExamen>>?>, t: Throwable) {
                dismissLoader(context)
                showToast(requireContext(), TAG + " validarInicioExamen() " + t.message)
            }
        })
    }

    private fun IniciarEncuesta(sistema: String, idExamenReserva: Int, idCurso: Int, idEncuesta: Int, idReserva: Int, idExamen: Int, tipo: String) {
        showLoader(context, "Procesando...")
        val totalPreguntas = App.db.preguntasDao().getCountPreguntas(sistema
                , idExamenReserva
                , idEncuesta
                , idReserva
                , getUsuarioId(context))
        val api = build(sistema)
        val resultado = ResultadosExamen(
                idExamenReserva,
                idReserva,
                idEncuesta,
                getUsuarioId(context),
                totalPreguntas,
                0,
                getIMEI(requireContext()),
                "ENCUESTA",
                -2
        )
        val call: Call<ApiResponse<Any>> = api.insertResultado(resultado)
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(call: Call<ApiResponse<Any>?>, response: Response<ApiResponse<Any>?>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        if (result.isSuccess) {
                            dismissLoader(context)
                            App.db.examenesDao().setExamenIniciado(sistema
                                    , idExamenReserva
                                    , idEncuesta
                                    , idReserva
                                    , getUsuarioId(context))
                            findNavController().navigate(R.id.examenesEncuestasFragment,
                                bundleOf("idSistema" to sistema,"idExamen" to "0" + "0$idExamen",
                                    "idReserva" to "0$idReserva","IdExamenReserva" to "0$idExamenReserva",
                                    "IdCurso" to "0$idCurso","idEncuesta" to "0$idEncuesta","Tipo" to tipo))
                        } else dismissLoader(context)
                    }
                } else {
                    dismissLoader(context)
                    showToast(requireContext(), response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                dismissLoader(context)
                showToast(requireContext(), TAG + " iniciarEncuesta() " + t.message)
            }
        })
    }

    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.confirmSessionAbandon()
        return false
    }
}