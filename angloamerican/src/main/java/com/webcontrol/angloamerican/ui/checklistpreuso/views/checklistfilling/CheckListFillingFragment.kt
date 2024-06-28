package com.webcontrol.angloamerican.ui.checklistpreuso.views.checklistfilling

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.FragmentPreUsoTestChecklistVehicularInspectionBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.CameraRequestListener
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoActivity
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoViewModel
import com.webcontrol.angloamerican.ui.checklistpreuso.adapters.ChecklistPreUsoFillingAdapter
import com.webcontrol.angloamerican.ui.checklistpreuso.adapters.OnCardImgClickListener
import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListScope
import com.webcontrol.angloamerican.ui.checklistpreuso.data.ScopesChecklist
import com.webcontrol.angloamerican.ui.checklistpreuso.data.TypeAnswer
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.AppDataBase
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.Question
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pre_uso_test_checklist_vehicular_inspection.btnSaveAnswers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class CheckListFillingFragment :
    BaseFragment<FragmentPreUsoTestChecklistVehicularInspectionBinding, ChecklistFillingViewModel>(),
    OnCardImgClickListener, CheckListPreUsoActivity.FragmentListener, SaveAnswersButtonListener {

    val args: CheckListFillingFragmentArgs by navArgs()
    private var cameraRequestListener: CameraRequestListener? = null
    private val parentViewModel: CheckListPreUsoViewModel by activityViewModels()
    lateinit var db: AppDataBase
    private val adapter: ChecklistPreUsoFillingAdapter by lazy {
        ChecklistPreUsoFillingAdapter(listOf(),this, parentViewModel.uiDisabledBtn, this, parentViewModel)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cameraRequestListener = context as CameraRequestListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "CheckList Test"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as CheckListPreUsoActivity?)?.setFragmentListener(this)
    }

    override fun getViewModelClass() = ChecklistFillingViewModel::class.java
    override fun getViewBinding() =
        FragmentPreUsoTestChecklistVehicularInspectionBinding.inflate(layoutInflater)

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
            //adapter.setList(group[args.position].questions.filter { it.description != "NO" })
            if (SharedUtils.isOnline(requireContext())){
                val currentQuestions = group[args.position].questions
                currentQuestions.forEach {
                    if(it.answer == null){
                        it.answer = TypeAnswer.NN
                    }
                }
                adapter.setList(currentQuestions)
                binding.lblTestName.text = group[args.position].questions.first().groupName
                binding.lblQuestionNum.text = "${group[args.position].questions.size} preguntas"
            }
        }

        with(binding.btnSaveAnswers){
            if (parentViewModel.uiDisabledBtn && adapter.isCompleteCL() && !parentViewModel.uiCheckListStatus){
                visibility = View.GONE
            }else {
                visibility = View.VISIBLE
                isEnabled = true
            }
        }

        binding.btnSaveAnswers.setOnClickListener {
            if (adapter.isCompleteCL()) {
                val groupAnswer = adapter.getList()
                val listGroup = parentViewModel.uiListGroup.toMutableList()
                listGroup[args.position].checkIdHead = parentViewModel.uiCheckingHead
                listGroup[args.position].questions = groupAnswer
                parentViewModel.setListQuestions(listGroup)
                findNavController().navigate(R.id.action_checkListFillingPreUsoFragment_to_listCheckListPreUsoFragment)
            } else {
                Toast.makeText(requireContext(), "Necesita Completar el Test", Toast.LENGTH_SHORT).show()
            }
        }
        NewCheckListScope.scope = ScopesChecklist.CHECKLIST_FILLING
    }

    override fun onCardImgClick(question: Question, position: Int) {
        if (hasCameraPermission())
        cameraRequestListener?.requestCamera(args.position, position)
        else requestCameraPermission()
    }

    override fun onResponseChange(answer: TypeAnswer) {
        /*val groupAnswer = adapter.getList()
        val allAnswersNotNo = groupAnswer.all { it.answer != TypeAnswer.NO && it.isCritical || !it.isCritical}
        btnSaveAnswers.isEnabled = allAnswersNotNo*/
    }

    private fun hasCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            102
        )
    }

    override fun updateFragmentList(catId: Int) {
        adapter.notifyItemChanged(catId)
    }

}

interface SaveAnswersButtonListener{
    fun onResponseChange(answer : TypeAnswer)
}