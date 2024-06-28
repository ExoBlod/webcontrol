package com.webcontrol.angloamerican.ui.bookcourses.ui.bookedCourses

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.BookedCoursesData
import com.webcontrol.angloamerican.databinding.FragmentBookedCoursesBinding
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesViewModel
import com.webcontrol.angloamerican.ui.bookcourses.adapter.BookedCoursesAdapter
import com.webcontrol.angloamerican.ui.bookcourses.data.ReserveCourseData
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookedCoursesFragment :
    BaseFragment<FragmentBookedCoursesBinding, BookedCoursesViewModel>(),
    BookedCoursesAdapter.OnButtonClickListener {

    private val adapter: BookedCoursesAdapter by lazy {
        BookedCoursesAdapter(listOf(), this@BookedCoursesFragment)
    }
    private val parentViewModel: BookCoursesViewModel by activityViewModels()
    override fun getViewModelClass() = BookedCoursesViewModel::class.java

    override fun getViewBinding() = FragmentBookedCoursesBinding.inflate(layoutInflater)

    override fun setUpViews() {
        super.setUpViews()

        binding.rcvInspectionHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvInspectionHistory.adapter = adapter
        binding.btnBookedHistory.setOnClickListener {
            findNavController().navigate(R.id.action_bookedCourses_to_historyBookFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getBookedCourses(parentViewModel.uiReserveCourse.workerId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bookedCourses.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    BookedCoursesUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), resources.getString(R.string.loading_history))
                    }

                    BookedCoursesUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }

                    BookedCoursesUIEvent.Error -> {

                    }

                    is BookedCoursesUIEvent.Success -> {
                        if (event.bookedCourses.isNotEmpty()) {
                            showHistoryList(event.bookedCourses)
                            /*if (parentViewModel.uiHistoryList != event.listHistory){
                                parentViewModel.setHistoryList(event.listHistory)

                            }*/
                        } else {
                            showEmptyHistory()
                        }
                    }
                }
            }
        }
    }

    private fun showHistoryList(listHistory: List<BookedCoursesData>) = with(binding) {

        adapter.setList(listHistory)
    }

    private fun showEmptyHistory() = with(binding) {

    }

    override fun onButtonClick(data: BookedCoursesData) {
        parentViewModel.setReserveCourse(
            parentViewModel.uiReserveCourse.copy(
                idProgram = data.ProgramId,
                idCourse = data.CourseId,
                idExam = data.ExamId
            )
        )
        navigateToCourseContent()
    }


    private fun navigateToCourseContent() {
        findNavController().navigate(R.id.action_bookedCourses_to_courseContent)
    }
}