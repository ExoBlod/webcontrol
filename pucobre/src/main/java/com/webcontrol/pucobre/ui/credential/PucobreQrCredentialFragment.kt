package com.webcontrol.pucobre.ui.credential

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.findBinding
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Loading
import com.webcontrol.core.common.model.Success
import com.webcontrol.core.utils.LocalStorage
import com.webcontrol.core.utils.SharedUtils
import com.webcontrol.pucobre.R
import com.webcontrol.pucobre.data.model.VehicleList
import com.webcontrol.pucobre.databinding.FragmentCredentialQrPucobreBinding
import com.webcontrol.pucobre.ui.credential.adapter.CredentialVehicleAdapter
import com.webcontrol.pucobre.ui.security.CredentialViewModel
import com.webcontrol.pucobre.ui.security.SecurityViewModel
import com.webcontrol.pucobre.ui.security.WorkerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.*
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.btnInfo
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.btnInfo2
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.btnInfo3
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.btnVerAdelante
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.cardBack
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.lblFecIngresoComp
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.lblLicMunicipal
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.lblVencLic
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.lblVencPsico
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.lblZonaConduccion
import kotlinx.android.synthetic.main.fragment_credential_back_pucobre.viewHeaderBack
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.*
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.btnVerAtras
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.cardFront
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.imgProfile
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.imgQr
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblAccesos
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblAutorizado
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblCargo
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblDpto
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblEmpresa
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblFaena
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblFuncionario
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblGerencias
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblMessage
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblRut
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblSuperintendencia
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblTitleAcceso
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblTitleAutorizado
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblTitleDpto
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblTitleFaena
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblTitleGerencia
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.lblTitleSuperInt
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.txtSubtitle
import kotlinx.android.synthetic.main.fragment_credential_front_pucobre.viewHeaderFront
import kotlinx.android.synthetic.main.fragment_lectura_qr.*
import kotlinx.android.synthetic.main.fragment_lectura_qr.lblMensaje
import kotlinx.android.synthetic.main.fragment_lectura_qr_credential.*
import javax.inject.Inject



@AndroidEntryPoint
class PucobreQrCredentialFragment : Fragment() {
    @Inject
    lateinit var localStorage: LocalStorage
    private lateinit var binding: FragmentCredentialQrPucobreBinding
    private val securityViewModel by viewModels<SecurityViewModel>()
    private val credentialViewModel by activityViewModels<CredentialViewModel>()
    private val workerViewModel by viewModels<WorkerViewModel>()
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var workerid = ""
    private var isValid = true
    private val handler = Handler(Looper.getMainLooper())
    companion object {
        const val title = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Credencial Personal"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCredentialQrPucobreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale: Float = requireContext().resources.displayMetrics.density
        binding.credentialFrontQR.cardFrontQR.cameraDistance = 8000 * scale
        binding.credentialBackQR.cardBackQR.cameraDistance = 8000 * scale
        frontAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_in
        ) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_out
        ) as AnimatorSet
        SharedUtils.showLoader(requireContext()!!, "Cargando Credencial...")
        //checkRUT()
        loadData()
        binding.credentialBackQR.btnVerAdelanteQR.setOnClickListener { binding.credentialBackQR.btnVerAdelanteQR.hide()
            flipCard()
            handler.postDelayed({
                binding.credentialBackQR.btnVerAdelanteQR.show()
            }, 1000) }
        binding.credentialFrontQR.btnVerAtrasQR.setOnClickListener { binding.credentialFrontQR.btnVerAtrasQR.hide()
            flipCard()
            handler.postDelayed({
                binding.credentialFrontQR.btnVerAtrasQR.show()
            }, 1000) }
        binding.credentialBackQR.btnInfoQR.setOnClickListener{ personalData(requireContext()) }
        binding.credentialBackQR.btnInfo2QR.setOnClickListener { emergencyData(requireContext()) }
        binding.credentialBackQR.btnInfo3QR.setOnClickListener { inMine(requireContext()) }

    }

    private fun flipCard() {
        if (isValid) {
            isFront = if (isFront) {
                frontAnim.setTarget(binding.credentialFrontQR.cardFrontQR)
                backAnim.setTarget(binding.credentialBackQR.cardBackQR)
                frontAnim.start()
                backAnim.start()
                false
            } else {
                frontAnim.setTarget(binding.credentialBackQR.cardBackQR)
                backAnim.setTarget(binding.credentialFrontQR.cardFrontQR)
                backAnim.start()
                frontAnim.start()
                true
            }
        }
    }

    private fun loadData() {
        credentialViewModel.getCredential(localStorage["RUT_QR", ""]?: "")
        credentialViewModel.credential.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> {
                    // TODO: add loader
                }
                is Success -> {
                    isValid = true
                    val worker = result.data
                    if (worker.workerCredentialPucobre == null) {
                        isValid = false
                        binding.credentialFrontQR.viewHeaderFrontQR.setBackgroundColor(resources.getColor(R.color.colorCredentialKS))
                        binding.credentialBackQR.viewHeaderBackQR.setBackgroundColor(resources.getColor(R.color.colorCredentialKS))
                        binding.credentialFrontQR.showSuccessAccredited.visibility = View.GONE
                        binding.credentialFrontQR.showNoAcredited.visibility = View.VISIBLE
                        binding.credentialFrontQR.lblMessageFail.text =
                            "El trabajador con ID ${SharedUtils.getRutQr(requireContext())} no cuenta con credencial activa"
                        //findNavController().navigate(R.id.invalidCredential)
                        return@observe
                        SharedUtils.dismissLoader(requireContext())
                    }else{
                        showSuccessAccredited.visibility = View.VISIBLE
                        showNoAcredited.visibility = View.GONE
                    }
                    binding.credentialFrontQR.imgProfileQR.visibility = View.VISIBLE
                    binding.credentialFrontQR.lblTitleAccesoQR.visibility = View.VISIBLE
                    binding.credentialFrontQR.txtSubtitleQR.visibility = View.VISIBLE
                    binding.credentialFrontQR.lblTitleGerenciaQR.visibility = View.VISIBLE
                    binding.credentialFrontQR.lblTitleSuperIntQR.visibility = View.VISIBLE
                    binding.credentialFrontQR.lblTitleDptoQR.visibility = View.VISIBLE
                    binding.credentialFrontQR.lblTitleAutorizadoQR.visibility = View.VISIBLE
                    binding.credentialFrontQR.lblTitleFaenaQR.visibility = View.VISIBLE
                    binding.credentialFrontQR.lblTitleConductorQR.visibility = View.VISIBLE
                    if (worker.workerCredentialPucobre.foto.isNotEmpty())
                        loadImg(worker.workerCredentialPucobre.foto)
                    else
                        binding.credentialFrontQR.imgProfileQR.setImageResource(R.drawable.ic_account_circle_materialgrey_240dp)
                    binding.credentialFrontQR.imgQr2.setImageBitmap(generateQR(worker.workerCredentialPucobre.workerId))
                    binding.credentialFrontQR.imgQr2.adjustViewBounds = true
                    binding.credentialFrontQR.lblEmpresaQR.text = worker.workerCredentialPucobre.workerEmpresa
                    if (worker.workerCredentialPucobre.mandante.equals("SI")) {
                        binding.credentialFrontQR.viewHeaderFrontQR.background = resources.getDrawable(R.color.colorCredentialPUCOBRE2)
                        binding.credentialBackQR.viewHeaderBackQR.background = resources.getDrawable(R.color.colorCredentialPUCOBRE2)
                    }else if (worker.workerCredentialPucobre.mandante.equals("NO")){
                        binding.credentialFrontQR.viewHeaderFrontQR.background = resources.getDrawable(R.color.colorCredentialExterno)
                        binding.credentialBackQR.viewHeaderBackQR.background = resources.getDrawable(R.color.colorCredentialExterno)
                    }

                    binding.credentialFrontQR.lblRutQR.text = SharedUtils.FormatRutPucobre(worker.workerCredentialPucobre.workerId)
                    binding.credentialFrontQR.lblFuncionarioQR.text = "${worker.workerCredentialPucobre.workerNombres} ${worker.workerCredentialPucobre.workerApellidos}"
                    binding.credentialFrontQR.lblCargoQR.text = worker.workerCredentialPucobre.rol
                    binding.credentialFrontQR.lblAccesosQR.text =
                        if (worker.workerCredentialPucobre.zonasAcceso.isNullOrEmpty()) "-" else worker.workerCredentialPucobre.zonasAcceso
                    binding.credentialFrontQR.lblAutorizadoQR.text = worker.workerCredentialPucobre.workerAutorizado
                    binding.credentialBackQR.lblVencLicQR.text = SharedUtils.getNiceDate(worker.workerCredentialPucobre.workerFlicconducir)
                    binding.credentialBackQR.lblLicMunicipalQR.text =
                        if (worker.workerCredentialPucobre.municipal.isNullOrEmpty()) "-" else worker.workerCredentialPucobre.municipal
                    binding.credentialBackQR.lblVencPsicoQR.text = SharedUtils.getNiceDate(worker.workerCredentialPucobre.workerPsico)
                    binding.credentialBackQR.lblFecIngresoCompQR.text = if(SharedUtils.getNiceDate(worker.workerCredentialPucobre.fechain).isNullOrEmpty()) "-" else SharedUtils.getNiceDate(worker.workerCredentialPucobre.fechain)
                    binding.credentialBackQR.lblZonaConduccionQR.text = worker.workerCredentialPucobre.zonasConduce
                    binding.credentialFrontQR. lblGerenciasQR.text = if (worker.workerCredentialPucobre.workerGerencias.isNullOrEmpty()) "-" else worker.workerCredentialPucobre.workerGerencias
                    binding.credentialFrontQR.lblDptoQR.text = if (worker.workerCredentialPucobre.workerDepartamento.isNullOrEmpty()) "-" else worker.workerCredentialPucobre.workerDepartamento
                    binding.credentialFrontQR.lblSuperintendenciaQR.text = if (worker.workerCredentialPucobre.workerSuperintendencia.isNullOrEmpty()) "-" else worker.workerCredentialPucobre.workerSuperintendencia
                    binding.credentialFrontQR.lblFaenaQR.text = if (worker.workerCredentialPucobre.workerFaena.isNullOrEmpty()) "-" else worker.workerCredentialPucobre.workerFaena
                    binding.credentialFrontQR.lblConductorQR.text = if (worker.workerCredentialPucobre.conductor.isNullOrEmpty()) "-" else worker.workerCredentialPucobre.conductor
                    binding.credentialBackQR.txtVacio.visibility = if (worker.vehicleList.isNullOrEmpty()) View.VISIBLE else View.GONE
                    initContentCredentialBackVehicle(worker.vehicleList)
                    SharedUtils.dismissLoader(requireContext())
                }
                is Error -> {
                    isValid = false
                    Toast.makeText(context, "Error obteniendo credencial", Toast.LENGTH_SHORT)
                        .show()

                }
            }

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
                .into(binding.credentialFrontQR.imgProfileQR!!)
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

    private var dialog: Dialog? = null
    private fun personalData(context : Context){
        credentialViewModel.getCredential(localStorage["RUT_QR", ""]?: "")
        credentialViewModel.credential.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> {
                    // TODO: add loader
                }
                is Success -> {
                    isValid = true
                    val worker = result.data
                    val dialogBinding = layoutInflater.inflate(R.layout.popup_information, null)
                    val myDialog = Dialog(context)
                    val yesBtn = dialogBinding.findViewById<Button>(R.id.btnOK)
                    val title = dialogBinding.findViewById<TextView>(R.id.title)
                    val direccion = dialogBinding.findViewById<TextView>(R.id.first)
                    val telefono = dialogBinding.findViewById<TextView>(R.id.second)
                    val gsanguineo = dialogBinding.findViewById<TextView>(R.id.third)
                    with(myDialog) {
                        setContentView(dialogBinding)
                        setCancelable(true)
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        show()
                        title.text = "Datos personales"
                        direccion.text = if(worker.workerCredentialPucobre.workerDireccion.isNullOrEmpty()) "Direccion: -" else "Direccion: "+worker.workerCredentialPucobre.workerDireccion
                        telefono.text = if(worker.workerCredentialPucobre.workerTelefono.isNullOrEmpty()) "Telefono: -" else "Telefono: "+worker.workerCredentialPucobre.workerTelefono
                        gsanguineo.text = if(worker.workerCredentialPucobre.gsangre.isNullOrEmpty()) "Grupo Sanguineo: -" else "Grupo Sanguineo: "+worker.workerCredentialPucobre.gsangre
                        credentialViewModel.credential.removeObservers(viewLifecycleOwner)
                        yesBtn.setOnClickListener {
                            Log.d("POPUP", "Botón Aceptar presionado")
                            dismiss() }
                    }
                }
                is Error -> {
                    isValid = false
                    Toast.makeText(context, "Error obteniendo datos personales", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun emergencyData(context : Context){
        val dialogBinding = layoutInflater.inflate(R.layout.popup_information, null)
        val myDialog = Dialog(context)
        val yesBtn = dialogBinding.findViewById<Button>(R.id.btnOK)
        val title = dialogBinding.findViewById<TextView>(R.id.title)
        val first = dialogBinding.findViewById<TextView>(R.id.first)
        val second = dialogBinding.findViewById<TextView>(R.id.second)
        val third = dialogBinding.findViewById<TextView>(R.id.third)
        with(myDialog) {
            setContentView(dialogBinding)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            title.text = "En caso de emergencia"
            first.text = "Informe a su supervisor directo"
            second.text = "Policlínico +56977687035 - Anexo *911"
            third.text = "Policlínico 522209439 - Anexo 2439"
            yesBtn.setOnClickListener { dismiss() }
        }
    }

    private fun initContentCredentialBackVehicle(credentialBackVehicle: ArrayList<VehicleList>) {
        with(binding) {
            val recycleViewVehicle = binding.credentialBackQR.rvVehicleListQR
            recycleViewVehicle.layoutManager = LinearLayoutManager(requireContext())
            recycleViewVehicle.adapter = CredentialVehicleAdapter(credentialBackVehicle)
        }
    }
    private fun inMine(context : Context){
        credentialViewModel.getCredential(localStorage["RUT_QR", ""]?: "")
        credentialViewModel.credential.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> {
                    // TODO: add loader
                }
                is Success -> {
                    isValid = true
                    val worker = result.data
                    val dialogBinding = layoutInflater.inflate(R.layout.popup_information, null)
                    val myDialog = Dialog(context)
                    val yesBtn = dialogBinding.findViewById<Button>(R.id.btnOK)
                    val title = dialogBinding.findViewById<TextView>(R.id.title)
                    val first = dialogBinding.findViewById<TextView>(R.id.first)
                    val second = dialogBinding.findViewById<TextView>(R.id.second)
                    val third = dialogBinding.findViewById<TextView>(R.id.third)
                    with(myDialog) {
                        setContentView(dialogBinding)
                        setCancelable(true)
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        show()
                        title.text = "Ingreso mina"
                        first.text = if(worker.workerCredentialPucobre.superficie.isNullOrEmpty()) "Superficie: -" else "Superficie: "+worker.workerCredentialPucobre.superficie
                        second.text = if(worker.workerCredentialPucobre.subterranea.isNullOrEmpty()) "Subterranea: -" else "Subterranea: "+worker.workerCredentialPucobre.subterranea
                        third.text = if(worker.workerCredentialPucobre.planta.isNullOrEmpty()) "Planta: -" else "Planta: "+worker.workerCredentialPucobre.planta
                        credentialViewModel.credential.removeObservers(viewLifecycleOwner)
                        yesBtn.setOnClickListener {
                            Log.d("POPUP", "Botón Aceptar presionado")
                            dismiss() }
                    }
                }
                is Error -> {
                    isValid = false
                    Toast.makeText(context, "Error obteniendo datos mina", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun checkRUT() {
        if (localStorage["RUT_QR",""]!=null){
            workerid = localStorage["RUT_QR", ""]?: "";
        }else{
            workerid = localStorage["USER_ID", ""]?: "";
        }
    }
}
