package com.webcontrol.android.ui.credential

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.R
import com.webcontrol.android.data.model.WorkerCredentialBarrick
import com.webcontrol.android.data.network.ApiResponseBarrick
import com.webcontrol.android.databinding.FragmentCredentialBarrickArBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import retrofit2.Call
import retrofit2.Response


class BarrickCredentialFragmentAR : Fragment() {
    private lateinit var binding: FragmentCredentialBarrickArBinding
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true
    private var selectedCountry: String? = null

    companion object {

        const val title = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title=this.requireArguments().getString(title)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCredentialBarrickArBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontqAR.cardFront.cameraDistance = 8000 * scale
        binding.includebackAR.cardBack.cameraDistance = 8000 * scale
        frontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_in) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_out) as AnimatorSet
        loadData()
        binding.includebackAR.btnVerAdelante.setOnClickListener { flipCard() }
        binding.includefrontqAR.btnVerAtras.setOnClickListener { flipCard() }
    }
    private fun flipCard() {
        if (isValid) {
            isFront = if (isFront) {
                frontAnim.setTarget(binding.includefrontqAR.cardFront)
                backAnim.setTarget(binding.includebackAR.cardBack)
                frontAnim.start()
                backAnim.start()
                false
            } else {
                frontAnim.setTarget(binding.includebackAR.cardBack)
                backAnim.setTarget(binding.includefrontqAR.cardFront)
                backAnim.start()
                frontAnim.start()
                true
            }
        }
    }

    private fun loadData() {
        SharedUtils.showLoader(requireContext(), "Cargando Credencial...")
        val api = RestClient.buildBarrick()
        Log.i("CARGANDO CREDENCIAL", api.toString())
        val call = api.getWorkerCredencial(SharedUtils.getUsuarioId(requireContext()), "AR")
        call.enqueue(object : retrofit2.Callback<ApiResponseBarrick<WorkerCredentialBarrick>> {
            override fun onFailure(call: Call<ApiResponseBarrick<WorkerCredentialBarrick>>, t: Throwable) {
                isValid = false
                SharedUtils.showToast(requireContext(), "Error obteniendo credenciales")
                Log.e("Error: ", t.toString())
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(call: Call<ApiResponseBarrick<WorkerCredentialBarrick>>,
                                    response: Response<ApiResponseBarrick<WorkerCredentialBarrick>>
            ) {
                Log.i("CREDENCIAL OBTENIDA", api.toString())
                if (response.isSuccessful && response.body()!!.data.isNotEmpty()) {
                    if (response.body()!!.isSuccess ) {
                        isValid = true
                        val worker = response.body()!!.data[0]
                        binding.includefrontqAR.viewHeaderFront.visibility = View.GONE
                        binding.includefrontqAR.imgProfileKinross.visibility = View.VISIBLE
                        binding.includefrontqAR.lblEmpresa.visibility = View.VISIBLE
                        if (!worker?.foto.isNullOrEmpty())
                            loadImg(worker!!.foto)
                        else
                            binding.includefrontqAR.imgProfileKinross.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                        binding.includefrontqAR.imgQr.setImageBitmap(generateQR(SharedUtils.getUsuarioId(requireContext())))
                        binding.includefrontqAR.imgQr.adjustViewBounds = true
                        if (!worker?.mandante.equals("SI")) {
                            binding.includefrontqAR.viewHeaderFront.background = ContextCompat.getDrawable(requireContext(), R.color.colorAccent)
                        }
                        binding.includefrontqAR.lblrut.text = SharedUtils.FormatRut(worker?.workerId.toString())
                        binding.includefrontqAR.lblFuncionario.text = "${worker?.nombres} ${worker?.apellidos}"
                        binding.includefrontqAR.lblEmpresa.text = worker.empresa
                    } else {
                        isValid = false
                        binding.includefrontqAR.imgPais.visibility = View.GONE
                        binding.includefrontqAR.imgMarca.visibility = View.GONE
                        binding.includefrontqAR.lblEmpresa.visibility = View.GONE
                        binding.includefrontqAR.textView16.visibility = View.VISIBLE
                        binding.includefrontqAR.lblMessage.visibility = View.VISIBLE
                        binding.includefrontqAR.lblMessage.text = "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"

                    }
                } else {
                    isValid = false
                    binding.includefrontqAR.imgPais.visibility = View.GONE
                    binding.includefrontqAR.imgMarca.visibility = View.GONE
                    binding.includefrontqAR.lblEmpresa.visibility = View.GONE
                    binding.includefrontqAR.textView16.visibility = View.VISIBLE
                    binding.includefrontqAR.lblMessage.visibility = View.VISIBLE
                    binding.includefrontqAR.lblMessage.text = "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
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
                .into(binding.includefrontqAR.imgProfileKinross!!)
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