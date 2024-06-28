package com.webcontrol.angloamerican.ui.bookcourses.ui.coursecontent

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.FragmentCourseContentBinding
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesViewModel
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.angloamerican.utils.getExtension
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_course_content.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CourseContentFragment : BaseFragment<FragmentCourseContentBinding, CourseContentViewModel>() {

    private val parentViewModel: BookCoursesViewModel by activityViewModels()
    override fun getViewModelClass() = CourseContentViewModel::class.java
    override fun getViewBinding() = FragmentCourseContentBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getBookedCourses(
            parentViewModel.uiReserveCourse.idProgram,
            parentViewModel.uiReserveCourse.idExam
        )
    }
    var isFullscreen = false

    override fun setUpViews() {
        super.setUpViews()
        onClick()
        mockUp()
    }

    override fun observeData() {
        super.observeData()

        val nameObserver = Observer<Int> { newCurrent ->
            binding.txtPageContent.text =
                "Recurso $newCurrent de ${parentViewModel.uiContentCourse.size}"
            parentViewModel.uiContentCourse[newCurrent - 1].Content.let {
                when (it.getExtension()?.lowercase()) {
                    "mp4" -> setVideoPlayer(it)
                    "pdf" -> setFilePlayer(it)
                    "zip" -> setFilePlayer(
                        it,
                        true,
                        parentViewModel.uiContentCourse[newCurrent - 1].Inicio
                    )
                    else -> {}
                }
            }

            if (parentViewModel.uiContentCourse.size == newCurrent)
                binding.btnBookedCourses.text = "Ir al Examen"
        }

        viewModel.currentContent.observe(this, nameObserver)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contentCourses.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    CourseContentUIEvent.ShowLoading -> {}
                    CourseContentUIEvent.HideLoading -> {}
                    CourseContentUIEvent.Error -> {}
                    is CourseContentUIEvent.Success -> {
                        if (event.contentCourses.isNotEmpty()) {
                            parentViewModel.setContentCourses(event.contentCourses)
                            viewModel.currentContent.setValue(1)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "El curso no tiene contenido",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        mockUp()
    }

    private fun mockUp() {
        binding.tittleInputBooking.text = "Curso"
    }

    private fun onClick() {
        binding.btnBookedCourses.setOnClickListener() {
            if (parentViewModel.uiContentCourse.size == viewModel.currentContent.value)
                findNavController().navigate(R.id.action_courseContent_to_testBookingCourse)
            else {
                viewModel.currentContent.setValue((viewModel.currentContent.value ?: 0) + 1)
            }
        }
    }

    private fun setFilePlayer(nameFile: String, typeScorm: Boolean = false, init: String = "") {
        val settings = binding.pdfView.settings

        // Enable java script in web view.
        settings.javaScriptEnabled = true

        // Enable DOM storage API.
        settings.domStorageEnabled = true

        // Enable zooming in web view.
        settings.setSupportZoom(true)

        // Allow pinch to zoom.
        settings.builtInZoomControls = true

        // Disable the default zoom controls on the page.
        settings.displayZoomControls = false

        // Enable responsive layout.
        settings.useWideViewPort = false

        // Zoom out if the content width is greater than the width of the viewport.
        settings.loadWithOverviewMode = false

        val url = if (!typeScorm) {
            val pdfFilePath = "https://webcontrol.anglochile.cl/saac/examenes/archivos/${
                nameFile.replace(
                    "{",
                    "%7B"
                ).replace("}", "%7D")
            }"
            "https://drive.google.com/viewerng/viewer?embedded=true&url=$pdfFilePath"
        } else {
            "https://webcontrol.anglochile.cl/saac/examenes/archivos/" + nameFile.replace(
                ".zip",
                ""
            ) + "/$init"
        }
        binding.pdfView.loadUrl(url)

        binding.progressBar.visibility = View.GONE
        binding.pdfView.visibility = View.VISIBLE
        binding.videoView.visibility = View.GONE

        btnFullscreen.setOnClickListener {
            toggleWebViewFullscreen()
        }
    }

    private fun setVideoPlayer(nameFile: String) {
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
        val encodedNameFile = nameFile.replace("{", "%7B").replace("}", "%7D")
        val videoUri = "$videoBaseUrl$encodedNameFile"
        val videoView = binding.videoView
        val mediaController = MediaController(requireContext())

        videoView.setMediaController(mediaController)
        videoView.setVideoURI(Uri.parse(videoUri))
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
        params.height = resources.getDimensionPixelSize(R.dimen.video_height) // Usa el tamaño original o el que desees
    }

    private fun setFullscreenLayoutParams(params: ViewGroup.LayoutParams) {
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun toggleWebViewFullscreen() {
        isFullscreen = !isFullscreen

        if (isFullscreen) {
            enterFullscreen()
        } else {
            exitFullscreen()
        }
    }

    private fun enterFullscreen() {
        val params = binding.pdfView.layoutParams as ConstraintLayout.LayoutParams
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        binding.pdfView.layoutParams = params
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.contraer)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding.btnBookedCourses.visibility = View.GONE
        binding.btnFullscreen.setImageDrawable(drawable)
    }

    private fun exitFullscreen() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        val params = binding.pdfView.layoutParams as ConstraintLayout.LayoutParams
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.expandir)
        params.height = 0 // Restaurar la altura original
        binding.pdfView.layoutParams = params
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding.btnBookedCourses.visibility = View.VISIBLE
        binding.btnFullscreen.setImageDrawable(drawable)
    }
}