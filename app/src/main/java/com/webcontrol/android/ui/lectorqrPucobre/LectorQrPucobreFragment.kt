package com.webcontrol.android.ui.lectorqrPucobre

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.DialogAction
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.stepstone.apprating.AppRatingDialog
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Division
import com.webcontrol.android.databinding.FragmentLecturaQrPucobreBinding
import com.webcontrol.android.ui.lectorqr.LectorQrFragment
import com.webcontrol.android.ui.lectorqr.ResultQr
import com.webcontrol.android.util.Constants.DIVISION_CP
import com.webcontrol.android.util.Constants.WORKER_ID_QR

import com.webcontrol.core.utils.SharedUtils
import com.webcontrol.core.utils.LocalStorage
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject
import java.net.MalformedURLException
import javax.inject.Inject

@AndroidEntryPoint
class LectorQrPucobreFragment : Fragment() {
    private lateinit var binding: FragmentLecturaQrPucobreBinding
    private var value: String? = null
    private var vista: String? = null
    private var atras: Boolean = false
    var valueSelected = false
    private lateinit var tilManualId: TextInputLayout
    private lateinit var txtManualId: EditText
    private lateinit var divisionList: List<Division>


    private val localStorage: LocalStorage by inject()

    companion object {
        private const val argVista = "vista"
        private const val argAtras = "atras"
        private const val title = "title"

        fun newInstance(vista: String?, atras: Boolean): LectorQrPucobreFragment {
            val args = Bundle()
            val fragment = LectorQrPucobreFragment()
            args.putString(argVista, vista)
            args.putBoolean(argAtras, atras)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vista = this.requireArguments().getString(argVista)
        atras = this.requireArguments().getBoolean(argAtras)
        requireActivity().title = this.requireArguments().getString(title)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLecturaQrPucobreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        validation()
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
            localStorage["RUT_QR"] = value
            findNavController().navigate(R.id.pucobreQRCredentialFragment)
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
        if (isURL(value!!)){
            val runWithDash = getRutFromURL(value!!)
            val runWithoutDash = runWithDash.replace("-", "")
            binding.scanview.stopCameraPreview()
            binding.scanview.stopCamera()
            localStorage["RUT_QR"] = runWithoutDash
            findNavController().navigate(R.id.pucobreQRCredentialFragment)
        }else if(value != null) {
            val cleanValue = cleanQRResult(value!!)
            binding.scanview.stopCameraPreview()
            binding.scanview.stopCamera()
            localStorage["RUT_QR"] = cleanValue
            findNavController().navigate(R.id.pucobreQRCredentialFragment)
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
