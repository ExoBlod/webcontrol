package com.webcontrol.android.angloamerican.ui.credentialVirtual

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.webcontrol.android.R
import com.webcontrol.android.common.VehiculoAdapter
import com.webcontrol.android.data.network.dto.AuthorizationPlacesDTO
import com.webcontrol.android.data.network.dto.AuthorizedVehicleDTO
import com.webcontrol.android.data.network.dto.DocumentValidityDTO
import com.webcontrol.android.data.network.dto.VehicleCategory
import com.webcontrol.android.data.network.dto.VehicleType
import com.webcontrol.android.databinding.FragmentCredentialVirtualQvBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.data.network.dto.CredentialVirtualDTO
import com.webcontrol.android.data.network.CredentialVirtualRequest
import com.webcontrol.android.data.network.dto.CredentialVirtualDocuments
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@AndroidEntryPoint
class QuellavecoCredentialVirtualFragment : Fragment() {
    private val viewModel by sharedViewModel<QuellavecoCredentialVirtualViewModel>()
    private lateinit var binding: FragmentCredentialVirtualQvBinding
    lateinit var listAuthorizedVehicle: ArrayList<VehicleCategory>
    lateinit var nombreDocumentoAccess: ArrayList<String>
    lateinit var fechaDocumentoAccess: ArrayList<String>
    lateinit var nombreDocumentoDriver: ArrayList<String>
    lateinit var fechaDocumentoDriver: ArrayList<String>
    lateinit var worker: CredentialVirtualDTO
    lateinit var documentsAccess: List<CredentialVirtualDocuments>
    lateinit var documentsDriver: List<CredentialVirtualDocuments>
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Credencial Virtual"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCredentialVirtualQvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontq.constraintFront.cameraDistance = 8000 * scale
        binding.includeback.constraintBack.cameraDistance = 8000 * scale

        frontAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_in
        ) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_out
        ) as AnimatorSet

        listAuthorizedVehicle = ArrayList()
        loadObserver()

        viewModel.getCredentialVirtual(CredentialVirtualRequest(SharedUtils.getUsuarioId(context)))

        binding.btnFlip.setOnClickListener {
            binding.btnFlip.hide()
            flipCard()
            handler.postDelayed({
                binding.btnFlip.show()
            }, 1000)
        }

        binding.includeback.btnVigencia.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_vigencia_documentos_qv)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
            var dataEmptyLayout = dialog.findViewById<ConstraintLayout>(R.id.dataEmpty)
            var titulo = dialog.findViewById<TextView>(R.id.txtTitulo)
            titulo.text = getString(R.string.vigencia_documentos_accesos)
            var nombre = dialog.findViewById<TextView>(R.id.txtNombreDocumento)
            var fecha = dialog.findViewById<TextView>(R.id.txtFechaDocumento)
            if (nombreDocumentoAccess.isNotEmpty() && fechaDocumentoAccess.isNotEmpty()) {
                dataEmptyLayout.visibility = View.GONE
                nombre.text = nombreDocumentoAccess.joinToString("\n")
                fecha.text = fechaDocumentoAccess.joinToString("\n")
            } else {
                dataEmptyLayout.visibility = View.VISIBLE
                nombre.visibility=View.GONE
                fecha.visibility=View.GONE
            }
            dialog.show()
        }
        binding.includeback.btnVigenciasConductor.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_vigencia_documentos_qv)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
            var dataEmptyLayout = dialog.findViewById<ConstraintLayout>(R.id.dataEmpty)
            var titulo = dialog.findViewById<TextView>(R.id.txtTitulo)
            titulo.text = getString(R.string.vigencia_documentos_conductor)
            var nombre = dialog.findViewById<TextView>(R.id.txtNombreDocumento)
            var fecha = dialog.findViewById<TextView>(R.id.txtFechaDocumento)
            if (nombreDocumentoDriver.isNotEmpty() && fechaDocumentoDriver.isNotEmpty()) {
                dataEmptyLayout.visibility = View.GONE
                nombre.text = nombreDocumentoDriver.joinToString("\n")
                fecha.text = fechaDocumentoDriver.joinToString("\n")
            } else {
                dataEmptyLayout.visibility = View.VISIBLE
                nombre.visibility=View.GONE
                fecha.visibility=View.GONE
            }
            dialog.show()
        }

    }

    private fun loadObserver() {
        viewModel.credentialVirtualState().observe(viewLifecycleOwner) { credential ->
            if (credential.isLoading)
                SharedUtils.showLoader(context, "Cargando...")
            else {
                SharedUtils.dismissLoader(context)
                if(credential.error== null) {
                    if (credential.data!!.isSuccess && credential.data!=null) {
                        worker = credential.data.data
                        loadDataUI(worker)
                    }
                    else{
                        binding.includefrontq.constraintFront.visibility=View.INVISIBLE
                        binding.includeback.constraintBack.visibility=View.INVISIBLE
                        binding.btnFlip.visibility=View.INVISIBLE
                        binding.credentialEmptyContainer.visibility=View.VISIBLE
                    }
                }
            }
        }
    }

    private fun loadDataDocumentsAccess(Documents: List<CredentialVirtualDocuments>) {
        nombreDocumentoAccess = arrayListOf<String>()
        fechaDocumentoAccess = arrayListOf<String>()
        Documents.forEach {
            nombreDocumentoAccess.add(it.NOMBRE_DOCUMENTO)
            fechaDocumentoAccess.add(": "+ObtenerFecha(it.FECHA_VIGENCIA))
        }
    }
    private fun loadDataDocumentsDriver(Documents: List<CredentialVirtualDocuments>) {
        nombreDocumentoDriver = arrayListOf<String>()
        fechaDocumentoDriver = arrayListOf<String>()
        Documents.forEach {
            nombreDocumentoDriver.add(it.NOMBRE_DOCUMENTO)
            fechaDocumentoDriver.add(": "+ObtenerFecha(it.FECHA_VIGENCIA))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadDataUI(worker: CredentialVirtualDTO) {
        binding.includefrontq.lblEmpresaQuellaveco.text = worker.credentialFront.empresa
        binding.includefrontq.lblRutQuellaveco.text = worker.credentialFront.rut
        binding.includefrontq.ivQr.setImageBitmap(SharedUtils.generateQR(SharedUtils.getUsuarioId(context)))
        binding.includefrontq.txtFuncionario.text = worker.credentialFront.nombres +" "+ worker.credentialFront.apellidos
        binding.includefrontq.txtGerencia.text = worker.credentialFront.gerencia
        binding.includefrontq.txtArea.text = worker.credentialFront.area
        binding.includefrontq.txtCargo.text = worker.credentialFront.cargo
        binding.includefrontq.txtAutorizadoAcceso.text = worker.credentialFront.autorizadoAcceso
        binding.includefrontq.txtAutorizadoConducir.text = worker.credentialFront.autorizadoConductor
        worker.credentialFront.foto?.let { loadImg(it) }
        binding.includeback.txtEmpresaContratista.text= worker.credentialBack.contratista
        binding.includeback.txtEmpresaSubContratista.text= worker.credentialBack.subcontratista
        binding.includeback.txtContrato.text= worker.credentialBack.contrato
        binding.includeback.txtVigenciaPase.text = ObtenerFecha(worker.credentialBack.vigencia)
        binding.includeback.txtNumeroLicencia.text= worker.credentialBack.nroLicencia
        binding.includeback.txtCategoriaLicencia.text= worker.credentialBack.categorias
        binding.includeback.txtUsoLentes.text= worker.credentialBack.usaLentes

        worker.credentialBack.docAccess?.let {loadDataDocumentsAccess(it)}
        worker.credentialBack.docDriver?.let {loadDataDocumentsDriver(it)}
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
                .into(binding.includefrontq.imgProfile!!)
        } catch (e: Exception) {
            SharedUtils.showToast(requireContext(), e.message)
        }
    }
    private fun flipCard() {
        if (isValid) {
            isFront = if (isFront) {
                frontAnim.setTarget(binding.includefrontq.constraintFront)
                backAnim.setTarget(binding.includeback.constraintBack)
                frontAnim.start()
                backAnim.start()
                false
            } else {
                frontAnim.setTarget(binding.includeback.constraintBack)
                backAnim.setTarget(binding.includefrontq.constraintFront)
                backAnim.start()
                frontAnim.start()
                true
            }
        }
    }

    private fun ObtenerFecha(date: String): String {
        val arregloFecha: MutableList<String> = ArrayList()
        var fechaFormateada = "---"

        if (date.isNotEmpty()) {
            arregloFecha.add(date.subSequence(0, 4).toString())
            arregloFecha.add(date.subSequence(4, 6).toString())
            arregloFecha.add(date.subSequence(6, 8).toString())
            fechaFormateada =
                arregloFecha[2] + "-" + arregloFecha[1] + "-" + arregloFecha[0]
        }

        return fechaFormateada
    }
}