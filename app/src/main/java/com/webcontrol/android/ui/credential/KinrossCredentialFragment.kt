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
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.R
import com.webcontrol.android.data.RestInterfaceKinross
import com.webcontrol.android.data.model.WorkerKinrossCredencial
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCredentialKinrossBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response

@AndroidEntryPoint
class KinrossCredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialKinrossBinding
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCredentialKinrossBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontq.cardFront.cameraDistance = 8000 * scale
        binding.includeback.cardBack.cameraDistance = 8000 * scale
        frontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_in) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_out) as AnimatorSet
        loadData()
        binding.includeback.btnVerAdelante.setOnClickListener { flipCard() }
        binding.includefrontq.btnVerAtras.setOnClickListener { flipCard() }
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
        val api: RestInterfaceKinross = RestClient.buildKinross()
        val call = api.getWorkerCredencial(SharedUtils.getUsuarioId(requireContext()))
        call.enqueue(object : retrofit2.Callback<ApiResponseAnglo<WorkerKinrossCredencial>> {
            override fun onFailure(call: Call<ApiResponseAnglo<WorkerKinrossCredencial>>, t: Throwable) {
                isValid = false
                SharedUtils.showToast(requireContext(), "Error obteniendo credenciales")
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(call: Call<ApiResponseAnglo<WorkerKinrossCredencial>>,
                                    response: Response<ApiResponseAnglo<WorkerKinrossCredencial>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        isValid = true
                        var workerKinross = response.body()!!.data
                        binding.includefrontq.imgProfileKinross.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAcceso.visibility = View.VISIBLE
                        binding.includefrontq.txtSubtitle.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAutorizado.visibility = View.VISIBLE
                        if (!workerKinross.foto.isNullOrEmpty())
                            loadImg(workerKinross.foto)
                        else
                            binding.includefrontq.imgProfileKinross.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                        binding.includefrontq.imgQr.setImageBitmap(generateQR(SharedUtils.getUsuarioId(requireContext())))
                        binding.includefrontq.imgQr.adjustViewBounds = true
                        binding.includefrontq.lblEmpresa.text = workerKinross.empresa
                        if (!workerKinross.mandante.equals("SI")) {
                            binding.includefrontq.viewHeaderFront.background = resources.getDrawable(R.color.colorExternalCredentialKS)
                            binding.includeback.viewHeaderBack.background = resources.getDrawable(R.color.colorExternalCredentialKS)
                        }
                        binding.includefrontq.lblRut.text = SharedUtils.FormatRut(workerKinross.workerId)
                        binding.includefrontq.lblFuncionario.text = "${workerKinross.nombres} ${workerKinross.apellidos}"
                        binding.includefrontq.lblCargo.text = workerKinross.rol
                        binding.includefrontq.lblAccesos.text = if (workerKinross.zonasAcceso.isNullOrEmpty()) "-" else workerKinross.zonasAcceso
                        binding.includefrontq.lblAutorizado.text = workerKinross.autorizacion
                        binding.includeback.lblVencLic.text = SharedUtils.getNiceDate(workerKinross.fechalic)
                        binding.includeback.lblLicMunicipal.text = if (workerKinross.clase.isNullOrEmpty()) "NA" else workerKinross.clase
                        binding.includeback.lblVencPsico.text = SharedUtils.getNiceDate(workerKinross.fechasico)
                        binding.includeback.lblFecIngresoComp.text = SharedUtils.getNiceDate(workerKinross.fechain)
                        binding.includeback.lblZonaConduccion.text = workerKinross.zonasConduce
                        binding.includeback.lblTiposVehiculos.text = workerKinross.tipoVehiculos
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
                    .into(binding.includefrontq.imgProfileKinross)
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