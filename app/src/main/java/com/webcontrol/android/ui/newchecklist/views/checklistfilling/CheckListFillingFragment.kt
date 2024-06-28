package com.webcontrol.android.ui.newchecklist.views.checklistfilling

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentTestChecklistVehicularInspectionBinding
import com.webcontrol.android.ui.newchecklist.NewCheckListViewModel
import com.webcontrol.android.ui.newchecklist.adapters.ChecklistFillingAdapter
import com.webcontrol.android.ui.newchecklist.data.NewCheckListGroup
import com.webcontrol.android.ui.newchecklist.data.NewCheckListScope
import com.webcontrol.android.ui.newchecklist.data.ScopesChecklist
import com.webcontrol.android.ui.newchecklist.data.TypeAnswer
import com.webcontrol.android.util.BaseFragment
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_test_checklist_vehicular_inspection.view.*
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CheckListFillingFragment :
    BaseFragment<FragmentTestChecklistVehicularInspectionBinding, ChecklistFillingViewModel>() {

    val args: CheckListFillingFragmentArgs by navArgs()

    private val parentViewModel: NewCheckListViewModel by activityViewModels()

    private val adapter: ChecklistFillingAdapter by lazy {
        ChecklistFillingAdapter(listOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "CheckList Test"
    }

    override fun getViewModelClass() = ChecklistFillingViewModel::class.java
    override fun getViewBinding() =
        FragmentTestChecklistVehicularInspectionBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcvTestVehicularInspection.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvTestVehicularInspection.adapter = adapter

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.lblDate.text = currentDate

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding.lblHour.text = currentTime

        parentViewModel.uiListGroup.let { group ->
            //TODO ELIMINAR FILTRO EN DESCRIPCION - CUANDO SE ARREGLE ENDPOINT
            //adapter.setList(group[args.position].data.filter { it.descripcion != "NO" })
            //*****
            if (SharedUtils.isOnline(requireContext())){
                adapter.setList(group[args.position].data)
                binding.lblTestName.text = group[args.position].data.first().nombrecheckgroup
                binding.lblQuestionNum.text = "${group[args.position].data.size} preguntas"
            }else{
                val prueba =App.db.checkListBambaDao().getAnswer(args.position+1).map {
                    it.toMapper()
                }
                adapter.setList(prueba)
            }


        }
        binding.btnSaveAnswers.setOnClickListener {
            if(adapter.isCompleteCL()) {
                val groupAnswer = adapter.getList()
                val listGroup = parentViewModel.uiListGroup.toMutableList()
                if (SharedUtils.isOnline(requireContext())){
                    listGroup[args.position].checkIdHead = parentViewModel.uiCheckingHead.result
                    listGroup[args.position].data = groupAnswer
                }
                else{
                    listGroup[args.position].checkIdHead = parentViewModel.uiCheckingHead.result
                    listGroup[args.position].data = groupAnswer
                }
                parentViewModel.setListQuestions(listGroup)
                findNavController().navigate(R.id.action_checkListFillingFragment_to_listCheckListFragment)
            } else {
                Toast.makeText(requireContext(), "Necesita Completar el Test", Toast.LENGTH_SHORT).show()
            }
        }
        NewCheckListScope.scope = ScopesChecklist.CHECKLIST_FILLING
    }
}