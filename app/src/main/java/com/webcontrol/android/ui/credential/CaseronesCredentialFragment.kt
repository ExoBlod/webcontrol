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
import com.webcontrol.android.data.RestInterfaceCaserones
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.model.WorkerCaserones
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCredentialCaseronesBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response

@AndroidEntryPoint
class CaseronesCredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialCaseronesBinding
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true


    companion object {
        @JvmStatic
        fun newInstance(worker: WorkerAnglo): CaseronesCredentialFragment {
            return CaseronesCredentialFragment()
        }

        fun newInstance(aaa: String?): CaseronesCredentialFragment {
            return CaseronesCredentialFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Credencial"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCredentialCaseronesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontq.cardFront2.cameraDistance = 8000 * scale
        binding.includeback.cardBack2.cameraDistance = 8000 * scale
        frontAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_in
        ) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_out
        ) as AnimatorSet
        loadData()
        binding.includefrontq.lblCasCompany.isSelected = true
//        container.setOnClickListener { flipCard() }
        binding.includeback.btnVerAdelante2.setOnClickListener { flipCard() }
        binding.includefrontq.btnVerAtras2.setOnClickListener { flipCard() }
    }

    private fun flipCard() {
        if (isValid) {
            isFront = if (isFront) {
                frontAnim.setTarget(binding.includefrontq.cardFront2)
                backAnim.setTarget(binding.includeback.cardBack2)
                frontAnim.start()
                backAnim.start()
                false
            } else {
                frontAnim.setTarget(binding.includeback.cardBack2)
                backAnim.setTarget(binding.includefrontq.cardFront2)
                backAnim.start()
                frontAnim.start()
                true
            }
        }
    }


    private fun loadData() {
        SharedUtils.showLoader(requireContext(), "Cargando Credencial...")
        val api: RestInterfaceCaserones = RestClient.buildCaserones()
        val call = api.getWorkerCredencial(SharedUtils.getUsuarioId(requireContext()))
        call.enqueue(object : retrofit2.Callback<ApiResponseAnglo<WorkerCaserones>> {
            override fun onFailure(call: Call<ApiResponseAnglo<WorkerCaserones>>, t: Throwable) {
                isValid = false
                SharedUtils.showToast(requireContext(), "Error obteniendo credenciales")
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<WorkerCaserones>>,
                response: Response<ApiResponseAnglo<WorkerCaserones>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        var workerCaserones = response.body()!!.data
                        isValid = true
                        binding.includefrontq.imgProfile.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                        binding.includefrontq.imgProfile.adjustViewBounds = true
                        binding.includefrontq.imgProfile.visibility = View.VISIBLE
                        binding.includefrontq.lblRut2.text = SharedUtils.FormatRut(workerCaserones.rut)
                        binding.includefrontq.lblFuncionario.text =
                            "${workerCaserones.nombres} ${workerCaserones.apellidos}"
                        binding.includefrontq.lblCargo.text = workerCaserones.ROL
                        binding.includefrontq. lblCasCompany.text = workerCaserones.empresa
                        binding.includefrontq. txtSubtitle2.visibility = View.VISIBLE
                        binding.includefrontq.lblCargo.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAcceso.visibility = View.VISIBLE
                        binding.includefrontq.lblAccesos.visibility = View.VISIBLE
                        binding.includefrontq.imgQr.setImageBitmap(
                            generateQR(
                                SharedUtils.FormatRutCharacters(
                                    workerCaserones.rut
                                )
                            )
                        )
                        binding.includefrontq.imgQr.adjustViewBounds = true

                        if (!workerCaserones.FOTO.isNullOrEmpty())
                            loadImg(workerCaserones.FOTO)
                        if (!workerCaserones.AUTORIZADO.isNullOrEmpty())
                            binding.includefrontq.lblAccesos.text = workerCaserones.AUTORIZADO
                        else
                            binding.includefrontq.lblAccesos.text = "NO"
                        if (!workerCaserones.FLICCONDUCIR.isNullOrEmpty())
                            binding.includeback.lblVencLic2.text = fechaFormato(workerCaserones.FLICCONDUCIR)
                        else
                            binding.includeback.lblVencLic2.text = "No registra"
                        if (!workerCaserones.FPSICOSENSOTECNICO.isNullOrEmpty())
                            binding.includeback.lblVencPsico2.text = fechaFormato(workerCaserones.FPSICOSENSOTECNICO)
                        else
                            binding.includeback.lblVencPsico2.text = "No registra"
                        if (!workerCaserones.FINGRESO.isNullOrEmpty())
                            binding.includeback. lblVencMan2.text = fechaFormato(workerCaserones.FINGRESO)
                        else
                            binding.includeback. lblVencMan2.text = "No registra"
                        if (!workerCaserones.MUNICIPAL.isNullOrEmpty())
                            binding.includeback.lblLicMuni.text = workerCaserones.MUNICIPAL
                        else
                            binding.includeback.lblLicMuni.text = "No registra"
                        if (!workerCaserones.ZONA.isNullOrEmpty())
                            binding.includeback.lblZonaCond.text = workerCaserones.ZONA
                        else
                            binding.includeback.lblZonaCond.text = "No registra"
                        if (!workerCaserones.TIPOVEHICULO.isNullOrEmpty())
                            binding.includeback.lblTipoVehiculo.text = workerCaserones.TIPOVEHICULO
                        else
                            binding.includeback.lblTipoVehiculo.text = "No registra"

                    } else {
                        isValid = false
                        binding.includefrontq.imgProfile.visibility = View.INVISIBLE
                        binding.includefrontq.lblMessage2.visibility = View.VISIBLE
                        binding.includefrontq.lblMessage2.text =
                            "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
                    }
                } else {
                    isValid = false
                    binding.includefrontq.imgProfile.visibility = View.INVISIBLE
                    binding.includefrontq.lblMessage2.visibility = View.VISIBLE
                    binding.includefrontq.lblMessage2.text =
                        "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
                }
                SharedUtils.dismissLoader(requireContext())
            }

        })
    }

    private fun fechaFormato(fecha: String): String {
        return "${
            fecha.substring(6, 8) + "-" + fecha.substring(4, 6) + "-" + fecha.substring(
                0,
                4
            )
        }"
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