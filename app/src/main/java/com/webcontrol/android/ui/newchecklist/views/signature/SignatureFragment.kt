package com.webcontrol.android.ui.newchecklist.views.signature

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentRecordSignatureBinding
import com.webcontrol.android.ui.newchecklist.data.NewCheckListScope
import com.webcontrol.android.ui.newchecklist.data.ScopesChecklist
import com.webcontrol.android.ui.newchecklist.data.WorkerSignature
import com.webcontrol.android.util.BaseFragment
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.bitmapToBase64
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignatureFragment : BaseFragment<FragmentRecordSignatureBinding, SignatureViewModel>() {

    private var signature = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Registrar Firma"
    }

    override fun getViewModelClass() = SignatureViewModel::class.java
    override fun getViewBinding() = FragmentRecordSignatureBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signaturePadFunction()

        binding.btnRecord.setOnClickListener {
            if (signature.isNotEmpty()){
                App.db.checkListBambaDao().insertSignature(
                    com.webcontrol.android.data.db.entity.WorkerSignatures(
                        SharedUtils.getUsuarioId(requireContext()),
                        signature
                    )
                )
                if(SharedUtils.isOnline(requireContext())){
                    viewModel.setSignatureWorker(
                        WorkerSignature(
                            SharedUtils.getUsuarioId(requireContext()),
                            signature
                        )
                    )
                }
                else{
                    findNavController().navigate(R.id.action_signatureFragment_to_inputDataFragment)
                }
            }

            else
                Toast.makeText(
                    requireContext(),
                    "No se pudo guardar la firma",
                    Toast.LENGTH_SHORT
                ).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signatureWorker.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    SignatureUIEvent.ShowLoading -> {
                        SharedUtils.showLoader(requireContext(), "Enviando Firma")
                    }
                    SignatureUIEvent.HideLoading -> {
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
                        if (event.workerSignature) {
                            //TODO Guardar Firma en Room
                            findNavController().navigate(R.id.action_signatureFragment_to_inputDataFragment)
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
        NewCheckListScope.scope = ScopesChecklist.SIGNATURE
    }

    private fun signaturePadFunction() = with(binding) {
        btnSave.setOnClickListener {
            val bitmap: Bitmap? = signaturePad.signatureBitmap
            ivSignature.setImageBitmap(bitmap)
            signature = bitmapToBase64(bitmap)?:""
        }
        btnClear.setOnClickListener {
            signaturePad.clear()
            ivSignature.setImageDrawable(null)
        }
    }
}