package com.webcontrol.angloamerican.ui.bookcourses.ui.testBookingCourse

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.QuestionData
import com.webcontrol.angloamerican.data.network.response.ResultExam
import com.webcontrol.angloamerican.databinding.FragmentTestBookingCourseBinding
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesViewModel
import com.webcontrol.angloamerican.ui.bookcourses.adapter.QuestionCourseAdapter
import com.webcontrol.angloamerican.ui.bookcourses.adapter.QuestionPageAdapter
import com.webcontrol.angloamerican.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@AndroidEntryPoint
class TestBookingCourseFragment :
    BaseFragment<FragmentTestBookingCourseBinding, TestBookingCourseViewModel>(),
    QuestionPageAdapter.OnButtonClickListener, QuestionCourseAdapter.OnButtonClickAnswerListener {
    private val parentViewModel: BookCoursesViewModel by activityViewModels()
    override fun getViewModelClass() = TestBookingCourseViewModel::class.java
    override fun getViewBinding() = FragmentTestBookingCourseBinding.inflate(layoutInflater)
    private val adapter: QuestionCourseAdapter by lazy {
        QuestionCourseAdapter(listOf(), this@TestBookingCourseFragment)
    }
    private val adapterPageCount: QuestionPageAdapter by lazy {
        QuestionPageAdapter(0, this@TestBookingCourseFragment)
    }

    private var listQuestions: List<QuestionData> = listOf()
    private var sizeQuestionForPage: Int = 0
    override fun setUpViews() {
        super.setUpViews()

        binding.rcvTestVehicularInspection.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvTestVehicularInspection.adapter = adapter

        binding.rcvTestQuestionsPages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcvTestQuestionsPages.adapter = adapterPageCount
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getQuestionList(parentViewModel.uiReserveCourse.idExam)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.testBookingEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    TestBookingCourseUIEvent.ShowLoading -> {
                        Toast.makeText(requireContext(), "Espere ...", Toast.LENGTH_SHORT).show()
                    }

                    TestBookingCourseUIEvent.HideLoading -> {
                    }

                    TestBookingCourseUIEvent.Error -> {
                    }

                    is TestBookingCourseUIEvent.Success -> {
                        Toast.makeText(requireContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show()
                        if (event.listQuestions.isNotEmpty()) {
                            listQuestions = event.listQuestions
                            sizeQuestionForPage = event.listQuestions[0].questionPagesize
                            adapterPageCount.setNumberPage(
                                roundUp(
                                    event.listQuestions.size,
                                    sizeQuestionForPage
                                )
                            )
                            val questionsForPage = getQuestForPage(0)
                            adapter.setList(questionsForPage)
                        }
                    }

                    is TestBookingCourseUIEvent.SuccessSendQuestionList -> {
                        if (event.resultExam.Aprobo.isNotEmpty()) {
                            parentViewModel.setResultExam(event.resultExam)
                            /*Toast.makeText(
                                requireContext(),
                                "Resultado: ${event.resultExam
                                    .Aprobo}",
                                Toast.LENGTH_SHORT
                            ).show()*/
                        }
                        findNavController().navigate(R.id.action_testBookingCourse_to_resultTestBookingCourse)
                    }
                }
            }
        }

        binding.btnSaveAnswers.setOnClickListener {
            showLoader()
            val idCharla = parentViewModel.uiReserveCourse.idCourse
            val idWorker = parentViewModel.uiReserveCourse.workerId
            val enterprise = parentViewModel.uiReserveCourse.idEnterprise
            viewModel.sendQuestionList(
                idCharla,
                idWorker,
                enterprise,
                listQuestions
            )
        }
    }

    private fun roundUp(numerator: Int, denominator: Int): Int {
        return if (numerator % denominator == 0) {
            numerator / denominator
        } else {
            numerator / denominator + 1
        }
    }

    override fun onItemAnswerClick(answerId: Int, questionType: Int, isChecked: Boolean) {
        val questionWithMarkedAnswer = listQuestions.find { question ->
            question.answerQuestionCourses.any { it.answerId == answerId }
        }
        if (questionWithMarkedAnswer != null) {
            if (isChecked) {
                when (questionType) {
                    1 -> {
                        questionWithMarkedAnswer.answerQuestionCourses.forEach {
                            it.answerMarked = false
                        }
                        val markedAnswer =
                            questionWithMarkedAnswer.answerQuestionCourses.find { it.answerId == answerId }
                        if (markedAnswer != null) {
                            markedAnswer.answerMarked = true
                        }
                    }

                    2 -> {
                        questionWithMarkedAnswer.answerQuestionCourses.forEach {
                            it.answerMarked = false
                        }
                        val markedAnswer =
                            questionWithMarkedAnswer.answerQuestionCourses.find { it.answerId == answerId }
                        if (markedAnswer != null) {
                            markedAnswer.answerMarked = true
                        }
                    }

                    3 -> {
                        val markedAnswer =
                            questionWithMarkedAnswer.answerQuestionCourses.find { it.answerId == answerId }
                        if (markedAnswer != null) {
                            markedAnswer.answerMarked = true
                        }
                    }
                }
            } else {
                val markedAnswer =
                    questionWithMarkedAnswer.answerQuestionCourses.find { it.answerId == answerId }
                if (markedAnswer != null) {
                    markedAnswer.answerMarked = false
                }
            }

        }
    }

    override fun onItemClick(positionPage: Int) {
        val questionsForPage = getQuestForPage(positionPage)
        adapter.setList(questionsForPage)
    }

    private fun getQuestForPage(pageIndex: Int): List<QuestionData> {
        val startIndex = pageIndex * sizeQuestionForPage
        val endIndex = minOf((startIndex + sizeQuestionForPage), listQuestions.size)
        if (startIndex >= listQuestions.size) {
            return emptyList()
        }
        return listQuestions.subList(startIndex, endIndex)
    }

    private fun showLoader() {
        binding.loader.visibility = View.VISIBLE
        binding.scrollView.visibility = View.GONE
    }

    private fun hideLoader() {
        binding.loader.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE
    }
}