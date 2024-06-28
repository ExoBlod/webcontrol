package com.webcontrol.angloamerican.ui.bookcourses.ui.resulttest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.ListVideo
import com.webcontrol.angloamerican.databinding.FragmentResultTestBookingCourseBinding
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesViewModel
import com.webcontrol.angloamerican.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultTestFragment :
    BaseFragment<FragmentResultTestBookingCourseBinding, ResultTestViewModel>() {

    override fun getViewModelClass() = ResultTestViewModel::class.java
    private val parentViewModel: BookCoursesViewModel by activityViewModels()
    private var opcionSeleccionada: Int = -1
    private var nombreVideoSeleccionado: String? = null


    override fun getViewBinding() = FragmentResultTestBookingCourseBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val correctAnswers = parentViewModel.uiResultExam.Nota
        val aprobo = parentViewModel.uiResultExam.Aprobo
        val resultIconImageView = binding.imgResultIcon
        val aprovedEditText = binding.txtUser
        val isExtraVideo: List<ListVideo> = listOf()

        if (aprobo == "NO") {
            resultIconImageView.setImageResource(R.drawable.sadface)
            aprovedEditText.text = "No ha superado el puntaje mínimo de aprobación"
        } else {
            resultIconImageView.setImageResource(R.drawable.ic_check_circle_green_24dp)
            aprovedEditText.text = "Usted ha aprobado satisfactoriamente"
        }

        val txtCorrectAnsw = binding.txtCorrectAnsw
        txtCorrectAnsw.text = "Respuestas Correctas: $correctAnswers%"

        binding.btnBookedCourses.setOnClickListener() {
            isExtraVideo()
        }
    }

    fun isExtraVideo() {
        if (parentViewModel.uiResultExam.ListVideo.isNotEmpty() && parentViewModel.uiResultExam.Aprobo == "SI") {
            findNavController().navigate(R.id.action_resultTestBookingCourse_to_extraVideo)
        } else {
            findNavController().navigate(R.id.action_resultTestBookingCourse_to_historyBookFragment)
        }
    }
}
