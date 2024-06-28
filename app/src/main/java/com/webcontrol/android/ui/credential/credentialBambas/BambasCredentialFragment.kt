package com.webcontrol.android.ui.credential.credentialBambas

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.R
import com.webcontrol.android.common.DocumentAdapter
import com.webcontrol.android.common.VehicleAdapter
import com.webcontrol.android.data.model.DocumentsBambas
import com.webcontrol.android.data.model.WorkerBambas
import com.webcontrol.android.databinding.FragmentCredentialBambasBinding
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@AndroidEntryPoint
class BambasCredentialFragment : Fragment() {
    private lateinit var binding: FragmentCredentialBambasBinding
    private val viewModel by sharedViewModel<BambasCredentialViewModel>()
    lateinit var worker: WorkerBambas
    var accessDocuments: List<DocumentsBambas> = ArrayList()
    var driverDocuments: List<DocumentsBambas> = ArrayList()
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = true
    private var isValid = true
    lateinit var vehiculos: String
    var listVehicle: List<String> = ArrayList()
    private var searchMode = false
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val SEARCH_MODE = "SEARCH_MODE"
        const val title = "title"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title=this.requireArguments().getString(title)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCredentialBambasBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkArguments()
        setUI()
        frontAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_in
        ) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.card_flip_right_out
        ) as AnimatorSet
        binding.btnFlip.setOnClickListener {
            binding.btnFlip.hide()
            flipCard()
            handler.postDelayed({
                binding.btnFlip.show()
            }, 1000)
        }
        if (!searchMode) {
            binding.credentialSearchContainer.visibility = View.GONE
            val userId = SharedUtils.getUsuarioId(requireContext())
            viewModel.getCredentialData(userId)
            viewModel.getDocumentsDataAccess(userId, 3057, true)
            viewModel.getDocumentsDataAccess(userId, 3062, false)
        } else {
            binding.credentialSearchContainer.visibility = View.VISIBLE
            binding.btnCredentialSearch.setOnClickListener {
                val value = binding.txtWorkerId.editText?.text.toString()
                if (!value.isNullOrEmpty()) {
                    viewModel.getCredentialData(value)
                    viewModel.getDocumentsDataAccess(value, 3057, true)
                    viewModel.getDocumentsDataAccess(value, 3062, false)
                } else
                    SharedUtils.showToast(requireContext(), "Ingrese un DNI")
            }
        }
        loadOberver()
        binding.includeback.btnTipoVehiculo.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_authorized_vehicles)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
            var titulo = dialog.findViewById<TextView>(R.id.txtTitulo)
            titulo.text = "Equipos Authorizados"
            var rvVehicle = dialog.findViewById<RecyclerView>(R.id.rcvVehiclesBambas)
            val adapter = VehicleAdapter(listVehicle)
            rvVehicle.adapter = adapter
            dialog.show()
        }
        binding.includeback.btnVigencia.setOnClickListener {
            dialog("ACCESOS", accessDocuments.sortedBy { it.nombreDocumento })
        }
        binding.includeback.btnVigenciasConductor.setOnClickListener {
            dialog("CONDUCTOR", driverDocuments.sortedBy { it.nombreDocumento })
        }
    }
    private fun loadOberver() {
        viewModel.workerInfoState().observe(viewLifecycleOwner) { itemWorkerBambas ->
            if (itemWorkerBambas.isLoading) {
                SharedUtils.showLoader(context, "Cargando...")
            } else {
                SharedUtils.dismissLoader(context)
                if (itemWorkerBambas.error == null && itemWorkerBambas.data != null) {
                    if (itemWorkerBambas.data!!.isSuccess) {
                        if (itemWorkerBambas.data!!.data != null) {
                            binding.includefrontq.lblMessage.visibility = View.GONE
                            worker = itemWorkerBambas.data!!.data
                            loadDataUI(worker)
                        } else {
                            binding.includefrontq.lblMessage.visibility = View.VISIBLE
                            binding.includefrontq.lblMessage.text =
                                "El trabajador con ID ${SharedUtils.getUsuarioId(requireContext())} no cuenta con credencial activa"
                            SharedUtils.dismissLoader(context)
                            binding.btnFlip.visibility =View.GONE
                            if (searchMode) {
                                binding.credentialEmptyContainer.visibility = View.VISIBLE
                                binding.includefrontq.root.visibility = View.GONE
                                binding.includeback.root.visibility = View.GONE
                                binding.btnFlip.visibility =View.GONE
                            } else {
                                binding.credentialEmptyContainer.visibility = View.GONE
                            }
                        }
                    } else {
                        Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_LONG)
                            .setAction("Reintentar") { }.show()
                        SharedUtils.dismissLoader(context)
                    }
                }
                SharedUtils.dismissLoader(context)
            }
        }
        viewModel.AccessdocumentsInfoState().observe(viewLifecycleOwner) { accessDocument ->
            if (accessDocument.isLoading) {
            } else {
                SharedUtils.dismissLoader(context)
                if (accessDocument.error == null && accessDocument.data != null) {
                    if (accessDocument.data!!.isSuccess) {
                        if (accessDocument.data!!.data != null) {
                            accessDocuments = accessDocument.data!!.data
                        }
                    } else {
                        Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_LONG)
                            .setAction("Reintentar") { }.show()
                        SharedUtils.dismissLoader(context)
                    }
                }
            }
        }
        viewModel.DriverdocumentsInfoState().observe(viewLifecycleOwner) { driverDocument ->
            if (driverDocument.isLoading) {
            } else {
                SharedUtils.dismissLoader(context)
                if (driverDocument.error == null && driverDocument.data != null) {
                    if (driverDocument.data!!.isSuccess) {
                        if (driverDocument.data!!.data != null) {
                            driverDocuments = driverDocument.data!!.data
                        }
                    } else {
                        Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_LONG)
                            .setAction("Reintentar") { }.show()
                        SharedUtils.dismissLoader(context)
                    }
                }
            }
        }
    }

    private fun loadDataUI(worker: WorkerBambas) {

        binding.credentialEmptyContainer.visibility = View.GONE
        binding.includefrontq.root.visibility = View.VISIBLE
        binding.includeback.root.visibility = View.VISIBLE
        binding.btnFlip.visibility =View.VISIBLE

        binding.includefrontq.textView162.visibility = View.VISIBLE
        binding.includefrontq.lblNombre.visibility = View.VISIBLE
        binding.includefrontq.lblGerencia.visibility = View.VISIBLE
        binding.includefrontq.lblArea.visibility = View.VISIBLE
        binding.includefrontq.lblCargo.visibility = View.VISIBLE
        binding.includefrontq.lblAutorizadoAcceso.visibility = View.VISIBLE
        binding.includefrontq.lblAutorizadoConducir.visibility = View.VISIBLE
        binding.includefrontq.imgProfile.visibility = View.VISIBLE
        if(worker.mandante== "SI"){
            binding.includefrontq.viewHeaderFront.setBackgroundColor(resources.getColor(R.color.colorCredentialBambasCont))
        }
        else{
            binding.includefrontq.viewHeaderFront.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        }
        binding.includefrontq.lblRutBambas.text = worker.identificador
        binding.includefrontq.txtFuncionario.text = worker.nombres + " " + worker.apellidos
        binding.includefrontq.txtGerencia.text = worker.gerencia
        binding.includefrontq.txtArea.text = worker.area
        binding.includefrontq.txtCargo.text = worker.cargo
        binding.includefrontq.txtAutorizadoAcceso.text = transformarAutorizacion(worker.autorizadoAcceso ?: "", "Acceso")
        binding.includefrontq.txtAutorizadoConducir.text =
            transformarAutorizacion(worker.autorizadoConducir ?: "", "Conducir")
        binding.includeback.txtEmpresaContratista.text = worker.empresaContratista
        binding.includefrontq.lblEmpresaBambas.text = worker.empresaContratista
        binding.includeback.txtEmpresaSubContratista.text = worker.empresaSubContratista
        binding.includeback.txtContrato.text = worker.nroContrato
        binding.includeback.txtVigenciaPase.text = worker.vigenciaAutorizado
        binding.includeback.txtNumeroLicencia.text = worker.nroLicencia
        binding.includeback.txtCategoriaLicencia.text = worker.categoria
        binding.includeback.txtUsoLentes.text = worker.usaLentes
        binding.includeback.lblItemsZonaConduccion.text = (worker.zonasCond ?: "").replace(",", "\n")
        if (obtenerArreglo((worker.tiposVehi ?: "")) < 4) binding.includeback.btnTipoVehiculo.visibility =
            View.GONE else binding.includeback.btnTipoVehiculo.visibility = View.VISIBLE
        vehiculos = (cortarVehiculo(worker.tiposVehi ?: "")).replace(",", "\n")
        binding.includeback.lblItemsVehiculos.text = vehiculos
        if (!worker.foto.isNullOrEmpty()) {
            loadImg(worker.foto)
        }
    }
    private fun cortarVehiculo(palabra:String):String{
        val list =palabra.split(",")
        var palabra = ""
        list.forEach{
            if(it.length>21)
                palabra = palabra+it.subSequence(0,21)+ ","
            else
                palabra = palabra +it+ ","
        }
        return palabra
    }
    private fun obtenerArreglo(lista: String): Int {
        listVehicle = lista.split(",").toTypedArray().toList()
        return listVehicle.size
    }

    private fun transformarAutorizacion(authorization: String, tipo: String): String {
        var authorizationAux = ""
        if (authorization.equals("APROBADO")) {
            authorizationAux = "SI"
            if (tipo == "Acceso") binding.includefrontq.txtAutorizadoAcceso.setTextColor(resources.getColor(R.color.colorPrimary))
            else binding.includefrontq.txtAutorizadoConducir.setTextColor(resources.getColor(R.color.colorPrimary))
        } else {
            authorizationAux = "NO"
            if (tipo == "Acceso") binding.includefrontq.txtAutorizadoAcceso.setTextColor(resources.getColor(R.color.red))
            else binding.includefrontq.txtAutorizadoConducir.setTextColor(resources.getColor(R.color.red))
        }
        return authorizationAux
    }

    private fun setUI() {
        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontq.cardFront2.cameraDistance = 8000 * scale
        binding.includeback.cardBack2.cameraDistance = 8000 * scale
        binding.includefrontq.lblEmpresaBambas.isSelected = true
        binding.credentialEmptyContainer.visibility = View.GONE
        if (searchMode) {
            viewModel.workerInfoState().value?.data = null
            viewModel.workerInfoState().value?.error = null
            viewModel.workerInfoState().value?.isLoading = false
            binding.includefrontq.root.visibility = View.GONE
            binding.includeback.root.visibility = View.GONE
            binding.btnFlip.visibility =View.GONE
        }
    }

    private fun checkArguments() {
        searchMode = requireArguments().getBoolean(SEARCH_MODE)
    }

    private fun flipCard() {
        if (isValid) {
            isFront = if (isFront) {
                frontAnim.setTarget(binding.includeback.cardBack2)
                backAnim.setTarget(binding.includefrontq.cardFront2)
                frontAnim.start()
                backAnim.start()
                false
            } else {
                frontAnim.setTarget(binding.includefrontq.cardFront2)
                backAnim.setTarget(binding.includeback.cardBack2)
                backAnim.start()
                frontAnim.start()
                true
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
            GlideApp.with(this)
                .load(bitmap)
                .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
                .error(R.drawable.ic_account_circle_materialgrey_240dp)
                .circleCrop()
                .into(binding.includefrontq.imgProfile)
        } catch (e: Exception) {
            SharedUtils.showToast(requireContext(), e.message)
        }
    }

    private fun dialog(Subtitulo: String, items: List<DocumentsBambas>) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_vigencia_documentos_accesos)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        var nulo = dialog.findViewById<ConstraintLayout>(R.id.dataEmpty)
        var titulo = dialog.findViewById<TextView>(R.id.txtTitulo)
        titulo.text = getString(R.string.vigencia_documentos)
        var subtitulo = dialog.findViewById<TextView>(R.id.txtTitulo2)
        titulo.text = getString(R.string.vigencia_documentos)
        subtitulo.text = Subtitulo
        var rvDocument = dialog.findViewById<RecyclerView>(R.id.rcvDocumentosBambas)
        val adapter = DocumentAdapter(items)
        rvDocument.adapter = adapter
        if (items.isEmpty()) nulo.visibility = View.VISIBLE
        else nulo.visibility = View.GONE
        dialog.show()
    }
}