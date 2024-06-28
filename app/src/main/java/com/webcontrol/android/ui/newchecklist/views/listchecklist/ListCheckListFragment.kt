package com.webcontrol.android.ui.newchecklist.views.listchecklist

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.NewCheckListGroups
import com.webcontrol.android.data.db.entity.NewCheckListQuestions
import com.webcontrol.android.databinding.FragmentChecklistVehicularInspectionBinding
import com.webcontrol.android.ui.newchecklist.NewCheckListViewModel
import com.webcontrol.android.ui.newchecklist.adapters.ListChecklistAdapter
import com.webcontrol.android.ui.newchecklist.data.NewCheckListGroup
import com.webcontrol.android.ui.newchecklist.data.NewCheckListScope
import com.webcontrol.android.ui.newchecklist.data.ScopesChecklist
import com.webcontrol.android.ui.newchecklist.data.TypeAnswer
import com.webcontrol.android.ui.newchecklist.listeners.ChecklistGroupListener
import com.webcontrol.android.util.BaseFragment
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tipo_checklist.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ListCheckListFragment :
    BaseFragment<FragmentChecklistVehicularInspectionBinding, ListChecklistViewModel>(),ChecklistGroupListener {

    private val parentViewModel: NewCheckListViewModel by activityViewModels()

    private val adapter: ListChecklistAdapter by lazy {
        ListChecklistAdapter(listOf(),this)
    }
    val menuCheckList = arrayListOf<NewCheckListGroups>()
    val chekListQuestionDB = arrayListOf<NewCheckListQuestions>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "CheckList"
    }

    override fun getViewModelClass() = ListChecklistViewModel::class.java
    override fun getViewBinding() =
        FragmentChecklistVehicularInspectionBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listCheckList.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    ListChecklistUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo checklist")
                    }
                    ListChecklistUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }
                    ListChecklistUIEvent.Error -> {
                        Log.e("parentwiwwmodelr",parentViewModel.uiListGroup.isNotEmpty().toString())
                        if (parentViewModel.uiListGroup.isNotEmpty()) {
                            showList(parentViewModel.uiListGroup)
                        }
                        else {
                            parentViewModel.setListQuestions(App.db.checkListBambaDao().getGroup().map {
                                it.toMapper(App.db.checkListBambaDao().getAnswer(it.checkIdGroup).map { it.toMapper() })
                            })
                        }
                    }
                    is ListChecklistUIEvent.Success -> {
                        val userId = SharedUtils.getUsuarioId(requireContext())
                        event.listChecklist.forEach { it.data.forEach { it.usrcrea = userId } }
                        if (event.listChecklist.isNotEmpty()) {
                            parentViewModel.setListQuestions(event.listChecklist)
                            showList(event.listChecklist)
                            for (listCheck in event.listChecklist){
                                menuCheckList.add(
                                    NewCheckListGroups(
                                        idCheck =listCheck.idCheck,
                                        checkIdHead = parentViewModel.uiCheckingHead.result,
                                        checkListType = listCheck.checkListType,
                                        checkListActivo = listCheck.checklistActivo,
                                        nameCheck = listCheck.nameCheck,
                                        checkIdGroup = listCheck.checkIdGroup,
                                        nameGroup = listCheck.nameGroup
                                    )
                                )
                                for (listQuestion in listCheck.data){
                                    chekListQuestionDB.add(NewCheckListQuestions(
                                        idTipo = listQuestion.iD_TIPO,
                                        idCheck = listQuestion.iD_CHECK,
                                        idCheckGroup = listQuestion.iD_CHECKGROUP,
                                        nombreCheckGroup = listQuestion.nombrecheckgroup,
                                        idCheckDet = listQuestion.iD_CHECKDET,
                                        nombre = listQuestion.nombre,
                                        descripcion = listQuestion.descripcion,
                                        regFoto = listQuestion.reqfoto,
                                        orden = listQuestion.orden,
                                        usrCrea = listQuestion.usrcrea,
                                        fecCrea = listQuestion.usrcrea,
                                        tipo = listQuestion.tipo,
                                        valor = listQuestion.valor,
                                        valorMult = listQuestion.valormult,
                                        critico = listQuestion.critico,
                                        answer = listQuestion.answer
                                    ))
                                }
                            }
                            App.db.checkListBambaDao().insertMenuList(menuCheckList)
                            App.db.checkListBambaDao().insertAnswer(chekListQuestionDB)
                        }
                    }
                    is ListChecklistUIEvent.SuccessAnswerSaving -> {
                        parentViewModel.setListQuestion(event.listChecklist)
                        if(event.listChecklist.isEmpty())
                            findNavController().navigate(R.id.action_listCheckListFragment_to_evidencePhotoFragment)
                        else
                            findNavController().navigate(R.id.action_listCheckListFragment_to_evidencePhotoFragment)
                    }
                }
            }
        }
        NewCheckListScope.scope = ScopesChecklist.LIST_CHECKLIST
    }

    override fun setUpViews() {
        super.setUpViews()

        binding.rcvListCl.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvListCl.adapter = adapter

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.lblDate.text = currentDate

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding.lblHour.text = currentTime



        if (SharedUtils.isOnline(requireContext())) {
            if (parentViewModel.uiListGroup.isEmpty()) {
                viewModel.getListCheckList()
            } else {
                showList(parentViewModel.uiListGroup)
            }
        } else {
            if (parentViewModel.uiListGroup.isEmpty()) {
                parentViewModel.setListQuestions(App.db.checkListBambaDao().getGroup().map {
                    it.toMapper(
                        App.db.checkListBambaDao().getAnswer(it.checkIdGroup).map { it.toMapper() })
                })
            }
            showList(parentViewModel.uiListGroup)


        }

        binding.btnContinue.isEnabled = parentViewModel.uiEnableBtn

        binding.btnContinue.setOnClickListener {
            //viewModel.noHacer()
            var status = "E2"
            parentViewModel.uiListGroup.forEach {
                it.data.forEach { question ->
                    if (question.critico && question.answer == TypeAnswer.NO) {
                        status = "E4"
                        return@forEach
                    }
                    else if (question.answer == TypeAnswer.NO){
                        status = "E3"
                    }
                }
                if (status == "E4")
                    return@forEach
            }
            if (parentViewModel.isCompleteGroup()) {
                App.db.checkListBambaDao().clean()
                for (listQuestion in parentViewModel.uiListGroup) {
                    listQuestion.status = status
                    for (listQuestion in listQuestion.data) {
                        chekListQuestionDB.add(
                            NewCheckListQuestions(
                                idTipo = listQuestion.iD_TIPO,
                                idCheck = listQuestion.iD_CHECK,
                                idCheckGroup = listQuestion.iD_CHECKGROUP,
                                nombreCheckGroup = listQuestion.nombrecheckgroup,
                                idCheckDet = listQuestion.iD_CHECKDET,
                                nombre = listQuestion.nombre,
                                descripcion = listQuestion.descripcion,
                                regFoto = listQuestion.reqfoto,
                                orden = listQuestion.orden,
                                usrCrea = listQuestion.usrcrea,
                                fecCrea = listQuestion.usrcrea,
                                tipo = listQuestion.tipo,
                                valor = listQuestion.valor,
                                valorMult = listQuestion.valormult,
                                critico = listQuestion.critico,
                                answer = listQuestion.answer
                            )
                        )
                    }
                }
                App.db.checkListBambaDao().insertAnswer(chekListQuestionDB)
                //viewModel.noHacer()
                Log.e("ingreso aca","antes del complete")
                if (parentViewModel.isCompleteGroup()) {

                    if(SharedUtils.isOnline(requireContext())){
                        Log.e("ingreso aca","despues del complete")
                        viewModel.savingAnswer(parentViewModel.uiListGroup)
                    }
                    else{
                        getNo()
                        findNavController().popBackStack(R.id.listCheckListFragment, false)
                        findNavController().navigate(R.id.action_listCheckListFragment_to_evidencePhotoFragment)
                    }
                } else
                    Toast.makeText(
                        requireContext(),
                        "Debe Completar Todos Los Test",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }

    private fun showList(listChecklist: List<NewCheckListGroup>) = with(binding) {
        val newCheckListGroup = listChecklist.map { it.reloadAnswers()}
        parentViewModel.setListQuestions(newCheckListGroup)
        adapter.setList(newCheckListGroup)
    }

    override fun onItemClickGroup(position: Int) {
        val action =  ListCheckListFragmentDirections.actionListCheckListFragmentToCheckListFillingFragment(position)
        findNavController().navigate(action)
    }
    fun getNo(){
        parentViewModel.setListQuestion(App.db.checkListBambaDao().getAnswerNo().map { it.toMapper() })
    }
}