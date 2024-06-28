package com.webcontrol.android.ui.credential

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.android.R
import com.webcontrol.android.data.model.WorkerCredentialBarrick
import com.webcontrol.android.data.network.ApiResponsBarrickCredential
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.ApiResponseBarrick
import com.webcontrol.android.ui.checklist.HistoricoCheckListFragment
import com.webcontrol.android.databinding.FragmentCredentialBarrickBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.core.utils.SharedUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.nav_submenu_item.*
import retrofit2.Call
import retrofit2.Response

object BAR {
    var dialogShown = false
}

@AndroidEntryPoint
class BarrickCredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialBarrickBinding
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true
    private var selectedCountry: String = ""

    companion object {
        const val title = "title"
        const val _dialogShown = "dialogShown"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = this.requireArguments().getString(title)
        BAR.dialogShown = this.requireArguments().getBoolean(_dialogShown)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCredentialBarrickBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density
        dialogCountry()
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
        loadData(selectedCountry)
        if (selectedCountry == "PE") {
            loadData("PE")
        }
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

    private fun loadData(selectedCountry: String?) {
        SharedUtils.showLoader(requireContext()!!, "Cargando Credencial...")
        val api = RestClient.buildBarrick()
        val countryCode = if (selectedCountry == "PE" || selectedCountry == "CL") {
            selectedCountry
        } else {
            "CL"
        }
        val call = api.getWorkerCredencial(SharedUtils.getUsuarioId(requireContext()), countryCode)
        call.enqueue(object : retrofit2.Callback<ApiResponseBarrick<WorkerCredentialBarrick>> {
            override fun onFailure(
                call: Call<ApiResponseBarrick<WorkerCredentialBarrick>>,
                t: Throwable
            ) {
                isValid = false
                SharedUtils.showToast(requireContext(), "Error obteniendo credenciales")
                Log.e("Error: ", t.toString())
                SharedUtils.dismissLoader(requireContext())
            }

            override fun onResponse(
                call: Call<ApiResponseBarrick<WorkerCredentialBarrick>>,
                response: Response<ApiResponseBarrick<WorkerCredentialBarrick>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess && response.body()!!.data.isNotEmpty()) {
                        isValid = true
                        val worker = response.body()!!.data[0]
                        binding.includefrontq.imgProfileKinross.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAcceso.visibility = View.VISIBLE
                        binding.includefrontq.txtSubtitle.visibility = View.VISIBLE
                        binding.includefrontq.lblTitleAutorizado.visibility = View.VISIBLE
                        if (!worker?.foto.isNullOrEmpty())
                            loadImg(worker!!.foto)
                        else
                            binding.includefrontq.imgProfileKinross.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                        binding.includefrontq.imgQr.setImageBitmap(
                            generateQR(
                                SharedUtils.getUsuarioId(
                                    requireContext()
                                )
                            )
                        )
                        binding.includefrontq.imgQr.adjustViewBounds = true
                        binding.includefrontq.lblEmpresa.text = worker?.empresa
                        if (!worker?.mandante.equals("SI")) {
                            binding.includefrontq.viewHeaderFront.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.color.colorExternalCredentialKS
                                )
                            binding.includeback.viewHeaderBack.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.color.colorExternalCredentialKS
                                )
                        }
                        binding.includefrontq.lblRut.text =
                            SharedUtils.FormatRut(worker?.workerId.toString())
                        binding.includefrontq.lblFuncionario.text =
                            "${worker?.nombres} ${worker?.apellidos}"
                        binding.includefrontq.lblCargo.text = worker?.rol
                        binding.includefrontq.lblAccesos.text =
                            if (worker?.zonasAcceso.isNullOrEmpty()) "-" else worker?.zonasAcceso
                        binding.includefrontq.lblAutorizado.text = worker?.autorizacion
                        binding.includeback.lblVencLic.text =
                            if (worker?.zonasAcceso.isNullOrEmpty()) "-" else SharedUtils.getNiceDate(worker?.fechalic)
                        binding.includeback.lblLicMunicipal.text =
                            if (worker?.municipal.isNullOrEmpty()) "-" else worker?.municipal
                        binding.includeback.lblVencPsico.text =
                            if (worker?.fechasico.isNullOrEmpty()) "-" else SharedUtils.getNiceDate(worker?.fechasico)
                        binding.includeback.lblFecIngresoComp.text =
                            if (worker?.fechain.isNullOrEmpty()) "-" else SharedUtils.getNiceDate(worker?.fechain)
                        binding.includeback.lblZonaConduccion.text = if (worker?.zonasConduce.isNullOrEmpty()) "-" else worker?.zonasConduce
                        binding.includeback.lblTiposVehiculos.text = if (worker?.tipoVehiculos.isNullOrEmpty()) "-" else worker?.tipoVehiculos
                        binding.includefrontq.lblMessage.visibility = View.GONE

                    } else {
                        isValid = false
                        binding.includefrontq.lblMessage.visibility = View.VISIBLE
                        binding.includefrontq.lblMessage.text =
                            "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
                    }
                } else {
                    isValid = false
                    binding.includefrontq.lblMessage.visibility = View.VISIBLE
                    binding.includefrontq.lblMessage.text =
                        "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
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
                .into(binding.includefrontq.imgProfileKinross!!)
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

    private fun dialogCountry() {
        val countries = mutableListOf("AR", "CL", "PE", "RD")
        countries.add(0, "Seleccionar")

        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_countries, null)
        val spinnerCountries = dialogView.findViewById<Spinner>(R.id.spinner_countries)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountries.adapter = adapter

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar país")
            .setView(dialogView)
            .setPositiveButton("Aceptar") { _, _ ->
                selectedCountry = countries[spinnerCountries.selectedItemPosition]
                destination()
            }
            .create()

        spinnerCountries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        dialog.show()
    }

    private fun destination() {
        when (selectedCountry) {
            "AR" -> {
                val bundle = bundleOf("title" to "Credenciales Barrick")
                val destination = R.id.fragmentAR
                findNavController().navigate(destination, bundle)
            }
            "RD" -> {
                val bundle = bundleOf("title" to "Credenciales Barrick")
                val destination = R.id.fragmentRD
                findNavController().navigate(destination, bundle)
            }
            else -> loadData(selectedCountry)
        }
    }
}