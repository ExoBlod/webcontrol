package com.webcontrol.android.ui.newchecklist.views.takeevidencephoto

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentEvidenceInspectionBinding
import com.webcontrol.android.ui.newchecklist.NewCheckListViewModel
import com.webcontrol.android.ui.newchecklist.data.*
import com.webcontrol.android.util.BaseFragment
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TakeEvidencePhotoFragment :
    BaseFragment<FragmentEvidenceInspectionBinding, TakeEvidencePhotoViewModel>() {

    private val parentViewModel: NewCheckListViewModel by activityViewModels()
    private val CAMERA_PERMISSION_CODE = 102

    private val photo by lazy {
        WorkerSignature(SharedUtils.getUsuarioId(requireContext()), "")
    }
    val menuCheckList = arrayListOf<NewCheckListGroup>()
    var chekListQuestionDB = arrayListOf<NewCheckListQuestion>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Toma de Evidencias"
    }

    override fun getViewModelClass() = TakeEvidencePhotoViewModel::class.java
    override fun getViewBinding() = FragmentEvidenceInspectionBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.lblDate.text = currentDate

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding.lblHour.text = currentTime

        if (parentViewModel.uiListQuestion.isEmpty()) {
            val action =
                TakeEvidencePhotoFragmentDirections.actionEvidencePhotoFragmentToHistoryFragment()
            findNavController().navigate(action)
        }

        val cameraView = binding.cameraView
        cameraView.mode = Mode.PICTURE
        cameraView.facing = Facing.BACK
        cameraView.setLifecycleOwner(viewLifecycleOwner)

        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                val imagePath = File(requireContext().externalCacheDir, "temp_image.jpg")

                result.toFile(imagePath) { file ->
                    if (file != null) {
                        val imageBitmap = BitmapFactory.decodeFile(file.absolutePath)

                        cameraView.visibility = View.GONE

                        binding.imgPhotoEvidence.setImageBitmap(imageBitmap)
                        binding.imgPhotoEvidence.visibility = View.VISIBLE
                        photo.workerPhoto = convertBitmapToBase64(imageBitmap)
                    } else {
                        // (puede lanzar una excepción o mostrar un mensaje de error)
                    }
                }
            }
        })

        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndTakePhoto(cameraView)
        }

        binding.btnSendEvidence.setOnClickListener {
            if (photo.workerPhoto.isNotEmpty()) {
                parentViewModel.uiListQuestion[viewModel.numEvidenceSend].reqfoto =
                    photo.workerPhoto
                viewModel.numEvidenceSend += 1
                if (viewModel.numEvidenceSend <= parentViewModel.uiListQuestion.size) {
                    if (SharedUtils.isOnline(requireContext())) {
                        val newCheckListGroup = NewCheckListGroup(
                            data = parentViewModel.uiListQuestion,
                            idCheck = parentViewModel.uiListQuestion[0].iD_CHECK,
                            checkListType = parentViewModel.uiListGroup[0].checkListType,
                            checklistActivo = parentViewModel.uiListGroup[0].checklistActivo,
                            checkIdHead = parentViewModel.uiCheckingHead.result,
                            nameCheck = parentViewModel.uiListGroup[0].nameCheck,
                            nameGroup = parentViewModel.uiListGroup[0].nameGroup,
                            checkIdGroup = 0
                        )
                        viewModel.sendPhotoEvidence(newCheckListGroup)
                    } else {
                        App.db.checkListBambaDao().updateQuestion(
                            parentViewModel.uiListQuestion[viewModel.numEvidenceSend - 1].iD_CHECKDET,
                            parentViewModel.uiListQuestion[viewModel.numEvidenceSend - 1].iD_CHECKGROUP,
                            photo.workerPhoto
                        )
                        if (parentViewModel.uiListQuestion.size <= viewModel.numEvidenceSend) {
                            findNavController().navigate(R.id.action_evidencePhotoFragment_to_historyFragment)
                        } else {
                            bindingView(viewModel.numEvidenceSend)
                        }
                    }
                } else {
                    if (!SharedUtils.isOnline(requireContext())) {
                        for (listCheck in App.db.checkListBambaDao().getGroup()) {
                            chekListQuestionDB.clear()
                            for (listQuestion in App.db.checkListBambaDao()
                                .getAnswer(listCheck.checkIdGroup)) {
                                chekListQuestionDB.add(
                                    NewCheckListQuestion(
                                        iD_TIPO = listQuestion.idTipo,
                                        iD_CHECK = listQuestion.idCheck,
                                        iD_CHECKGROUP = listQuestion.idCheckGroup,
                                        nombrecheckgroup = listQuestion.nombreCheckGroup,
                                        iD_CHECKDET = listQuestion.idCheckDet,
                                        nombre = listQuestion.nombre,
                                        descripcion = listQuestion.descripcion,
                                        reqfoto = listQuestion.regFoto,
                                        orden = listQuestion.orden,
                                        usrcrea = listQuestion.usrCrea,
                                        feccrea = listQuestion.fecCrea,
                                        tipo = listQuestion.tipo,
                                        valor = listQuestion.valor,
                                        valormult = listQuestion.valorMult,
                                        critico = listQuestion.critico,
                                        answer = listQuestion.answer,
                                        tipopregunta = listQuestion.tipo,
                                        pregunta = listQuestion.tipo,
                                        grupo = ""
                                    )
                                )
                            }
                            menuCheckList.add(
                                NewCheckListGroup(
                                    idCheck = listCheck.idCheck,
                                    checkIdHead = listCheck.checkIdHead,
                                    checkListType = listCheck.checkListType,
                                    checklistActivo = listCheck.checkListActivo,
                                    nameCheck = listCheck.nameCheck,
                                    checkIdGroup = listCheck.checkIdGroup,
                                    nameGroup = listCheck.nameGroup,
                                    data = chekListQuestionDB
                                )
                            )
                        }
                        parentViewModel.setListQuestions(menuCheckList)
                        findNavController().navigate(R.id.action_evidencePhotoFragment_to_historyFragment)
                    }
                }
            } else
                Toast.makeText(requireContext(), "No hay una imagen guardada", Toast.LENGTH_SHORT)
                    .show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photoEvidence.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    TakeEvidencePhotoUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Enviando Evidencia")
                    }
                    TakeEvidencePhotoUIEvent.HideLoading -> {
                        SharedUtils.dismissLoader(requireContext())
                    }
                    TakeEvidencePhotoUIEvent.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Evidencia enviada",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (viewModel.numEvidenceSend > parentViewModel.uiListQuestion.size)
                            findNavController().navigate(R.id.action_evidencePhotoFragment_to_historyFragment)
                        else
                            bindingView(viewModel.numEvidenceSend)
                    }
                    is TakeEvidencePhotoUIEvent.Success -> {
                        if (event.responsePostEvidence) {
                            if (parentViewModel.uiListQuestion.size == viewModel.numEvidenceSend) {
                                findNavController().navigate(R.id.action_evidencePhotoFragment_to_historyFragment)
                            } else {
                                bindingView(viewModel.numEvidenceSend)
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Evidencia enviada",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (parentViewModel.uiListQuestion.size == viewModel.numEvidenceSend) {
                                findNavController().navigate(R.id.action_evidencePhotoFragment_to_historyFragment)
                            } else {
                                bindingView(viewModel.numEvidenceSend)
                            }
                        }
                    }
                }
            }
        }
        NewCheckListScope.scope = ScopesChecklist.EVIDENCE
    }

    override fun setUpViews() {
        super.setUpViews()
        if (parentViewModel.uiListQuestion.isNotEmpty()) {
            bindingView(viewModel.numEvidenceSend)
        }
    }

    private fun bindingView(position: Int = 0) {
        binding.imgPhotoEvidence.visibility = View.GONE
        binding.cameraView.visibility = View.VISIBLE
        val question = parentViewModel.uiListQuestion[position]
        binding.txtQuestion.text = question.nombre
        binding.textBoxEvidence.setText("")
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val imageBitmap = intent?.extras?.get("data") as Bitmap
                binding.imgPhotoEvidence.setImageBitmap(imageBitmap)
                photo.workerPhoto = convertBitmapToBase64(imageBitmap)
            } else {
                Toast.makeText(requireContext(), "No se pudo tomar la foto", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForResult.launch(intent)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun checkCameraPermissionAndTakePhoto(cameraView: CameraView) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraView.visibility = View.VISIBLE
            cameraView.takePicture()
        } else {
            requestCameraPermission()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permiso de cámara denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
