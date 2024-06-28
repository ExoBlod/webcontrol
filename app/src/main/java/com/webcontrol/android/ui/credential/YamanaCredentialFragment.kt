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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.R
import com.webcontrol.android.data.RestInterfaceYamana
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.model.WorkerYamana
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCredentialYamanaBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response

@AndroidEntryPoint
class YamanaCredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialYamanaBinding
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true


    companion object {
        @JvmStatic
        fun newInstance(worker: WorkerAnglo): YamanaCredentialFragment {
            return YamanaCredentialFragment()
        }

        fun newInstance(aaa: String?): YamanaCredentialFragment {
            return YamanaCredentialFragment()
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
        binding = FragmentCredentialYamanaBinding.inflate(inflater, container, false)
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
        binding.includefrontq.lblEmpresa2.isSelected = true
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
        SharedUtils.showLoader(requireContext()!!, getString(R.string.loading_credential))
        val api: RestInterfaceYamana = RestClient.buildYamana()
        val call = api.getWorkerCredencial(SharedUtils.getUsuarioId(requireContext()))
        call.enqueue(object : retrofit2.Callback<ApiResponseAnglo<WorkerYamana>> {
            override fun onFailure(call: Call<ApiResponseAnglo<WorkerYamana>>, t: Throwable) {
                isValid = false
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.error_getting_credentials)
                )
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<WorkerYamana>>,
                response: Response<ApiResponseAnglo<WorkerYamana>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        var workerYamana = response.body()!!.data
                        isValid = true

                        binding.includefrontq.imgProfile.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                        binding.includefrontq.imgProfile.adjustViewBounds = true
                        binding.includefrontq.imgProfile.visibility = View.VISIBLE
                        binding.includefrontq.lblRut2.text = SharedUtils.FormatRut(workerYamana.rut)
                        binding.includefrontq.lblFuncionario.text = "${workerYamana.nombres} ${workerYamana.apellidos}"
                        binding.includefrontq.lblCargo.text = workerYamana.rol
                        binding.includefrontq.lblEmpresa2.text = workerYamana.empresa
                        binding.includefrontq.txtSubtitle2.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAcceso.visibility = View.VISIBLE
                        binding.includefrontq.lblAccesos.visibility = View.VISIBLE
                        binding.includefrontq.imgQr.setImageBitmap(generateQR(workerYamana.rut))
                        binding.includefrontq.imgQr.adjustViewBounds = true
                        binding.includefrontq.lblAccesos.text = workerYamana.autorizado
                        binding.includefrontq.btnVerAtras2.visibility = View.VISIBLE

                        if (!workerYamana.foto.isNullOrEmpty()) {
                            loadImg(workerYamana.foto)
                        }
                        if (workerYamana.mandante == "SI") {
                            binding.includeback.viewHeaderBack2.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorCredentialYamanaMand
                                )
                            )
                            binding.includefrontq.viewHeaderFront.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorCredentialYamanaMand
                                )
                            )
                        } else {
                            binding.includeback.viewHeaderBack2.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorCredentialYamanaCont
                                )
                            )
                            binding.includefrontq.viewHeaderFront.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorCredentialYamanaCont
                                )
                            )
                        }
                        if (!workerYamana.flicconducir.isNullOrEmpty())
                            binding.includeback.lblVencLic2.text = fechaFormato(workerYamana.flicconducir)
                        else
                            binding.includeback.lblVencLic2.text = "No registra"
                        if (!workerYamana.fpsicosensotecnico.isNullOrEmpty())
                            binding.includeback.lblVencPsico2.text = fechaFormato(workerYamana.fpsicosensotecnico)
                        else
                            binding.includeback.lblVencPsico2.text = "No registra"
                        if (!workerYamana.fmanejodef.isNullOrEmpty())
                            binding.includeback.lblVencMan.text = fechaFormato(workerYamana.fmanejodef)
                        else
                            binding.includeback.lblVencMan.text = "No registra"
                        if (!workerYamana.fingreso.isNullOrEmpty())
                            binding.includeback.lblIngresoMina.text = fechaFormato(workerYamana.fingreso)
                        else
                            binding.includeback.lblIngresoMina.text = "No registra"
                    } else {
                        isValid = false
                        binding.includefrontq.lblMessage2.visibility = View.VISIBLE
                        binding.includefrontq.lblMessage2.text = getString(
                            R.string.worker_id_not_have_active_credential,
                            SharedUtils.getUsuarioId(requireContext())
                        )
                    }
                } else {
                    isValid = false
                    binding.includefrontq.lblMessage2.visibility = View.VISIBLE
                    binding.includefrontq.lblMessage2.text = getString(
                        R.string.worker_id_not_have_active_credential,
                        SharedUtils.getUsuarioId(requireContext())
                    )
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

    private fun generateQR(source: String, height: Int = 512, width: Int = 512): Bitmap {
        val bitMatrix = QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height)
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
        return bitmap
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
}