package com.webcontrol.android.ui.checklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.databinding.FragmentHistoricoCheckListDetalleBinding
import com.webcontrol.android.ui.common.adapters.*
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.CheckListTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HistoricoCheckListDetalleFragment : Fragment(), CheckListAdapterListener, IOnBackPressed {
    private lateinit var binding: FragmentHistoricoCheckListDetalleBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var tipoCheckList: String? = null
    private var nomCheckList: String? = null
    private var checklistId = 0
    private var listCheckList_Detalle: List<CheckListTest_Detalle?>? = null
    private var checkListTestm: CheckListTest? = null
    val checkListTestViewModel: CheckListTestViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tipoCheckList = requireArguments().getString(CHECKLIST_TYPE)
        nomCheckList = requireArguments().getString(CHECKLIST_NAME)
        checklistId = requireArguments().getString(CHECKLIST_ID)?.toInt() ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoricoCheckListDetalleBinding.inflate(inflater, container, false)
        binding.lblSubTitulo.text = "Histórico $nomCheckList"
        listCheckList_Detalle = ArrayList()
        layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))
        binding.rcvHistoricoDetalleFatigaSomnolencia.addItemDecoration(divider)
        binding.rcvHistoricoDetalleFatigaSomnolencia.layoutManager = layoutManager

        binding.btnRegresar.setOnClickListener {
            setBtnRegresar()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkListTestViewModel.getCheckListError().observe(viewLifecycleOwner) { result ->
            Log.e(TAG, result.toString())
            Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_INDEFINITE)
                .setAction("Reintentar") { loadData() }.show()
        }


        checkListTestViewModel.getCheckListEnvioResult().observe(viewLifecycleOwner) { result ->
            if (result && tipoCheckList == "DDS") {
                ProgressLoadingJIGB.finishLoadingJIGB(context)
                Toast.makeText(context, "Se envio", Toast.LENGTH_SHORT).show()
                App.db.checkListDao().updateCheckListTestSend(
                    checkListTestm!!.idDb, 2,
                    SharedUtils.wCDate,
                    SharedUtils.time
                )
                findNavController().navigate(
                    R.id.historicoCheckListFragment, bundleOf(
                        HistoricoCheckListFragment.CHECKLIST_TYPE to tipoCheckList,
                        HistoricoCheckListFragment.CHECKLIST_NAME to nomCheckList,
                        HistoricoCheckListFragment.CHECKLIST_ID to "1",
                        HistoricoCheckListFragment.COMPANY_ID to checkListTestm?.companyId
                    )
                )
            }
        }

        if (requireArguments().getSerializable(CHECKLIST) != null) {
            requireArguments().getSerializable(CHECKLIST).let {
                checkListTestm = requireArguments().getSerializable(CHECKLIST) as CheckListTest
            }
            binding.btnRegresar.let{
                it.text = getString(R.string.finalizar)
            }
        }
    }

    protected fun loadData() {
        if (SharedUtils.isOnline(requireContext())) {
            checkListTestViewModel.getCheckListTest(tipoCheckList, "1", checkListTestm?.companyId)
        } else Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_INDEFINITE)
            .setAction("Reintentar") { loadData() }.show()
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    fun load() {
        val sections: MutableList<SimpleSectionedRecyclerViewAdapter.Section> = ArrayList()
        listCheckList_Detalle = App.db.checkListDao().selectCheckListTest_DetalleByTest(checklistId)
        if (listCheckList_Detalle!!.isNotEmpty()) {
            binding.emptyState.visibility = View.GONE
            binding.rcvHistoricoDetalleFatigaSomnolencia.visibility = View.VISIBLE
            var numPreguntas = 0
            val questionsList: MutableList<CheckListTest_Detalle?> = ArrayList()
            val groupsList = App.db.checklistGroupsDao().getAllByTest(checklistId)
            for (i in groupsList!!.indices) {
                questionsList.addAll(
                    App.db.checkListDao()
                        .selectDetalleByTestAndGroupId(
                            checklistId,
                            groupsList[i]!!.checklistGroupId
                        )!!
                )
                sections.add(
                    SimpleSectionedRecyclerViewAdapter.Section(
                        numPreguntas,
                        groupsList[i]!!.groupName!!
                    )
                )
                numPreguntas = questionsList.size
            }
            val typeFactory: TypeFactory = TypeFactoryImpl()
            val adapter = ChecklistAdapter(
                requireContext(),
                questionsList as List<Visitable>,
                typeFactory,
                true,
                tipoCheckList!!,
                this@HistoricoCheckListDetalleFragment,
                null
            )
            val dummy = arrayOfNulls<SimpleSectionedRecyclerViewAdapter.Section>(sections.size)
            val mSectionedAdapter = SimpleSectionedRecyclerViewAdapter(
                requireContext(),
                R.layout.section,
                R.id.section_text,
                adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
            )
            mSectionedAdapter.setSections(sections.toTypedArray())
            binding.rcvHistoricoDetalleFatigaSomnolencia.adapter = mSectionedAdapter
        } else {
            binding.rcvHistoricoDetalleFatigaSomnolencia.visibility = View.GONE
            binding.emptyState.visibility = View.VISIBLE
        }
    }

    fun setBtnRegresar() {
        if (checkListTestm != null) {
            var message = "¿Esta seguro que desea finalizar la encuesta?"
            var respuestaNegativa = false
            checkListTestm?.grupoList?.get(1)?.preguntas?.forEach {
                if (it.isChecked) {
                    respuestaNegativa = true
                }
            }
            if (respuestaNegativa) {
                message += "\n USTED PRESENTA UNO O MAS SINTOMAS SE NOTIFICARÁ AL ÁREA MÉDICA Y TENDRÁ QUE PERMANECER EN SU HABITACIÓN O RESIDENCIA"
            }
            MaterialDialog.Builder(requireContext())
                .title("COMPLETADO!")
                .content(message)
                .positiveText("ACEPTAR")
                .negativeText("CANCELAR")
                .autoDismiss(true)
                .onPositive { dialog, which ->
                    ProgressLoadingJIGB.startLoadingJIGB(
                        context,
                        R.raw.loaddinglottie,
                        "Cargando...",
                        0,
                        500,
                        200
                    )
                    checkListTestViewModel.sendCheckListTest(checkListTestm)
                }
                .onNegative { dialog, which -> dialog.dismiss() }
                .show()
        } else {
            returnToHistoricoCheckList()
        }
    }

    private fun returnToHistoricoCheckList() {
        if (tipoCheckList != "TFS" && tipoCheckList != "ENC" && tipoCheckList != "TMZ" && tipoCheckList != "EQV" && tipoCheckList != "DDS") {
            nomCheckList = "Vehículos"
        }
        findNavController().navigate(
            R.id.historicoCheckListFragment, bundleOf(
                HistoricoCheckListFragment.CHECKLIST_TYPE to tipoCheckList,
                HistoricoCheckListFragment.CHECKLIST_NAME to nomCheckList,
            )
        )
    }

    override fun onClickButtonSI(position: CheckListTest_Detalle) {}
    override fun onClickButtonNO(position: CheckListTest_Detalle) {}
    override fun onClickButtonNOApply(item: CheckListTest_Detalle) {}

    override fun onTextChanged(item: CheckListTest_Detalle) {}

    override fun onBackPressed(): Boolean {
        return if (checkListTestm != null) {
            val fm: FragmentManager = requireActivity().supportFragmentManager
            for (i in 0 until fm.backStackEntryCount) {
                fm.popBackStack()
            }
            false
        } else {
            returnToHistoricoCheckList()
            false
        }
    }

    companion object {
        private val TAG = HistoricoCheckListDetalleFragment::class.java.simpleName
        private const val CHECKLIST = "checklist"
        const val CHECKLIST_TYPE = "checklist_type"
        const val CHECKLIST_NAME = "checklist_name"
        const val CHECKLIST_ID = "checklist_id"
    }
}