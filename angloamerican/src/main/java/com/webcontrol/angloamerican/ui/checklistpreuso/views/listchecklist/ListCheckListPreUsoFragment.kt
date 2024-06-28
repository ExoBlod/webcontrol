package com.webcontrol.angloamerican.ui.checklistpreuso.views.listchecklist

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.FragmentPreUsoChecklistVehicularInspectionBinding
import com.webcontrol.angloamerican.databinding.PopupInformationBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoActivity
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoViewModel
import com.webcontrol.angloamerican.ui.checklistpreuso.adapters.ChecklistGroupListener
import com.webcontrol.angloamerican.ui.checklistpreuso.adapters.ListChecklistAdapter
import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListScope
import com.webcontrol.angloamerican.ui.checklistpreuso.data.ScopesChecklist
import com.webcontrol.angloamerican.ui.checklistpreuso.data.TypeAnswer
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.AppDataBase
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.entity.NewCheckListGroups
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.entity.NewCheckListQuestions
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.angloamerican.utils.Utils
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.popup_add_comment_on_save.editTextMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ListCheckListPreUsoFragment :
    BaseFragment<FragmentPreUsoChecklistVehicularInspectionBinding, ListChecklistViewModel>(),
    ChecklistGroupListener {

    private val parentViewModel: CheckListPreUsoViewModel by activityViewModels()
    private val adapter: ListChecklistAdapter by lazy {
        ListChecklistAdapter(listOf(),this, parentViewModel.uiClearHistory)
    }
    private val db: AppDataBase by inject()
    val menuCheckList = arrayListOf<NewCheckListGroups>()
    val chekListQuestionDB = arrayListOf<NewCheckListQuestions>()
    private val questionsSaved : String = "RESPUESTAS GUARDADAS CON EXITO"
    private val questionsUpdated : String = "RESPUESTAS ACTUALIZADAS CON EXITO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "CheckList"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (parentViewModel.uiDisabledBtn) {
                    findNavController().navigate(R.id.action_listCheckListPreUsoFragment_to_historyPreUsoFragment)
                } else {
                    Toast.makeText(requireContext(), "No puede volver hacia atrás", Toast.LENGTH_SHORT).show()
                }
            }
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getViewModelClass() = ListChecklistViewModel::class.java
    override fun getViewBinding() =
        FragmentPreUsoChecklistVehicularInspectionBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnIconQuestion.setOnClickListener { Utils.infoChecklist(requireContext()) }
        binding.helperText.setOnClickListener { Utils.infoChecklist(requireContext()) }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listCheckList.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    ListChecklistUIEvent.ShowLoading -> {
                        (requireActivity() as CheckListPreUsoActivity).showLoading(true)
                    }
                    ListChecklistUIEvent.HideLoading -> {
                        (requireActivity() as CheckListPreUsoActivity).showLoading(false)
                    }
                    is ListChecklistUIEvent.Error -> {
                        event.error.message?.let { Log.e("ListCheckListPreUsoFrg", it) }
                        Toast.makeText(activity, "Ocurrió un error cargando la informacion", Toast.LENGTH_SHORT).show()

                        if (parentViewModel.uiListGroup.isNotEmpty()) {
                            showList(parentViewModel.uiListGroup)
                        }
                    }
                    is ListChecklistUIEvent.Success -> {
                        adapter.setList(event.questionListResponse)
                        val userId = SharedUtils.getUsuarioId(requireContext())

                        if (event.questionListResponse.isNotEmpty()) {
                            parentViewModel.setListQuestions(event.questionListResponse)
                            //parentViewModel.uiListGroup[0].checkIdHead = parentViewModel.uiCheckingHead
                            val test = parentViewModel.uiListGroupLiveData
                        }

                        event.questionListResponse.forEach { it.questions.forEach { it.usrCrea = userId } }
                        if (event.questionListResponse.isNotEmpty()) {
                            parentViewModel.setListQuestions(event.questionListResponse)
                            showList(event.questionListResponse)
                            for (listCheck in event.questionListResponse){
                                menuCheckList.add(
                                    NewCheckListGroups(
                                        idCheck =listCheck.idCheck,
                                        checkIdHead = parentViewModel.uiCheckingHead,
                                        checkListType = listCheck.checklistType,
                                        checkListActivo = listCheck.checklistActivo,
                                        nameCheck = listCheck.nameCheck,
                                        checkIdGroup = listCheck.checkIdGroup,
                                        nameGroup = listCheck.nameGroup
                                    )
                                )
                                for (listQuestion in listCheck.questions){
                                    chekListQuestionDB.add(NewCheckListQuestions(
                                        idTipo = listQuestion.idTipo,
                                        idCheck = listQuestion.idCheck,
                                        idCheckGroup = listQuestion.groupId,
                                        nombreCheckGroup = listQuestion.groupName,
                                        idCheckDet = listQuestion.idCheckDet,
                                        nombre = listQuestion.name,
                                        descripcion = listQuestion.description,
                                        regFoto = listQuestion.reqPhoto,
                                        orden = listQuestion.order,
                                        usrCrea = listQuestion.usrCrea,
                                        fecCrea = listQuestion.usrCrea,
                                        tipo = listQuestion.type.toString(),
                                        valor = listQuestion.answer.toString(),
                                        valorMult = listQuestion.valorMult,
                                        critico = listQuestion.isCritical,
                                        answer = listQuestion.answer
                                    ))
                                }
                            }
                            db.checkListPreUso().insertMenuList(menuCheckList)
                            db.checkListPreUso().insertAnswer(chekListQuestionDB)
                        }
                    }
                    is ListChecklistUIEvent.SuccessAnswerSaving -> {
                        if(event.listSaveAnswersResponse.isNotEmpty()){
                            val response = event.listSaveAnswersResponse[0].result

                            if(response.equals(questionsSaved)){
                                Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                                findNavController().navigate(R.id.action_listCheckListPreUsoFragment_to_historyPreUsoFragment)
                            } else if (response.equals(questionsUpdated)){
                                Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                            }
                            else {
                                Toast.makeText(requireContext(), "Hubo un error guardando la información", Toast.LENGTH_LONG).show()
                            }
                        }
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
                viewModel.getListCheckList(parentViewModel.uiVehicle.typeVehicle)
            } else {
                showList(parentViewModel.uiListGroup)
                adapter.setList(parentViewModel.uiListGroup)
            }
        } else {
            if (parentViewModel.uiListGroup.isEmpty()) {
               /* parentViewModel.setListQuestions(db.checkListPreUso().getGroup().map {
                    it.toMapper(
                        db.checkListPreUso().getAnswer(it.checkIdGroup).map { it.toMapper() })
                })*/
            }
            showList(parentViewModel.uiListGroup)
        }

        if (parentViewModel.uiDisabledBtn && !parentViewModel.uiCheckListStatus) {
            val questionList = parentViewModel.uiListGroup
            val allQuestionsAnswered = questionList.all { group ->
                group.questions.all { question ->
                    question.answer != TypeAnswer.NN
                }
            }
            binding.btnContinue.isEnabled = !allQuestionsAnswered
            binding.btnContinue.visibility = View.GONE
        } else {
            binding.btnContinue.isEnabled = true
        }

        binding.btnContinue.setOnClickListener {
            var status = "E2"
            var showNotification = false
            parentViewModel.uiListGroup.forEach group@ { group ->
                group.checkIdHead = parentViewModel.uiCheckingHead
                    group.questions.forEach question@{ question ->
                        if (question.isCritical && question.answer == TypeAnswer.NO) {
                            showNotification = true
                            status = "E4"
                            return@question
                        } else if (question.answer == TypeAnswer.NO) {
                            status = "E3"
                        }
                        if (question.answer == null) question.answer = TypeAnswer.NN
                    }
                parentViewModel.uiListGroup.forEach { group ->
                    if (!group.isComplete()) {
                        status = "E1"
                    }
                }
                    group.status = status
            }

            if (parentViewModel.isCompleteGroup()) {
                db.checkListPreUso().clean()
                for (listQuestion in parentViewModel.uiListGroup) {
                    listQuestion.status = status
                    for (listQuestion in listQuestion.questions) {
                        chekListQuestionDB.add(
                            NewCheckListQuestions(
                                idTipo = listQuestion.idTipo,
                                idCheck = listQuestion.idCheck,
                                idCheckGroup = listQuestion.groupId,
                                nombreCheckGroup = listQuestion.groupName,
                                idCheckDet = listQuestion.idCheckDet,
                                nombre = listQuestion.name,
                                descripcion = listQuestion.description,
                                regFoto = listQuestion.reqPhoto,
                                orden = listQuestion.order,
                                usrCrea = listQuestion.usrCrea,
                                fecCrea = listQuestion.usrCrea,
                                tipo = listQuestion.type.toString(),
                                valor = listQuestion.answer.toString(),
                                valorMult = listQuestion.valorMult,
                                critico = listQuestion.isCritical,
                                answer = listQuestion.answer
                            )
                        )
                    }
                }
                db.checkListPreUso().insertAnswer(chekListQuestionDB)
                //viewModel.noHacer()
            }

            //if (parentViewModel.isCompleteGroup()) {

                if(SharedUtils.isOnline(requireContext())){
                    if (showNotification)
                        showNotification(requireContext())
                    else
                        askForAdditionalCommentDialog(requireContext())
                }
                else{
                    //getNo()
                    //findNavController().popBackStack(R.id.listCheckListFragment, false)
                    //findNavController().navigate(R.id.action_listCheckListFragment_to_evidencePhotoFragment)
                }
           /* } else
                Toast.makeText(
                    requireContext(),
                    "Debe Completar Todos Los Test",
                    Toast.LENGTH_SHORT
                ).show()*/
        }
    }

    private fun askForAdditionalCommentDialog(context: Context){
        val dialogBinding = layoutInflater.inflate(R.layout.popup_add_comment_on_save, null)
        val confirmDialog = Dialog(context)
        val yesBtn = dialogBinding.findViewById<Button>(R.id.btnOK)
        val cancelBtn = dialogBinding.findViewById<Button>(R.id.btnCancel)
        with(confirmDialog) {
            setContentView(dialogBinding)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            yesBtn.setOnClickListener {
                dismiss()
                val message = editTextMessage.text.toString()
                val listGroup = parentViewModel.uiListGroup.toMutableList()
                listGroup.forEach{ group ->
                    group.questions.forEach { question ->
                        if(question.photo.isNotEmpty()){
                            val uri: Uri = Uri.parse(question.photo)
                            question.photo = transformUriIntoBitmap(uri)
                        }
                    }
                }
                listGroup[0].checkComment = message
                parentViewModel.setListQuestions(listGroup)
                viewModel.saveAnswers(listGroup)
            }
            cancelBtn.setOnClickListener { dismiss() }
            show()
        }
    }

    fun showNotification(context: Context) {
        val dialogBinding = DataBindingUtil.inflate<PopupInformationBinding>(
            LayoutInflater.from(context),
            R.layout.popup_information,
            null,
            false
        )

        val myDialog = Dialog(context)
        val yesBtn = dialogBinding.btnOK
        val title = dialogBinding.title
        val first = dialogBinding.first
        val second = dialogBinding.second
        val third = dialogBinding.textInputLayoutPlate

        title.text = context.getString(R.string.popup_add_comment_title)
        first.text = context.getString(R.string.critical_answers)
        second.visibility = View.GONE
        third.visibility = View.GONE

        with(myDialog) {
            setContentView(dialogBinding.root)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            yesBtn.setOnClickListener {
                dismiss()
                askForAdditionalCommentDialog(context)
            }
        }
    }

    fun transformUriIntoBitmap(uri: Uri): String{
        val bitmap: Bitmap =  MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun showList(listChecklist: List<QuestionListResponse>) = with(binding) {
        val newCheckListGroup = listChecklist.map { it.reloadAnswers()}
        parentViewModel.setListQuestions(newCheckListGroup)
        adapter.setList(newCheckListGroup)
    }

    override fun onItemClickGroup(position: Int) {
        val action = ListCheckListPreUsoFragmentDirections.actionListCheckListPreUsoFragmentToCheckListFillingPreUsoFragment(position)
        findNavController().navigate(action)
    }
}