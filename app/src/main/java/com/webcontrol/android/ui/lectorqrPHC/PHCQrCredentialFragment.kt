package com.webcontrol.android.ui.lectorqrPHC

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.databinding.FragmentCredentialQrPhcBinding
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.pucobre.R
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import javax.inject.Inject

@AndroidEntryPoint
class PHCQrCredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialQrPhcBinding
    private val credentialViewModel by sharedViewModel<CredentialPHCQrViewModel>()
    private var workerid = ""
    private var isValid = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Credencial Personal"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCredentialQrPhcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun loadData() {
        val workerId: String = SharedUtils.getWorkerQrId(requireContext())
        credentialViewModel.getCredential(workerId)
        credentialViewModel.credential().observe(viewLifecycleOwner) { result ->
            if (result.isLoading) {
                Toast.makeText(context, "Cargando...", Toast.LENGTH_SHORT).show()
            } else {
                if (result.error == null && result.data != null) {
                    isValid = true
                    val worker = result.data!!.data
                    if (worker == null) {
                        isValid = false
                        binding.viewHeaderFrontQR.setBackgroundColor(resources.getColor(R.color.colorCredentialKS))
                        binding.showSuccessAccredited.visibility = View.GONE
                        binding.showNoAcredited.visibility = View.VISIBLE
                        binding.lblMessageFail.text =
                            "El trabajador no cuenta con credencial activa"
                        return@observe
                    }
                    binding.imgProfileQR.visibility = View.VISIBLE
                    binding.txtSubtitleQR.visibility = View.VISIBLE
                    binding.lblTitleAutorizadoQR.visibility = View.VISIBLE
                    binding.lblTitleConductorQR.visibility = View.VISIBLE
                    if (worker.foto != null && worker.foto!!.isNotEmpty())
                        loadImg(worker.foto)
                    else
                        binding.imgProfileQR.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                    binding.imgQr2.setImageBitmap(generateQR(worker.workerId))
                    binding.imgQr2.adjustViewBounds = true
                    binding.lblEmpresaQR.text = worker.workerEmpresa
                    binding.lblRutQR.text =
                        formatRut(worker.workerId)
                    binding.lblFuncionarioQR.text =
                        "${worker.workerNombres} ${worker.workerApellidos}"
                    binding.lblCargoQR.text = worker.rol
                    binding.lblAutorizadoQR.text = worker.workerAutorizado
                    binding.lblConductorQR.text =
                        if (worker.workerFlicconducir.isNullOrEmpty()) "-" else worker.workerFlicconducir
                } else {
                    isValid = false
                    Toast.makeText(context, "Error obteniendo credencial", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun formatRut(rut: String): String {
        var rut = rut
        return if (rut.length <= 7) rut else {
            var cont = 0
            var format: String
            rut = rut.replace(".", "")
            rut = rut.replace("-", "")
            format = "-" + rut.substring(rut.length - 1)
            for (i in rut.length - 2 downTo 0) {
                format = rut.substring(i, i + 1) + format
                cont++
                if (cont == 3 && i != 0) {
                    format = ".$format"
                    cont = 0
                }
            }
            format
        }
    }

    private fun loadImg(foto: String) {
        val image: ByteArray?
        val bitmap: Bitmap?
        try {
            image = Base64.decode(foto, 0)
            val options = BitmapFactory.Options()
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.size, options)
            Glide.with(this)
                .load(bitmap)
                .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
                .error(R.drawable.ic_account_circle_materialgrey_240dp)
                .circleCrop()
                .fitCenter()
                .into(binding.imgProfileQR!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateQR(source: String, height: Int = 512, width: Int = 512): Bitmap {
        val bitMatrix = QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height)
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
        return bitmap
    }

}