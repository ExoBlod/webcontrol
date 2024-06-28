package com.webcontrol.angloamerican.ui.bookcourses.ui.bookcourseshistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.webcontrol.angloamerican.databinding.FragmentHistoryBookCoursesBinding
import com.webcontrol.angloamerican.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.HistoryBookCourseData
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesViewModel
import com.webcontrol.angloamerican.ui.bookcourses.adapter.BookCoursesHistoryAdapter
import com.webcontrol.angloamerican.ui.bookcourses.data.ReserveCourseData
import com.webcontrol.core.utils.SharedUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookCoursesHistoryFragment :
    BaseFragment<FragmentHistoryBookCoursesBinding, BookCoursesHistoryViewModel>(),
    BookCoursesHistoryAdapter.OnButtonClickListener {

    private val adapter: BookCoursesHistoryAdapter by lazy {
        BookCoursesHistoryAdapter(listOf(), this@BookCoursesHistoryFragment)
    }
    private val parentViewModel: BookCoursesViewModel by activityViewModels()
    override fun getViewModelClass() = BookCoursesHistoryViewModel::class.java
    override fun getViewBinding() = FragmentHistoryBookCoursesBinding.inflate(layoutInflater)

    private var idWorker: String? = ""
    override fun setUpViews() {
        super.setUpViews()
        binding.rcvInspectionHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvInspectionHistory.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idWorker = parentViewModel.uiReserveCourse.workerId
        viewModel.getHistory(idWorker!!)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listHistory.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    HistoryBookCoursesUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo historico")
                    }

                    HistoryBookCoursesUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }

                    HistoryBookCoursesUIEvent.Error -> {

                    }

                    is HistoryBookCoursesUIEvent.Success -> {
                        if (event.listHistory.isNotEmpty()) {
                            showHistoryList(event.listHistory)
                            /*if (parentViewModel.uiHistoryList != event.listHistory){
                                parentViewModel.setHistoryList(event.listHistory)

                            }*/
                        } else {
                            showEmptyHistory()
                        }
                    }

                    is HistoryBookCoursesUIEvent.SuccessSearchByCheckingHead -> {
                    }
                }
            }
        }

        binding.btnNewBooking.setOnClickListener {
            onShowSomethingButtonClick()
        }

        binding.btnBookedCourses.setOnClickListener {
            getBookedFragment()
        }
    }

    private fun onShowSomethingButtonClick() {
        //!Create a new reservation
        findNavController().navigate(
            R.id.action_historyBookFragment_to_newBookCourses
        )
    }

    private fun getBookedFragment() {
        findNavController().navigate(
            R.id.action_historyBookFragment_to_bookedCourses
        )
    }

    private fun showHistoryList(listHistory: List<HistoryBookCourseData>) = with(binding) {
        animationInspectionHistory.visibility = View.GONE
        tvNoRegister.visibility = View.GONE
        adapter.setList(listHistory)
    }

    private fun showBookHistoryList(listHistory: List<HistoryBookCourseData>) = with(binding) {
        animationInspectionHistory.visibility = View.GONE
        tvNoRegister.visibility = View.GONE
        adapter.setList(listHistory)
    }

    private fun showEmptyHistory() = with(binding) {
        animationInspectionHistory.visibility = View.VISIBLE
        tvNoRegister.visibility = View.VISIBLE
    }

    override fun onButtonClick(data: HistoryBookCourseData) {
        TODO("Not yet implemented")
    }
}