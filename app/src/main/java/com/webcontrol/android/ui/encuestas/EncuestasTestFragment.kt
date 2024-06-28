package com.webcontrol.android.ui.encuestas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.OnCheckListener
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.model.ResponseExam
import com.webcontrol.android.databinding.FragmentTestCheckListBinding
import com.webcontrol.android.ui.common.adapters.*
import com.webcontrol.android.ui.common.adapters.holder.StarViewHolder
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.EncuestasTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EncuestasTestFragment: Fragment(), CheckListAdapterListener, OnCheckListener {

    private lateinit var binding: FragmentTestCheckListBinding
    val encuestasTestViewModel: EncuestasTestViewModel by viewModels()
    private var linearLayoutManager: LinearLayoutManager? = null
    private var loader: MaterialDialog? = null
    private var responseExam: ResponseExam? = null
    private var questionsList: MutableList<CheckListTest_Detalle>? = null

    private var IdCompany: String? = ""

    private var TipoEncuesta: String? = null
    private var NomEncuesta: String? = null
    private var idEncuesta: String? = null
    private var rutPasajero: String? = ""
    private var IdPadre: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TipoEncuesta = requireArguments().getString("TipoEncuesta")
        NomEncuesta = requireArguments().getString("NomEncuesta")
        idEncuesta = requireArguments().getString("idEncuesta")
        rutPasajero = requireArguments().getString(ARG_PARAM4)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentTestCheckListBinding.inflate(inflater, container, false)
        binding.lblFecha.text = SharedUtils.getNiceDate(SharedUtils.wCDate)
        binding.lblHora.text = SharedUtils.time
        IdCompany = SharedUtils.getIdCompany(context)
        binding.btnEnviarResultados.setOnClickListener {
            EnviarResultados()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeRecycler()
        loadData()

        val sections: MutableList<SimpleSectionedRecyclerViewAdapter.Section> = ArrayList()
        encuestasTestViewModel.getEncuestasResult().observe(viewLifecycleOwner) { encuestaTest ->
            if (encuestaTest != null) {
                IdPadre =
                    App.db.encuestasDao().getLastInsertedEncuestasIdPadre(idEncuesta!!.toInt())
                responseExam = encuestaTest
                binding.lblNomTest.text = encuestaTest.ExamName
                Toast.makeText(requireContext(), "Encuesta", Toast.LENGTH_SHORT).show()
                questionsList = ArrayList<CheckListTest_Detalle>()
                encuestaTest.Questions?.forEach { preguntas ->
                    val item = CheckListTest_Detalle()
                    item.idDb = preguntas.QuestionId!!
                    item.idTest = encuestaTest.ExamId
                    item.idTest_Detalle = encuestaTest.ExamId
                    item.tipo = "STAR"
                    item.descripcion = preguntas.QuestionText.toString()
                    item.isChecked = false
                    item.groupId = encuestaTest.Questions.indexOf(preguntas)
                    item.respuestas =
                        preguntas.Answers?.map { it -> it.AnswerText!!.replace(",", ".") }
                            .toString()
                    item.respuestaSeleccionada =
                        preguntas.Answers?.map { it -> it.AnswerId }.toString()
                    questionsList!!.add(item)
                }
                val numPreguntas = encuestaTest.Questions?.size
                binding.lblNumPreguntas.text = "$numPreguntas preguntas"

                val adapter = StarRecyclerViewAdapter(this)
                adapter.setmAllRepositories(questionsList)

                val llm = LinearLayoutManager(requireContext())
                llm.orientation = LinearLayoutManager.VERTICAL
                binding.rcvTestList.layoutManager = llm
                binding.rcvTestList.adapter = adapter
                binding.rcvTestList.recycledViewPool.setMaxRecycledViews(StarViewHolder.LAYOUT, 1)
            }
            loader!!.dismiss()
        }

        encuestasTestViewModel.getEncuestasEnvioResult().observe(viewLifecycleOwner) { result ->
            if (result) {
                if (IdPadre != null) {
                    App.db.encuestasDao().updatedEncuestasId(IdPadre!!)
                }
                findNavController().navigate(R.id.historicoEncuestasFragment,
                    bundleOf("TipoCheckList" to "ENCUESTA","NomEncuesta" to "Encuesta de Satisfacción",
                    "idEncuesta" to "0"))
            } else {
                Toast.makeText(
                    context,
                    "No se pudo completar la operación intentelo nuevamente",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    protected fun initializeRecycler() {
        val divider: RecyclerView.ItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        linearLayoutManager = LinearLayoutManager(context)
        binding.rcvTestList.setHasFixedSize(true)
        binding.rcvTestList.layoutManager = linearLayoutManager
        binding.rcvTestList.addItemDecoration(divider)
    }

    protected fun loadData() {
        if (SharedUtils.isOnline(requireContext())) {
            showLoader()
            encuestasTestViewModel.getEncuestaTest(TipoEncuesta, idEncuesta,SharedUtils.getUsuarioId(requireContext()))
        } else Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar") { loadData() }.show()
    }

    protected fun showLoader() {
        loader = MaterialDialog.Builder(requireContext())
                .title("Procesando")
                .content("Espere, por favor.")
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 0)
                .show()
    }


    fun EnviarResultados() {
        var flagAsw = true
        for (QuestionExam in responseExam?.Questions!!){
            if(flagAsw)
                for(AnswerExam in QuestionExam.Answers!!)
                    if(AnswerExam.IsMarked!!){
                        flagAsw = true
                        break
                    }
                    else
                        flagAsw = false
            else
                break
        }
        if(flagAsw){
            responseExam?.WorkerId = SharedUtils.getUsuarioId(requireContext())
            Log.e("Respuesta",responseExam.toString())
            encuestasTestViewModel.sendEncuestaTest(responseExam)
        }else
            checkAnswers()
    }

    fun checkAnswers(){
        MaterialDialog.Builder(requireContext())
            .title("Alerta")
            .content("Responda todas las preguntas")
            .positiveText("Aceptar")
            .autoDismiss(true)
            .show()
    }

    override fun onClickButtonSI(item: CheckListTest_Detalle) {
        responseExam?.Questions?.forEach {
            if(it.QuestionId == item.idDb){
                it.Answers?.get(item.estado-1)?.IsMarked = true
                App.db.encuestasDao().updatedEncuestasIdandAnswer(IdPadre!!,item.idDb,item.groupId.toString())
            }
        }
    }

    override fun onClickButtonNO(item: CheckListTest_Detalle) {

    }

    override fun onClickButtonNOApply(item: CheckListTest_Detalle) {
        TODO("Not yet implemented")
    }

    override fun onTextChanged(item: CheckListTest_Detalle) {

    }

    companion object {
        private val TAG = EncuestasTestFragment::class.java.simpleName
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"
        private const val ARG_PARAM4 = "param4"

        @JvmStatic
        fun newInstance(param1: String?, param2: String?, param3: String?, param4: String? = null): EncuestasTestFragment {
            val fragment = EncuestasTestFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            args.putString(ARG_PARAM3, param3)
            args.putString(ARG_PARAM4, param4)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCheck(value: String, item: CheckListTest_Detalle) {
    }
}
