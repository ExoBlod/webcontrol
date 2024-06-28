package com.webcontrol.android.angloamerican.ui.credentialLicencia

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.common.VehiculoAdapter
import com.webcontrol.android.data.network.dto.*
import com.webcontrol.android.databinding.FragmentCredentialQuellavecosBinding
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@AndroidEntryPoint
class QuellavecoCredentialLicenciaFragment : Fragment() {
    private val viewModel by sharedViewModel<QuellavecoCredentialLicenciaViewModel>()
    private lateinit var binding: FragmentCredentialQuellavecosBinding
    lateinit var listAuthorizedVehicle: ArrayList<VehicleCategory>
    lateinit var aux1: List<AuthorizationPlacesDTO>
    lateinit var aux2: List<AuthorizationPlacesDTO>
    lateinit var nombreDocumento: ArrayList<String>
    lateinit var fechaDocumento: ArrayList<String>
    lateinit var vehiculoAdapter: VehiculoAdapter
    lateinit var worker: DataWorkerDTO
    lateinit var authorizationPlaces: List<AuthorizationPlacesDTO>
    lateinit var authorizationVehicles: List<AuthorizedVehicleDTO>
    lateinit var documents: List<DocumentValidityDTO>
    lateinit var frontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    private var isFront = false
    private var isValid = true
    lateinit var authorization: String
    lateinit var authorization2: String
    lateinit var categoryList: List<String>
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Credencial Licencia"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCredentialQuellavecosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scale: Float = requireContext().resources.displayMetrics.density
        binding.includefrontq.frontQuellaveco.cameraDistance = 8000 * scale
        binding.includebackq.backQuellaveco.cameraDistance = 8000 * scale

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


        viewModel.getLicenseData(SharedUtils.getUsuarioId(context))
        viewModel.getAuthorizationPlaces(SharedUtils.getUsuarioId(context))
        viewModel.getAuthorizationVehicle(SharedUtils.getUsuarioId(context))
        viewModel.getDocuments(SharedUtils.getUsuarioId(context))

        try {
            var urlPhoto = "%sworker/%s/photo"
            urlPhoto = String.format(
                urlPhoto,
                getString(R.string.ws_url_anglo),
                SharedUtils.getUsuarioId(context)
            )
            var token = SharedUtils.getTokenAuthorization(App.getContext())
            val glideUrl = GlideUrl(urlPhoto) { mapOf(Pair("Authorization", "Bearer $token")) }
            Glide.with(this)
                .load(glideUrl)
                .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
                .error(R.drawable.ic_account_circle_materialgrey_240dp)
                .circleCrop()
                .into(binding.includefrontq.imageAvatar)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        binding.btnFlipQuellaveco.setOnClickListener {
            binding.btnFlipQuellaveco.hide()
            flipCard()
            handler.postDelayed({
                binding.btnFlipQuellaveco.show()
            }, 1000)
        }


        binding.includebackq.btnBuscarCredencial.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_vigencia_documentos)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
            var titulo = dialog.findViewById<TextView>(R.id.txtTitulo)
            titulo.text = getString(R.string.vigencia_documentos)
            var nombre = dialog.findViewById<TextView>(R.id.txtNombreDocumento)
            nombre.text = nombreDocumento.joinToString("\n")
            var fecha = dialog.findViewById<TextView>(R.id.txtFechaDocumento)
            fecha.text = fechaDocumento.joinToString("\n")
            dialog.show()
        }
    }

    private fun loadObserver() {
        viewModel.workerInfoState().observe(viewLifecycleOwner) { itemWorkerGV ->
            if (itemWorkerGV.isLoading)
                SharedUtils.showLoader(context, "Cargando...")
            else {
                SharedUtils.dismissLoader(context)
                if (itemWorkerGV.error == null) {
                    if (itemWorkerGV.data!!.isSuccess) {
                        if (itemWorkerGV.data.data.isNotEmpty()) {
                            worker = itemWorkerGV.data.data[0]
                            loadDataUI(worker)
                        }
                    } else {
                        Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_LONG)
                            .setAction("Reintentar") { }.show()
                        SharedUtils.dismissLoader(context)
                    }
                }
            }
        }

        viewModel.AuthorizationPlaces().observe(viewLifecycleOwner) { authorizationPlace ->
            if (authorizationPlace.isLoading)
            //SharedUtils.showLoader(context,"Cargando...")
            else {
                SharedUtils.dismissLoader(context)
                if (authorizationPlace.error == null) {
                    if (authorizationPlace.data!!.isSuccess) {
                        if (authorizationPlace.data.data.isEmpty()) {
                            binding.includefrontq.lblItems.text = ""
                            binding.includefrontq.lblItems2.text = ""
                        } else {
                            authorizationPlaces = authorizationPlace.data.data
                            loadDataPlaces(authorizationPlaces)
                        }

                    } else {
                        Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_LONG)
                            .setAction("Reintentar") { }.show()
                        SharedUtils.dismissLoader(context)
                    }
                }
            }
        }

        viewModel.AuthorizationVehicle().observe(viewLifecycleOwner) { authorizationVehicle ->
            if (authorizationVehicle.isLoading)
            //SharedUtils.showLoader(context,"Cargando...")
            else {
                SharedUtils.dismissLoader(context)
                if (authorizationVehicle.error == null) {
                    if (authorizationVehicle.data!!.isSuccess) {
                        if (authorizationVehicle.data.data.isEmpty()) {

                        } else {
                            authorizationVehicles = authorizationVehicle.data.data
                            loadData(authorizationVehicles)
                        }

                    } else {
                        Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_LONG)
                            .setAction("Reintentar") {
                                //loadData()
                            }.show()
                        SharedUtils.dismissLoader(context)
                    }
                }
            }
        }

        viewModel.documents().observe(viewLifecycleOwner) { listDocuments ->
            if (listDocuments.isLoading)
            //SharedUtils.showLoader(context,"Cargando...")
            else {
                SharedUtils.dismissLoader(context)
                if (listDocuments.error == null) {
                    if (listDocuments.data!!.isSuccess) {
                        if (listDocuments.data.data.isEmpty()) {

                        } else {
                            documents = listDocuments.data.data
                            loadDataDocuments(documents)
                        }

                    } else {
                        Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_LONG)
                            .setAction("Reintentar") {
                                //loadData()
                            }.show()
                        SharedUtils.dismissLoader(context)
                    }
                }
            }
        }
    }

    private fun loadDataDocuments(Documents: List<DocumentValidityDTO>) {
        nombreDocumento = arrayListOf<String>()
        fechaDocumento = arrayListOf<String>()
        Documents.forEach {
            nombreDocumento.add(it.NOMBRE_DOCSFEC)
            fechaDocumento.add(ObtenerFecha(it.FECHA_VIGENCIA ?: ""))
        }
    }

    private fun loadDataPlaces(authorizationPlaces: List<AuthorizationPlacesDTO>) {
        authorization = ""
        authorization2 = ""
        aux1 = authorizationPlaces.subList(0, authorizationPlaces.size / 2)
        aux2 = authorizationPlaces.subList(authorizationPlaces.size / 2, authorizationPlaces.size)

        for (i in aux1.indices) {
            authorization = authorization + aux1[i].LUGARAUTORIZADO + ("\n")
        }
        for (i in aux2.indices) {
            authorization2 = authorization2 + aux2[i].LUGARAUTORIZADO + ("\n")
        }
        binding.includefrontq.lblItems.text = authorization
        binding.includefrontq.lblItems2.text = authorization2
    }

    private fun loadDataUI(worker: DataWorkerDTO) {
        var tipo = ""
        var signo = ""

        worker.TIPO_SANGRE?.let {
            if (worker.TIPO_SANGRE.isNotEmpty()){
                tipo = worker.TIPO_SANGRE.subSequence(0, worker.TIPO_SANGRE.length - 1).toString()
                signo = worker.TIPO_SANGRE.subSequence(
                    worker.TIPO_SANGRE.length - 1,
                    worker.TIPO_SANGRE.length
                ).toString()
            }
        }

        binding.includefrontq.txtName.text = worker.NOMBRES
        binding.includefrontq.txtApellido.text = worker.APELLIDOS
        binding.includefrontq.txtDNI.text = "DNI ${worker.RUT}"
        binding.includefrontq.txtBloodType.text = worker.TIPO_SANGRE
        binding.includefrontq.txtCompany.text = worker.EMPRESA
        binding.includefrontq.txtArea.text = worker.AREA
        binding.includefrontq.txtJob.text = worker.PUESTO
        binding.includefrontq.txtAuthorizationDrive.text = worker.AUTORIZADO_CONDUCIR
        binding.includefrontq.txtExpeditionDate.text = ObtenerFecha(worker.FECHA_EXPEDICION ?: "")
        binding.includefrontq.txtExpirationDate.text = ObtenerFecha(worker.FECHA_VENCIMIENTO ?: "")
        binding.includebackq.txtNroLicencia.text = worker.NROLICENCIA
        binding.includebackq.txtClaseCategoria.text = worker.CLASE_CATEGORIA
        binding.includebackq.txtFechaRevalidacion.text =
            ObtenerFecha(worker.FECHA_REVALIDACION ?: "")
        binding.includebackq.txtLentes.text = worker.RESTRICCIONES
        binding.includefrontq.txtBloodSign.text = signo
        binding.includefrontq.txtBloodType.text = tipo
    }

    private fun flipCard() {
        if (isValid) {
            isFront = if (isFront) {
                frontAnim.setTarget(binding.includefrontq.frontQuellaveco)
                backAnim.setTarget(binding.includebackq.backQuellaveco)
                frontAnim.start()
                backAnim.start()
                false
            } else {
                frontAnim.setTarget(binding.includebackq.backQuellaveco)
                backAnim.setTarget(binding.includefrontq.frontQuellaveco)
                backAnim.start()
                frontAnim.start()
                true
            }
        }
    }

    private fun loadData(authorizedVehicleDTO: List<AuthorizedVehicleDTO>) {
        val data: MutableList<VehicleCategory> = ArrayList()
        for (item in authorizedVehicleDTO) {
            val category: VehicleCategory
            val types = authorizedVehicleDTO.filter { it.CATEGORIA == item.CATEGORIA }
            if (!data.contains(
                    VehicleCategory(
                        name = item.CATEGORIA,
                        vehicleList = types.map { VehicleType(it.TIPOVEHICULO) })
                )
            ) {
                category = VehicleCategory(
                    name = item.CATEGORIA,
                    vehicleList = types.map { VehicleType(it.TIPOVEHICULO) })
                data.add(category)
            }
        }
        for (i in 0 until data.size) {
            listAuthorizedVehicle.add(data[i])
        }
        binding.includebackq.rvAuthorizedVehicles.layoutManager =
            GridLayoutManager(requireContext(), 2)
        vehiculoAdapter = VehiculoAdapter(listAuthorizedVehicle, requireContext())
        binding.includebackq.rvAuthorizedVehicles.adapter = vehiculoAdapter
    }

    private fun ObtenerFecha(date: String): String {
        val arregloFecha: MutableList<String> = ArrayList()
        var fechaFormateada = ""

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


