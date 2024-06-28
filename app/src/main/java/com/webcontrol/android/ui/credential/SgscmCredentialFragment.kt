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
import com.webcontrol.android.data.RestInterfaceSgscm
import com.webcontrol.android.data.model.sgscm.CredencialSgscmResponse
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCredentialSgscmBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response

@AndroidEntryPoint
class SgscmCredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialSgscmBinding
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCredentialSgscmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontq.cardFront.cameraDistance = 8000 * scale
        binding.includeback.cardBack.cameraDistance = 8000 * scale
        frontAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_in
        ) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_out
        ) as AnimatorSet
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
        SharedUtils.showLoader(requireContext(), getString(R.string.loading_credential))
        val api: RestInterfaceSgscm = RestClient.buildSgscm()
        val call = api.getWorkerCredencialSgscm(SharedUtils.getUsuarioId(requireContext()))
        call.enqueue(object : retrofit2.Callback<ApiResponseAnglo<CredencialSgscmResponse?>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<CredencialSgscmResponse?>>,
                t: Throwable
            ) {
                isValid = false
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.error_getting_credentials)
                )
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<CredencialSgscmResponse?>>,
                response: Response<ApiResponseAnglo<CredencialSgscmResponse?>>
            ) {
                SharedUtils.dismissLoader(requireContext())
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        val workerKs = response.body()!!.data
                        if (workerKs == null) {
                            showCredentialErrorMessage()
                            return
                        }
                        isValid = true
                        binding.includefrontq.imgProfile.visibility = View.VISIBLE
                        binding.includefrontq.txtSubtitle.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAutorizado.visibility = View.VISIBLE
                        if (!workerKs.foto.isNullOrEmpty())
                            loadImg(workerKs.foto ?: "")
                        else
                            binding.includefrontq.imgProfile.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                        binding.includefrontq.imgQr.setImageBitmap(generateQR(SharedUtils.getUsuarioId(requireContext())))
                        binding.includefrontq.imgQr.adjustViewBounds = true
                        binding.includefrontq.lblEmpresa.text = workerKs.companyName
                        if (!workerKs.mandante.equals("SI")) {
                            binding.includefrontq.viewHeaderFront.background =
                                resources.getDrawable(R.color.colorExternalCredentialKS)
                            binding.includeback.viewHeaderBack.background =
                                resources.getDrawable(R.color.colorExternalCredentialKS)
                        }
                        binding.includefrontq.lblRut.text = SharedUtils.FormatRut(workerKs.workerId ?: "")
                        binding.includefrontq.lblFuncionario.text = "${workerKs.nameWorker} ${workerKs.lastNameWorker}"
                        binding.includefrontq.lblCargo.text = workerKs.rol
                        binding.includefrontq.lblAutorizado.text = workerKs.autorizacion

                        binding.includeback.lblLicMunicipal.text =
                            if (workerKs.clase.isNullOrEmpty()) "NA" else workerKs.clase
                        binding.includeback.lblZonaConduccion.text = workerKs.zonaConduce
                        binding.includeback.lblTiposVehiculos.text = workerKs.equiposVehiculo
                    } else {
                        showCredentialErrorMessage()
                    }
                } else {
                    showCredentialErrorMessage()
                }
            }
        })
    }

    private fun showCredentialErrorMessage() {
        isValid = false
        binding.includefrontq.lblMessage.visibility = View.VISIBLE
        binding.includefrontq.lblMessage.text = getString(
            R.string.worker_id_not_have_active_credential,
            SharedUtils.getUsuarioId(requireContext())
        )
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
