package com.webcontrol.android.ui.encuestas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.db.entity.Encuestas
import com.webcontrol.android.data.model.AnswerExam
import com.webcontrol.android.data.model.QuestionExam
import com.webcontrol.android.data.model.ResponseExam
import com.webcontrol.android.databinding.FragmentHistoricoCheckListDetalleBinding
import com.webcontrol.android.ui.common.adapters.*
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.EncuestasTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class HistoricoEncuestasDetallesFragment: Fragment(), CheckListAdapterListener {
    private lateinit var binding: FragmentHistoricoCheckListDetalleBinding
    private var TipoEncuesta: String? = null
    private var NomEncuesta: String? = null
    private var IdDb = 0

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var listEncuesta_Detalle: List<CheckListTest_Detalle?>? = null
    val  encuestasViewModel: EncuestasTestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TipoEncuesta = requireArguments().getString("TipoCheckList")
        NomEncuesta = requireArguments().getString("localName")
        IdDb = requireArguments().getInt("idDb")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoricoCheckListDetalleBinding.inflate(inflater, container, false)

        binding.lblSubTitulo.text = "Histórico $NomEncuesta"
        listEncuesta_Detalle = ArrayList()
        layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))
        Toast.makeText(context,"",Toast.LENGTH_SHORT).show()
        binding.rcvHistoricoDetalleFatigaSomnolencia.addItemDecoration(divider)
        binding.rcvHistoricoDetalleFatigaSomnolencia.layoutManager = layoutManager

        binding.btnRegresar.setOnClickListener {
            setBtnRegresar()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        load()
    }

    protected fun load(){
        val sections: MutableList<SimpleSectionedRecyclerViewAdapter.Section> = ArrayList()
        val encuestas = App.db.encuestasDao().selectEncuestasByPadreId(IdDb)
        val encuestaTest = encuestaListToExam(encuestas)
        if(encuestas.isNotEmpty()){
            binding.emptyState.visibility = View.GONE
            binding.rcvHistoricoDetalleFatigaSomnolencia.visibility = View.VISIBLE
            val questionsList = ArrayList<CheckListTest_Detalle>()
            encuestaTest.Questions?.forEach { preguntas ->
                val item = CheckListTest_Detalle()
                item.idDb = preguntas.QuestionId!!
                item.idTest = encuestaTest.ExamId
                item.idTest_Detalle = encuestaTest.ExamId
                item.tipo = "STAR"
                item.descripcion = preguntas.QuestionText.toString()
                item.isChecked = false
                item.groupId = preguntas.QuestionId
                item.respuestas = preguntas.Answers?.map{ it -> it.AnswerText!!.replace(",",".")}.toString()
                item.respuestaSeleccionada = preguntas.Answers?.map{ it -> it.AnswerId}.toString()
                questionsList.add(item)
            }
            val typeFactory: TypeFactory = TypeFactoryImpl()
            val adapter = ChecklistAdapter(requireContext(),
                questionsList, typeFactory, true, TipoEncuesta!!, this, null)

            val mSectionedAdapter = SimpleSectionedRecyclerViewAdapter(requireContext(), R.layout.section, R.id.section_text, adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
            mSectionedAdapter.setSections(sections.toTypedArray())
            binding.rcvHistoricoDetalleFatigaSomnolencia.adapter = mSectionedAdapter
        } else {
            binding.rcvHistoricoDetalleFatigaSomnolencia.visibility = View.GONE
            binding.emptyState.visibility = View.VISIBLE
        }
    }

    protected fun encuestaListToExam(list:List<Encuestas>):ResponseExam {
        val answerExam = mutableListOf<AnswerExam>()
        val questionExam = mutableListOf<QuestionExam>()
        var ind = list[0].QuestionId
        var temp = Encuestas()
        for(it in list){
            if (ind == it.QuestionId){
                answerExam.add(AnswerExam(it.AnswerId,it.AnswerText,it.AnswerOrder,it.IsCorrectAnswer,it.IsMarked))
                temp = it
            } else {
                ind = it.QuestionId
                answerExam[temp.Estado!!-1].IsMarked = true
                questionExam.add(QuestionExam(temp.WorkerId?.toInt(),temp.QuestionText,temp.QuestionOrder,temp.IsCommented,temp.IsOrderer,answerExam.toList()))
                answerExam.clear()
                answerExam.add(AnswerExam(it.AnswerId,it.AnswerText,it.AnswerOrder,it.IsCorrectAnswer,it.IsMarked))
            }
        }
        questionExam.add(QuestionExam(temp.WorkerId?.toInt(),temp.QuestionText,temp.QuestionOrder,temp.IsCommented,temp.IsOrderer,answerExam))
        return ResponseExam(temp.ExamId!!,temp.ExamType,temp.ExamName,temp.ExamObservation,questionExam,SharedUtils.wCDate,SharedUtils.time,SharedUtils.getUsuarioId(requireContext()),temp.Estado!!)
    }

    fun setBtnRegresar() {
        findNavController().navigate(R.id.historicoEncuestasFragment, bundleOf("nombre" to "ENCUESTA",
            "Encuesta" to "Encuesta de Satisfacción","id" to "0"))
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"
        private const val ARG_PARAM4 = "param4"

        @JvmStatic
        fun newInstance(
            param1: String?,
            param2: String?,
            param3: Int,
            param4: String? = ""
        ): HistoricoEncuestasDetallesFragment {
            val fragment = HistoricoEncuestasDetallesFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            args.putInt(ARG_PARAM3, param3)
            args.putString(ARG_PARAM4, param4)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onClickButtonSI(item: CheckListTest_Detalle) {
        TODO("Not yet implemented")
    }

    override fun onClickButtonNO(item: CheckListTest_Detalle) {
        TODO("Not yet implemented")
    }

    override fun onClickButtonNOApply(item: CheckListTest_Detalle) {
        TODO("Not yet implemented")
    }

    override fun onTextChanged(item: CheckListTest_Detalle) {
        TODO("Not yet implemented")
    }
}