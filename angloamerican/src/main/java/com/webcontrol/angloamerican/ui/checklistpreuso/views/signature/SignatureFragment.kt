package com.webcontrol.angloamerican.ui.checklistpreuso.views.signature

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.FragmentPreUsoRecordSignatureBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoActivity
import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListScope
import com.webcontrol.angloamerican.ui.checklistpreuso.data.ScopesChecklist
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.AppDataBase
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.entity.WorkerSignature
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SignatureRequest
import com.webcontrol.angloamerican.utils.BaseFragment
import com.webcontrol.angloamerican.utils.Utils.bitmapToBase64
import com.webcontrol.core.utils.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pre_uso_record_signature.signature_pad
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class SignatureFragment : BaseFragment<FragmentPreUsoRecordSignatureBinding, SignatureViewModel>() {

    private var signature = ""
    private val database: AppDataBase by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Registrar Firma"
    }

    override fun getViewModelClass() = SignatureViewModel::class.java
    override fun getViewBinding() = FragmentPreUsoRecordSignatureBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signaturePadFunction()

        binding.btnRecord.setOnClickListener {
            if (signature.isNotEmpty()){
                confirmSignatureRecord(requireContext())
            }
            else
                Toast.makeText(
                    requireContext(),
                    "Tiene que registrar su firma",
                    Toast.LENGTH_SHORT
                ).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signatureWorker.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    SignatureUIEvent.ShowLoading -> {
                        (requireActivity() as CheckListPreUsoActivity).showLoading(true)
                        SharedUtils.showLoader(requireContext(), "Enviando Firma")
                    }
                    SignatureUIEvent.HideLoading -> {
                        (requireActivity() as CheckListPreUsoActivity).showLoading(false)
                        SharedUtils.dismissLoader(requireContext())
                    }
                    SignatureUIEvent.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "No se pudo guardar la firma, inténtelo más tarde",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is SignatureUIEvent.Success -> {
                        if(event.signatureResponse.isNotEmpty()){
                            val signatureResponse = event.signatureResponse[0].result
                            if (signatureResponse.equals("FIRMA GUARDADA CON EXITO")) {
                                database.checkListPreUso().insertSignature(
                                    WorkerSignature(
                                        SharedUtils.getUsuarioId(requireContext()),
                                        signature
                                    )
                                )
                                showSuccessNotification(requireContext())
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "No se pudo guardar la firma, inténtelo más tarde",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
        NewCheckListScope.scope = ScopesChecklist.SIGNATURE
    }

    private fun signaturePadFunction() = with(binding) {
        btnSave.setOnClickListener {
            val bitmap: Bitmap? = signaturePad.signatureBitmap
            ivSignature.setImageBitmap(bitmap)
            signature = bitmapToBase64(bitmap) ?: ""
        }
        btnClear.setOnClickListener {
            signaturePad.clear()
            ivSignature.setImageDrawable(null)
        }
    }

    private fun confirmSignatureRecord(context : Context){
        if(signature_pad.isEmpty) {
            Toast.makeText(context, R.string.empty_signature_message, Toast.LENGTH_SHORT).show()
            return
        }

        val dialogBinding = layoutInflater.inflate(R.layout.popup_confirm_signature_registration, null)
        val confirmDialog = Dialog(context)
        val yesBtn = dialogBinding.findViewById<Button>(R.id.btnOK)
        val cancelBtn = dialogBinding.findViewById<Button>(R.id.btnCancel)
        with(confirmDialog) {
            setContentView(dialogBinding)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            yesBtn.setOnClickListener {
                dismiss()
                if(SharedUtils.isOnline(requireContext())){
                    viewModel.setSignatureWorker(
                        SignatureRequest(
                            SharedUtils.getUsuarioId(requireContext()),
                            signature
                        )
                    )
                } else {
                    Toast.makeText(context, R.string.signature_not_saved_message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signaturePreUsoFragment_to_inputDataPreUsoFragment)
                }
            }
            cancelBtn.setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    private fun showSuccessNotification(context : Context){
        val dialogBinding = layoutInflater.inflate(R.layout.popup_signature_registration_success, null)
        val successDialog = Dialog(context)
        val yesBtn = dialogBinding.findViewById<Button>(R.id.btnOK)
        with(successDialog) {
            setContentView(dialogBinding)
            setCancelable(true)
            setOnCancelListener {
                dismiss()
                this@SignatureFragment.findNavController().navigate(R.id.action_signaturePreUsoFragment_to_inputDataPreUsoFragment)
            }
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            yesBtn.setOnClickListener {
                dismiss()
                this@SignatureFragment.findNavController().navigate(R.id.action_signaturePreUsoFragment_to_inputDataPreUsoFragment)
            }
        }
    }
}