package com.webcontrol.android.ui.encuestas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.Encuestas
import com.webcontrol.android.databinding.FragmentHistoricoCheckListBinding
import com.webcontrol.android.ui.checklist.TipoChecklistFragment
import com.webcontrol.android.ui.common.adapters.HistoricoCheckListAdapter
import com.webcontrol.android.ui.common.adapters.holder.StarViewHolder
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.HistoricoEncuestasViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoricoEncuestasFragment : Fragment(),
    HistoricoCheckListAdapter.HistoricoCheckListAdapterListener {

    private lateinit var binding: FragmentHistoricoCheckListBinding
    private var listEncuestas: List<Encuestas>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    val historicoEncuestasViewModel: HistoricoEncuestasViewModel by viewModels()
    private var historicoCheckListAdapter: HistoricoCheckListAdapter? = null

    private var TipoCheckList: String? = null
    private var NomEncuesta: String? = null
    private var idEncuesta: String? = null
    private var idCompany: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TipoCheckList = requireArguments().getString(tipoEnc)
        NomEncuesta = requireArguments().getString(nombrEncu)
        idEncuesta = requireArguments().getString(indPrimario)
        idCompany = requireArguments().getString(indSecundario)
        requireActivity().title = NomEncuesta
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoricoCheckListBinding.inflate(inflater, container, false)
        SharedUtils.setIdCompany(context, idCompany)
        //lblSubTitulo!!.text = "Historico " + NomEncuesta!!.toLowerCase()

        historicoEncuestasViewModel.getEncuestasResult()
            .observe(viewLifecycleOwner) { listaEncuestas ->
                listEncuestas = listaEncuestas
                if (listEncuestas!!.isNotEmpty()) {
                    binding.emptyState.visibility = View.GONE
                    binding.rcvHistoricoFatigaSomnolencia.visibility = View.VISIBLE
                    val lista: MutableList<CheckListTest> = mutableListOf()
                    listaEncuestas.forEach {
                        val checkList = CheckListTest()
                        checkList.localName = it.ExamName
                        checkList.divisionName = it.ExamType
                        checkList.vehicleId = it.ExamObservation
                        checkList.horaSubmit = it.ExamTime
                        checkList.fechaSubmit = it.ExamDate
                        checkList.estadoInterno = it.Estado!!
                        checkList.idDb = it.PadreId!!
                        lista.add(checkList)
                    }

                        historicoCheckListAdapter = HistoricoCheckListAdapter(
                            requireContext(),
                            lista,
                            this@HistoricoEncuestasFragment
                        )
                        val llm = LinearLayoutManager(requireContext())
                        llm.orientation = LinearLayoutManager.VERTICAL
                        binding.rcvHistoricoFatigaSomnolencia.layoutManager = llm
                        binding.rcvHistoricoFatigaSomnolencia.adapter = historicoCheckListAdapter
                        binding.rcvHistoricoFatigaSomnolencia.recycledViewPool.setMaxRecycledViews(
                            StarViewHolder.LAYOUT,
                            1
                        )
                    } else {
                        binding.rcvHistoricoFatigaSomnolencia.visibility = View.GONE
                        binding.emptyState.visibility = View.VISIBLE
                    }
                    dismissLoader()
                }
        initRecyclerView()
        loadData()

        binding.btnNuevo.setOnClickListener {
            nuevo()
        }
        return binding.root
    }

    private fun loadData() {
        showLoader()
        if (TipoCheckList == null) {
            historicoEncuestasViewModel.getCheckList(nombrEncu)
            TipoCheckList = nombrEncu
        } else {
            historicoEncuestasViewModel.getCheckList(TipoCheckList)
        }
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))
        binding.rcvHistoricoFatigaSomnolencia.addItemDecoration(divider)
        binding.rcvHistoricoFatigaSomnolencia.layoutManager = layoutManager
    }

    private fun showLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(
            context,
            R.raw.loaddinglottie,
            "Cargando...",
            0,
            500,
            200
        )
    }

    private fun dismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(context)
    }

    fun nuevo() {
        if (TipoCheckList == null) {
            findNavController().navigate(
                R.id.tipoChecklistFragment, bundleOf(
                    TipoChecklistFragment.CHECKLIST_TYPE to nombrEncu,
                    TipoChecklistFragment.CHECKLIST_NAME to tipoEnc
                )
            )
        } else {
            findNavController().navigate(
                R.id.tipoChecklistFragment, bundleOf(
                    TipoChecklistFragment.CHECKLIST_TYPE to TipoCheckList,
                    TipoChecklistFragment.CHECKLIST_NAME to NomEncuesta
                )
            )
        }

    }

    companion object {
        const val PARAM_TIPO_ENCUESTA = "tipo_encuesta"
        const val PARAM_NOMBRE_ENCUESTA = "nombre_encuesta"
        const val PARAM_INDICE_PRINCIPAL = "indice_primario"
        const val PARAM_INDICE_SECUNDARIO = "indice_secundario"

        private var tipoEnc = PARAM_TIPO_ENCUESTA
        private var nombrEncu = PARAM_NOMBRE_ENCUESTA
        private var indPrimario = PARAM_INDICE_PRINCIPAL
        private var indSecundario = PARAM_INDICE_SECUNDARIO

        @JvmStatic
        fun newInstance(
            param1: String?,
            param2: String?,
            param3: String?,
            param4: String? = ""
        ): HistoricoEncuestasFragment {
            val fragment = HistoricoEncuestasFragment()
            val args = Bundle()
            args.putString(nombrEncu, param1)
            nombrEncu = param1!!
            args.putString(tipoEnc, param2)
            tipoEnc = param2!!
            args.putString(indPrimario, param3)
            if (param3 != null) {
                indPrimario = param3
            }
            args.putString(indSecundario, param4)
            fragment.arguments = args
            return fragment
        }
    }

    override fun OnRowItemClick(checkListTest: CheckListTest?) {
        findNavController().navigate(
            R.id.historicoEncuestasDetallesFragment, bundleOf(
                "TipoCheckList" to TipoCheckList,
                "localName" to checkListTest!!.localName, "idDb" to checkListTest.idDb
            )
        )
    }
}
