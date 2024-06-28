package com.webcontrol.angloamerican.ui.checklistpreuso.views.takeevidencephoto

import android.os.Bundle
import android.view.View
import com.webcontrol.angloamerican.databinding.FragmentPreUsoEvidenceInspectionBinding
import com.webcontrol.angloamerican.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TakeEvidencePhotoFragment :
    BaseFragment<FragmentPreUsoEvidenceInspectionBinding, TakeEvidencePhotoViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Toma de Evidencias"
    }

    override fun getViewModelClass() = TakeEvidencePhotoViewModel::class.java
    override fun getViewBinding() = FragmentPreUsoEvidenceInspectionBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.lblDate.text = currentDate

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding.lblHour.text = currentTime


        binding.btnTakePhoto.setOnClickListener {
            //checkCameraPermissionAndTakePhoto(cameraView)
        }

        binding.btnSendEvidence.setOnClickListener {

        }

    }
}
