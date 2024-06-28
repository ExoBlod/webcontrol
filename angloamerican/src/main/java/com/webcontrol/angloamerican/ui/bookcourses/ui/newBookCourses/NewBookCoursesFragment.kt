package com.webcontrol.angloamerican.ui.bookcourses.ui.newBookCourses

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.CourseData
import com.webcontrol.angloamerican.databinding.FragmentNewBookCoursesBinding
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesViewModel
import com.webcontrol.angloamerican.ui.bookcourses.adapter.NewBookCoursesAdapter
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewBookCoursesFragment :
    BaseFragment<FragmentNewBookCoursesBinding, NewBookCoursesViewModel>(),
    NewBookCoursesAdapter.OnItemClickListener {

    private val parentViewModel: BookCoursesViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    override fun getViewModelClass() = NewBookCoursesViewModel::class.java

    override fun getViewBinding() = FragmentNewBookCoursesBinding.inflate(layoutInflater)
    private val adapter: NewBookCoursesAdapter by lazy {
        NewBookCoursesAdapter(listOf(), this@NewBookCoursesFragment)
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.rcvInspectionNewBookCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvInspectionNewBookCourses.adapter = adapter
    }

    private fun showCoursesList(listCourses: List<CourseData>) = with(binding) {
        adapter.setList(listCourses)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rcvInspectionNewBookCourses
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listCourses.flowWithLifecycle(lifecycle).collectLatest {
                when (it) {
                    NewBookCoursesUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Obteniendo cursos")
                    }

                    NewBookCoursesUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }

                    NewBookCoursesUIEvent.Error -> {

                    }

                    is NewBookCoursesUIEvent.Success -> {
                        if (it.listNewBookCourses.isNotEmpty()) {
                            showCoursesList(it.listNewBookCourses)
                        }
                    }
                }
            }
        }
    }


    override fun onItemClick(idProgram: Int) {
        Toast.makeText(requireContext(), "idProgram: $idProgram", Toast.LENGTH_SHORT).show()
        parentViewModel.setReserveCourse(
            parentViewModel.uiReserveCourse.copy(
                idProgram = idProgram
            )
        )
        findNavController().navigate(
            R.id.action_newBookCourses_to_inputBooking,
        )
    }
}