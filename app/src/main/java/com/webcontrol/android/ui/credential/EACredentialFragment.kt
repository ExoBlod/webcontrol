package com.webcontrol.android.ui.credential

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.webcontrol.android.R
import com.webcontrol.android.data.RestInterfaceEA
import com.webcontrol.android.data.model.WorkerEA
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCredentialEaBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response

@AndroidEntryPoint
class EACredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialEaBinding
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCredentialEaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontq.cardFrontEA.cameraDistance = 8000 * scale
        binding.includeback.cardBackEA.cameraDistance = 8000 * scale
        frontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_in) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_out) as AnimatorSet
        loadData()
        binding.includeback.btnVerAdelanteEA.setOnClickListener { flipCard() }
        binding.includefrontq.btnVerAtrasEA.setOnClickListener { flipCard() }
    }

    private fun flipCard() {
        if (isValid) {
            isFront = if (isFront) {
                frontAnim.setTarget(binding.includefrontq.cardFrontEA)
                backAnim.setTarget(binding.includeback.cardBackEA)
                frontAnim.start()
                backAnim.start()
                false
            } else {
                frontAnim.setTarget(binding.includeback.cardBackEA)
                backAnim.setTarget(binding.includefrontq.cardFrontEA)
                backAnim.start()
                frontAnim.start()
                true
            }
        }
    }

    private fun loadData() {
        SharedUtils.showLoader(requireContext()!!, "Cargando Credencial...")
        val api: RestInterfaceEA = RestClient.buildEA()
        val call = api.getWorkerCredencial(SharedUtils.getUsuarioId(requireContext()))
        call.enqueue(object : retrofit2.Callback<ApiResponseAnglo<WorkerEA>> {
            override fun onFailure(call: Call<ApiResponseAnglo<WorkerEA>>, t: Throwable) {
                isValid = false
                SharedUtils.showToast(requireContext(), "Error obteniendo credenciales")
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(call: Call<ApiResponseAnglo<WorkerEA>>, response: Response<ApiResponseAnglo<WorkerEA>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        var workerEA = response.body()!!.data
                        isValid = true
                        binding.includefrontq.imgCodeBarEA?.let {
                            it.visibility = View.VISIBLE
                        }
                        binding.includefrontq.txtSubtitleEA.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAutorizadoEA.visibility = View.VISIBLE
                        if (!workerEA.foto.isNullOrEmpty())
                            loadImg(workerEA.foto)
                        else
                            binding.includefrontq.imgProfileEA.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)

                        val rut = formatRutEA(SharedUtils.getUsuarioId(requireContext()))
                        binding.includefrontq.imgCodeBarEA.setImageBitmap(generateBarcode(rut))
                        binding.includefrontq.imgCodeBarEA.adjustViewBounds = true
                        binding.includefrontq.lblRutEA.text = SharedUtils.FormatRut(workerEA.rut)
                        binding.includefrontq.lblFuncionarioEA.text = "${workerEA.nombres} ${workerEA.apellidos}"
                        binding.includefrontq.lblCargoEA.text = workerEA.rol
                        binding.includefrontq.lblEmpresaEA.text = workerEA.empresa
                        binding.includefrontq.lblAutorizadoEA.text = workerEA.autorizado

                        binding.includeback.lblLicMunicipal.text = if (workerEA.clase.isNullOrEmpty()) "NA" else workerEA.clase
                        binding.includeback.lblVencPsico.text = SharedUtils.getNiceDate(workerEA.fecExpPsico)
                        binding.includeback.lblZonaConduccion.text = workerEA.zonasConduce
                        binding.includeback.lblTiposVehiculos.text = workerEA.tipoVehiculos
                        binding.includeback.lblCargoElectrico.text = if(workerEA.cargoElectrico.isNullOrEmpty()) "NO" else workerEA.cargoElectrico
                        binding.includeback.lblDocCargoElectrico.text = if(workerEA.documentoCargoElectrico.isNullOrEmpty()) "NO" else workerEA.documentoCargoElectrico
                    } else {
                        isValid = false
                        binding.includefrontq.lblMessageEA.visibility = View.VISIBLE
                        binding.includefrontq.lblMessageEA.text = "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
                    }
                } else {
                    isValid = false
                    binding.includefrontq.lblMessageEA.visibility = View.VISIBLE
                    binding.includefrontq.lblMessageEA.text = "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
                }
                SharedUtils.dismissLoader(requireContext())
            }
        })
    }

    private fun loadImg(foto: String) {
        var image: ByteArray? = null
        var bitmap: Bitmap? = null
        try {
            image = Base64.decode(foto, 0)
            val options = BitmapFactory.Options()
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.size, options)
            GlideApp.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
                    .error(R.drawable.ic_account_circle_materialgrey_240dp)
                    .circleCrop()
                    .fitCenter()
                    .into(binding.includefrontq.imgProfileEA!!)
        } catch (e: Exception) {
            SharedUtils.showToast(requireContext(), e.message)
        }
    }

    private fun generateBarcode(source: String, height: Int = 400, width: Int = 520): Bitmap {
        val bitMatrix = Code128Writer().encode(source, BarcodeFormat.CODE_128, width, height)
        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height)
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
        return bitmap
    }

    private fun formatRutEA(rut: String): String{
        val newRut = StringBuilder()
        newRut.append(rut.substring(0, rut.length - 1)).append("-").append(rut.substring(rut.length - 1, rut.length))

        return newRut.toString()
    }
}