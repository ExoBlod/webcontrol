package com.webcontrol.angloamerican.ui.bookcourses.ui.inputBookingCourses

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.COURSE_ID
import com.webcontrol.angloamerican.data.ID_PROGRAM
import com.webcontrol.angloamerican.data.network.response.CourseData
import com.webcontrol.angloamerican.databinding.FragmentInputBookingBinding
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesViewModel
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.core.utils.LocalStorage
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class InputBookingCoursesFragment :
    BaseFragment<FragmentInputBookingBinding, InputBookingCoursesViewModel>() {

    private val parentViewModel: BookCoursesViewModel by activityViewModels()
    override fun getViewModelClass() = InputBookingCoursesViewModel::class.java
    override fun getViewBinding() = FragmentInputBookingBinding.inflate(layoutInflater)
    private val calendar: Calendar = Calendar.getInstance()
    private var idProgram: Int = 0
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaActual = dateFormat.format(calendar.time)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val formattedDate = "${year}${String.format("%02d", month)}${String.format("%02d", day)}"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idProgram = parentViewModel.uiReserveCourse.idProgram
        binding.etPlate.setText(fechaActual)
        binding.etUserName.setText(parentViewModel.uiReserveCourse.workerId)
        binding.etCourseName.setText(idProgram.toString())
        binding.btnBookedCourses.setOnClickListener {
                viewModel.sendCourse(
                parentViewModel.uiReserveCourse.workerId,
                idProgram,
                formattedDate,
            )
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.successSending.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    InputBookingCoursesUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Cargando ...")
                    }

                    InputBookingCoursesUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }

                    InputBookingCoursesUIEvent.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Error al reservar el curso",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is InputBookingCoursesUIEvent.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "Curso reservado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(
                            R.id.action_inputBooking_to_historyBookFragment
                        )
                    }

                }
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            calendar.set(year, monthOfYear, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)
            binding.etPlate.setText(selectedDate)
        }
}