package com.webcontrol.android.ui.lectorqr

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.webcontrol.android.R
import com.webcontrol.android.ui.checklist.HistoricoCheckListFragment
import com.webcontrol.android.databinding.FragmentLecturaQrBinding
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zxing.ZXingScannerView

@AndroidEntryPoint
class LectorQrFragment : Fragment() {
    private lateinit var binding: FragmentLecturaQrBinding
    private var value: String? = null
    private var vista: String? = null
    private var atras: Boolean = false

    companion object {
        private const val argVista = "vista"
        private const val argAtras = "atras"
        private const val title="title"

        fun newInstance(vista: String?, atras: Boolean): LectorQrFragment {
            val args = Bundle()
            val fragment = LectorQrFragment()
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
        requireActivity().title=this.requireArguments().getString(title)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLecturaQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!atras) {
            binding.scanview.setResultHandler(ZXingScannerView.ResultHandler { result ->
                value = result.text!!
                loadVista()
            })
        } else {
            binding.scanview.resumeCameraPreview(ZXingScannerView.ResultHandler { result ->
                value = result.text!!
                loadVista()
            })
        }
        loadScan()
    }

    private fun loadScan() {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    binding.scanview.startCamera()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    if (response!!.isPermanentlyDenied){
                        MaterialDialog.Builder(requireContext())
                            .title(R.string.app_name)
                            .content("Active el permiso de cámara para el correcto funcionamiento del lector QR.")
                            .positiveText(android.R.string.ok)
                            .onPositive { dialog, which ->
                                dialog.dismiss()
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:${requireActivity().packageName}"))
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
        binding.scanview.stopCameraPreview()
        binding.scanview.stopCamera()
        val resultQr = ResultQr.Builder()
                .fragment(value, vista)
                .build()
        val transaction : FragmentTransaction? = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.nav_host_fragment_content_main, resultQr.fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}