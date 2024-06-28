package com.webcontrol.android.ui.lectorqrPHC

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentLecturaQrPhcBinding
import com.webcontrol.android.util.LocalStorage
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class LectorQrPHCFragment : Fragment() {
    private lateinit var binding: FragmentLecturaQrPhcBinding
    private var value: String? = null
    private var vista: String? = null
    private var atras: Boolean = false
    var valueSelected = false
    private lateinit var tilManualId: TextInputLayout
    private lateinit var txtManualId: EditText

    private val localStorage: LocalStorage by inject()

    companion object {
        private const val argVista = "vista"
        private const val argAtras = "atras"
        private const val title = "title"

        fun newInstance(vista: String?, atras: Boolean): LectorQrPHCFragment {
            val args = Bundle()
            val fragment = LectorQrPHCFragment()
            args.putString(argVista, vista)
            args.putBoolean(argAtras, atras)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vista = this.requireArguments().getString(LectorQrPHCFragment.argVista)
        atras = this.requireArguments().getBoolean(LectorQrPHCFragment.argAtras)
        requireActivity().title = this.requireArguments().getString(LectorQrPHCFragment.title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        validation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLecturaQrPhcBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun validation() {
        if (!atras) {
            binding.scanview.setResultHandler { result ->
                value = result.text!!
                loadVista()
            }
        } else {
            binding.scanview.resumeCameraPreview { result ->
                value = result.text!!
                loadVista()
            }
        }
        loadScan()

        binding.buttonManual.setOnClickListener {
            manualInput()
        }
    }

    private fun manualInput() {
        val dialog = MaterialDialog.Builder(requireContext())
            .customView(R.layout.manual_layout, true)
            .positiveText(R.string.text_ok)
            .onPositive { _, _ ->
                if (!txtManualId.text.isNullOrEmpty()) {
                    login(txtManualId.text.toString())
                }
            }.build()

        val view: View? = dialog.customView
        tilManualId = view!!.findViewById(R.id.tilManualId)
        txtManualId = view.findViewById(R.id.txtManualId)
        tilManualId.hint = "Ingrese RUT"

        dialog.show()

    }

    private fun login(rut: String) {
        if (rut != null) {
            value = rut
            SharedUtils.setWorkerQrId(requireContext(), rut)
            findNavController().navigate(R.id.phcQRCredentialFragment)
        }
    }

    private fun loadScan() {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    binding.scanview.startCamera()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    if (response!!.isPermanentlyDenied) {
                        MaterialDialog.Builder(requireContext())
                            .title(R.string.app_name)
                            .content("Active el permiso de cámara para el correcto funcionamiento del lector QR.")
                            .positiveText(android.R.string.ok)
                            .onPositive { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:${requireActivity().packageName}")
                                )
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            .negativeText(android.R.string.cancel)
                            .onNegative { dialog: MaterialDialog, which: DialogAction? ->
                                dialog.dismiss()
                            }
                            .autoDismiss(false)
                            .cancelable(false)
                            .show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun loadVista() {
        if (isURL(value!!)) {
            val runWithDash = getRutFromURL(value!!)
            val runWithoutDash = runWithDash.replace("-", "")
            binding.scanview.stopCameraPreview()
            binding.scanview.stopCamera()
            localStorage["RUT_QR"] = runWithoutDash
            findNavController().navigate(R.id.phcQRCredentialFragment)
        } else if (value != null) {
            val cleanValue = cleanQRResult(value!!)
            binding.scanview.stopCameraPreview()
            binding.scanview.stopCamera()
            localStorage["RUT_QR"] = cleanValue
            findNavController().navigate(R.id.phcQRCredentialFragment)
        }
    }

    private fun isURL(input: String): Boolean {
        return try {
            val uri = Uri.parse(input)
            uri.scheme != null && uri.host != null
        } catch (e: Exception) {
            false
        }
    }

    private fun getRutFromURL(url: String): String {
        val startIndex = url.indexOf("RUN=") + 4
        val endIndex = url.indexOf("&")
        return url.substring(startIndex, endIndex)
    }

    private fun cleanQRResult(qrResult: String): String {
        return qrResult.replace(Regex("[^0-9]"), "")
    }
}