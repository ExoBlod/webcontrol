package com.webcontrol.android.ui.credential

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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.R
import com.webcontrol.android.data.model.WorkerCredentialGoldfields
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCredentialGoldfieldsBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response

@AndroidEntryPoint
class GoldfieldsCredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialGoldfieldsBinding
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true

    companion object {
        const val title = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title=requireArguments().getString(title)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCredentialGoldfieldsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontq.cardFront.cameraDistance = 8000 * scale
        binding.includeback.cardBack.cameraDistance = 8000 * scale
        frontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_in) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_out) as AnimatorSet
        binding.includeback.btnVerAdelante.setOnClickListener { flipCard() }
        binding.includefrontq.btnVerAtras.setOnClickListener { flipCard() }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun flipCard() {
        if (isValid) {
            isFront = if (isFront) {
                frontAnim.setTarget(binding.includefrontq.cardFront)
                backAnim.setTarget(binding.includeback.cardBack)
                frontAnim.start()
                backAnim.start()
                false
            } else {
                frontAnim.setTarget(binding.includeback.cardBack)
                backAnim.setTarget(binding.includefrontq.cardFront)
                backAnim.start()
                frontAnim.start()
                true
            }
        }
    }

    private fun loadData() {
        SharedUtils.showLoader(requireContext(), "Cargando Credencial...")
        val api = RestClient.buildGf()
        val call = api.getWorkerCredencial(SharedUtils.getUsuarioId(requireContext()))
        call.enqueue(object : retrofit2.Callback<ApiResponseAnglo<WorkerCredentialGoldfields>> {
            override fun onFailure(call: Call<ApiResponseAnglo<WorkerCredentialGoldfields>>, t: Throwable) {
                isValid = false
                SharedUtils.showToast(requireContext(), "Error obteniendo credenciales")
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(call: Call<ApiResponseAnglo<WorkerCredentialGoldfields>>,
                                    response: Response<ApiResponseAnglo<WorkerCredentialGoldfields>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        isValid = true
                        val worker = response.body()!!.data
                        binding.includefrontq.imgProfile.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAcceso.visibility = View.GONE
                        binding.includefrontq.lblAccesos.visibility = View.GONE
                        binding.includefrontq.txtSubtitle.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAutorizado.visibility = View.VISIBLE
                        if (!worker.foto.isNullOrEmpty())
                            loadImg(worker.foto)
                        else
                            binding.includefrontq.imgProfile.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                        binding.includefrontq.imgQr.setImageBitmap(generateQR(SharedUtils.getUsuarioId(requireContext())))
                        binding.includefrontq.imgQr.adjustViewBounds = true
                        binding.includefrontq.viewHeaderFront.background = ResourcesCompat.getDrawable(resources, R.color.colorCredentialGoldfieldsMand, null)
                        binding.includeback.viewHeaderBack.background = ResourcesCompat.getDrawable(resources, R.color.colorCredentialGoldfieldsMand, null)

                        binding.includefrontq.lblEmpresa.text = worker.empresa
                        binding.includefrontq.lblRut.text = SharedUtils.FormatRut(worker.workerId)
                        binding.includefrontq.lblFuncionario.text = "${worker.nombres} ${worker.apellidos}"
                        binding.includefrontq.lblCargo.text = worker.rol
                        binding.includefrontq.lblAccesos.text = if (worker.zonasAcceso.isNullOrEmpty()) "-" else worker.zonasAcceso
                        binding.includefrontq.lblAutorizado.text = worker.autorizacion
                        binding.includeback.lblVencLic.text = if (worker.fechalic.isNullOrEmpty()) getString(R.string.credential_default_data) else SharedUtils.getNiceDate(worker.fechalic)
                        binding.includeback.lblLicMunicipal.text = if (worker.licMunicipal.isNullOrEmpty()) getString(R.string.credential_default_data) else worker.licMunicipal
                        binding.includeback.lblVencPsico.text = if (worker.fechasico.isNullOrEmpty()) getString(R.string.credential_default_data) else SharedUtils.getNiceDate(worker.fechasico)
                        binding.includeback.lblFecIngresoComp.text = SharedUtils.getNiceDate(worker.fechain)
                        binding.includeback.lblZonaConduccion.text = worker.zonasConduce
                        binding.includeback.lblTiposVehiculos.text = if (worker.tipoVehiculos.isNullOrEmpty()) getString(R.string.credential_default_data) else worker.tipoVehiculos
                    } else {
                        isValid = false
                        binding.includefrontq.lblMessage.visibility = View.VISIBLE
                        binding.includefrontq.lblMessage.text = "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
                    }
                } else {
                    isValid = false
                    binding.includefrontq.lblMessage.visibility = View.VISIBLE
                    binding.includefrontq.lblMessage.text = "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
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
                    .into(binding.includefrontq.imgProfile)
        } catch (e: Exception) {
            SharedUtils.showToast(requireContext(), e.message)
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