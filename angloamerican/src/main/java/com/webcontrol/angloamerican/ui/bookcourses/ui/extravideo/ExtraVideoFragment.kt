package com.webcontrol.angloamerican.ui.bookcourses.ui.extravideo

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.network.response.ResultExam
import com.webcontrol.angloamerican.databinding.FragmentCourseContentBinding
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesViewModel
import com.webcontrol.angloamerican.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_course_content.*

class ExtraVideoFragment : BaseFragment<FragmentCourseContentBinding, BookCoursesViewModel>() {

    private val parentViewModel: BookCoursesViewModel by activityViewModels()

    override fun getViewModelClass() = BookCoursesViewModel::class.java
    override fun getViewBinding() = FragmentCourseContentBinding.inflate(layoutInflater)

    var isFullscreen = false

    override fun setUpViews() {
        super.setUpViews()
        setVideoPlayer()
        onClick()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    override fun observeData() {
        super.observeData()

        val nameObserver = Observer<ResultExam> { resultExam ->
            resultExam?.let { exam ->
                val currentIndex = parentViewModel.uiResultExam.ListVideo.indexOfFirst { it.Video == exam.ListVideo.firstOrNull()?.Video }
                viewModel.currentContent.value = currentIndex + 1
                setVideoPlayer(currentIndex)
            }
        }
        parentViewModel.resultExamLiveData.observe(viewLifecycleOwner, nameObserver)
    }


    private fun onClick() {
        binding.btnBookedCourses.setOnClickListener {
            val currentIndex = viewModel.currentContent.value ?: 0
            if (currentIndex < parentViewModel.uiResultExam.ListVideo.size - 1) {
                viewModel.setCurrentContent(currentIndex + 1)
                setVideoPlayer(currentIndex + 1)
            } else {
                findNavController().navigate(R.id.action_extraVideo_to_historyBookFragment)
            }
        }
    }

    private fun setVideoPlayer(currentIndex: Int? = null) {
        val videoIndex = currentIndex ?: 0
        val video = parentViewModel.uiResultExam.ListVideo.getOrNull(videoIndex) ?: return
        val videoUrl = getVideoUrl(video.Video)
        val videoView = binding.videoView
        val mediaController = MediaController(requireContext())

        videoView.setMediaController(mediaController)
        videoView.setVideoURI(Uri.parse(videoUrl))
        mediaController.setAnchorView(videoView)
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = false
        }
        binding.progressBar.visibility = View.GONE
        binding.pdfView.visibility = View.GONE
        binding.videoView.visibility = View.VISIBLE
        btnFullscreen.setOnClickListener {
            toggleFullscreen(videoView)
        }
        binding.txtPageContent.text = "Recurso Adicional ${video.IdDivision}"
        binding.tittleInputBooking.text = video.Division
    }


    private fun getVideoUrl(videoFileName: String): String {
        val videoBaseUrl = when (parentViewModel.uiContentCourse.firstOrNull()?.Version) {
            //M1->"https://m1.webcontrolcorp.com/WebControl42/examenes/archivos/"
            //AngloPruebas->"https://angloamerican.webcontrolcorp.com/v42/examenes/archivos/"
            //QA->"https://coppermtsqa.anglochile.cl/webcontrol42/examenes/archivos/"
            //PRD->"https://webcontrol.anglochile.cl/Webcontrol42/examenes/archivos/"

            "1" -> "https://webcontrol.anglochile.cl/Webcontrol42/examenes/archivos/"

            //M1->"https://m1.webcontrolcorp.com/examenes/archivos/"
            //AngloPruebas->"https://angloamerican.webcontrolcorp.com/examenes/archivos/"
            //QA->"https://coppermtsqa.anglochile.cl/saac/examenes/archivos/"
            //PRD->"https://webcontrol.anglochile.cl/saac/examenes/archivos/"

            else -> "https://webcontrol.anglochile.cl/saac/examenes/archivos/"
        }
        val encodedNameFile = videoFileName.replace("{", "%7B").replace("}", "%7D")
        return "$videoBaseUrl$encodedNameFile"
    }

    private fun toggleFullscreen(videoView: VideoView) {
        isFullscreen = !isFullscreen
        if (isFullscreen) {
            enterVideoFullscreen(videoView)
        } else {
            exitVideoFullscreen(videoView)
        }
    }

    private fun enterVideoFullscreen(videoView: VideoView) {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val layoutParams = videoView.layoutParams
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.contraer)
        setFullscreenLayoutParams(layoutParams)
        videoView.layoutParams = layoutParams
        binding.btnBookedCourses.visibility = View.GONE
        binding.btnFullscreen.setImageDrawable(drawable)
    }

    private fun exitVideoFullscreen(videoView: VideoView) {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        val layoutParams = videoView.layoutParams
        setNormalLayoutParams(layoutParams)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.expandir)
        videoView.layoutParams = layoutParams
        binding.btnBookedCourses.visibility = View.VISIBLE
        binding.btnFullscreen.setImageDrawable(drawable)
    }

    private fun setNormalLayoutParams(params: ViewGroup.LayoutParams) {
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = resources.getDimensionPixelSize(R.dimen.video_height)
    }

    private fun setFullscreenLayoutParams(params: ViewGroup.LayoutParams) {
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
    }
}
