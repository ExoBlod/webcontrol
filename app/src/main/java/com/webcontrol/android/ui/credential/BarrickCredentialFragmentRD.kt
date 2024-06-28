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
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.R
import com.webcontrol.android.data.model.WorkerCredentialBarrick
import com.webcontrol.android.data.network.ApiResponsBarrickCredential
import com.webcontrol.android.data.network.ApiResponseBarrick
import com.webcontrol.android.databinding.FragmentCredentialBarrickArBinding
import com.webcontrol.android.databinding.FragmentCredentialBarrickRdBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import retrofit2.Call
import retrofit2.Response

class BarrickCredentialFragmentRD : Fragment() {
    private lateinit var binding: FragmentCredentialBarrickRdBinding
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
        binding = FragmentCredentialBarrickRdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontqRD.cardFront.cameraDistance = 8000 * scale
        binding.includebackRD.cardBack.cameraDistance = 8000 * scale
        frontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_in) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_right_out) as AnimatorSet
        loadData()
        binding.includebackRD.btnVerAdelante.setOnClickListener { flipCard() }
        binding.includefrontqRD.btnVerAtras.setOnClickListener { flipCard() }
    }
    private fun flipCard() {
        if (isValid) {
            isFront = if (isFront) {
                frontAnim.setTarget(binding.includefrontqRD.cardFront)
                backAnim.setTarget(binding.includebackRD.cardBack)
                frontAnim.start()
                backAnim.start()
                false
            } else {
                frontAnim.setTarget(binding.includebackRD.cardBack)
                backAnim.setTarget(binding.includefrontqRD.cardFront)
                backAnim.start()
                frontAnim.start()
                true
            }
        }
    }

    private fun loadData() {
        SharedUtils.showLoader(requireContext()!!, "Cargando Credencial...")
        val api = RestClient.buildBarrick()
        val call = api.getWorkerCredencial(SharedUtils.getUsuarioId(requireContext()), "RD")
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
                if (response.isSuccessful && response.body()!!.data.isNotEmpty()) {
                    if (response.body()!!.isSuccess ) {
                        isValid = true
                        val worker = response.body()!!.data[0]
                        binding.includefrontqRD.viewHeaderFront.visibility = View.GONE
                        binding.includefrontqRD.imgProfileKinross.visibility = View.VISIBLE
                        binding.includefrontqRD.MAIN.background = resources.getDrawable(R.color.barrick)
                        if (!worker?.foto.isNullOrEmpty())
                            loadImg(worker!!.foto)
                        else
                            binding.includefrontqRD.imgProfileKinross.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                        binding.includefrontqRD.imgQr.setImageBitmap(generateQR(SharedUtils.getUsuarioId(requireContext())))
                        binding.includefrontqRD.imgQr.adjustViewBounds = true
                        binding.includefrontqRD.lblEmpresa.text = worker?.empresa
                        if (!worker?.mandante.equals("SI")) {
                            binding.includefrontqRD.viewHeaderFront.background = resources.getDrawable(R.color.colorPrimaryDark)
                            binding.includebackRD.viewHeaderBack.background = resources.getDrawable(R.color.colorPrimaryDark)
                        }
                        binding.includefrontqRD.lblrut.text = SharedUtils.FormatRut(worker?.workerId.toString())
                        binding.includefrontqRD.lblFuncionario.text = "${worker?.nombres} ${worker?.apellidos}"
                        binding.includebackRD.lblVencLic.text = SharedUtils.getNiceDate(worker?.fechalic)
                        binding.includebackRD.lblLicMunicipal.text = if (worker?.clase.isNullOrEmpty()) "-" else worker?.clase
                        binding.includebackRD.lblVencPsico.text = SharedUtils.getNiceDate(worker?.fechasico)
                        binding.includebackRD.lblFecIngresoComp.text = SharedUtils.getNiceDate(worker?.fechain)
                        binding.includebackRD.lblZonaConduccion.text = worker?.zonasConduce
                        binding.includebackRD.lblTiposVehiculos.text = worker?.tipoVehiculos

                    } else {
                        isValid = false
                        binding.includefrontqRD.imgLogo.visibility = View.GONE
                        binding.includefrontqRD.textView16.visibility = View.VISIBLE
                        binding.includefrontqRD.lblMessage.visibility = View.VISIBLE
                        binding.includefrontqRD.lblMessage.text = "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
                    }
                } else {
                    isValid = false
                    binding.includefrontqRD.imgLogo.visibility = View.GONE
                    binding.includefrontqRD.textView16.visibility = View.VISIBLE
                    binding.includefrontqRD.lblMessage.visibility = View.VISIBLE
                    binding.includefrontqRD.lblMessage.text = "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
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
                .into(binding.includefrontqRD.imgProfileKinross!!)
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