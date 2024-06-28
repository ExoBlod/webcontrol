package com.webcontrol.android.ui.checklist

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.CheckListTest
import com.webcontrol.android.databinding.FragmentHistoricoCheckListBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.common.adapters.HistoricoCheckListAdapter
import com.webcontrol.android.ui.common.adapters.HistoricoCheckListAdapter.HistoricoCheckListAdapterListener
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.HistoricoCheckListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoricoCheckListFragment : Fragment(), HistoricoCheckListAdapterListener, IOnBackPressed {
    private lateinit var binding: FragmentHistoricoCheckListBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var historicoCheckListAdapter: HistoricoCheckListAdapter? = null
    private var listCheckList: List<CheckListTest>? = null
    val historicoCheckListViewModel: HistoricoCheckListViewModel by viewModels()
    private lateinit var tipoCheckList: String
    private var nomCheckList: String? = null
    private var idChecklist: String = "0"
    private var idCompany: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tipoCheckList = requireArguments().getString(CHECKLIST_TYPE) ?: ""
        nomCheckList = requireArguments().getString(CHECKLIST_NAME)
        idChecklist = requireArguments().getString(CHECKLIST_ID) ?: "0"
        idCompany = requireArguments().getString(COMPANY_ID)
        requireActivity().title = nomCheckList
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoricoCheckListBinding.inflate(inflater, container, false)

        SharedUtils.setIdCompany(context, idCompany)
        if (nomCheckList == null) binding.lblSubTitulo.text =
            "Historico " + CHECKLIST_NAME!!.toLowerCase()
        else binding.lblSubTitulo.text = "Historico " + nomCheckList!!.toLowerCase()

        historicoCheckListViewModel.getCheckListResult()
            .observe(viewLifecycleOwner, Observer { checkListTests ->
                listCheckList = checkListTests
                if (listCheckList!!.isNotEmpty()) {
                    binding.emptyState.visibility = View.GONE
                    binding.rcvHistoricoFatigaSomnolencia.visibility = View.VISIBLE
                    historicoCheckListAdapter = HistoricoCheckListAdapter(
                        requireContext(),
                        listCheckList!!,
                        this@HistoricoCheckListFragment
                    )
                    binding.rcvHistoricoFatigaSomnolencia.adapter = historicoCheckListAdapter
                } else {
                    binding.rcvHistoricoFatigaSomnolencia.visibility = View.GONE
                    binding.emptyState.visibility = View.VISIBLE
                }
                DismissLoader()
            })

        historicoCheckListViewModel.getCheckListError().observe(viewLifecycleOwner, Observer {
            binding.rcvHistoricoFatigaSomnolencia.visibility = View.GONE
            binding.emptyState.visibility = View.VISIBLE
            DismissLoader()
        })

        historicoCheckListViewModel.workerObservableResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                if (result.isSuccess) {
                    if (result.isSuccess) {
                        if (result.data.autor) {
                            findNavController().navigate(
                                R.id.checkListIngresoFragment, bundleOf(
                                    "TipoCheckList" to tipoCheckList,
                                    "NomCheckList" to nomCheckList, "idChecklist" to idChecklist
                                )
                            )
                        } else {
                            showError()
                        }
                    } else {
                        showError()
                    }
                }
                DismissLoader()
            })

        historicoCheckListViewModel.workerObservableError.observe(viewLifecycleOwner, Observer {
            DismissLoader()
            Snackbar.make(binding.rcvHistoricoFatigaSomnolencia, "Error de red", Snackbar.LENGTH_LONG)
                .setAction("Reintentar") { nuevo() }.show()
        })

        historicoCheckListViewModel.cdlResult.observe(viewLifecycleOwner, Observer {
            DismissLoader()
            if (it != null)
                ShowCdlDialog(it)
        })

        binding.btnNuevo.setOnClickListener{
            nuevo()
        }

        initRecyclerView()
        loadData()
        return binding.root
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))
        binding.rcvHistoricoFatigaSomnolencia.addItemDecoration(divider)
        binding.rcvHistoricoFatigaSomnolencia.layoutManager = layoutManager
    }

    private fun loadData() {
        ShowLoader()
        historicoCheckListViewModel.getCheckList(tipoCheckList, idCompany)
    }

    fun nuevo() {
        val date = SharedUtils.wCDate
        if (tipoCheckList == "COV") {
            ShowLoader()
            historicoCheckListViewModel.ValidateSurveyTime(requireContext(), idCompany)
        } else if (tipoCheckList == "DDS") {
            getLocation()
        } else {
            if (SharedUtils.getTmzNow(requireContext()) == date && tipoCheckList == "TMZ") {
                SharedUtils.showToast(requireContext(), "Permitido solo un ingreso al dia.")
            } else {
                findNavController().navigate(
                    R.id.tipoChecklistFragment, bundleOf(
                        TipoChecklistFragment.CHECKLIST_TYPE to tipoCheckList,
                        TipoChecklistFragment.CHECKLIST_NAME to nomCheckList
                    )
                )
            }
        }
    }

    private fun ShowCdlDialog(flag: Int) {
        var message: String
        var title: String
        when (flag) {
            1 -> {
                message = if (tipoCheckList == "DDS") {
                    "Está por llenar la encuesta diaria de síntomas. Por favor verifique que" +
                            " la información llenada sea correcta. Si marcó algun item de forma incorrecta informe a su supervisor."
                } else {
                    if (idCompany == Companies.BR.valor || idCompany == Companies.GF.valor) {
                        "Está por llenar la encuesta de Covid-19 obligatoria. Por favor verifique que" +
                                " la información llenada sea correcta. Si marcó algun item de forma incorrecta informe a su supervisor."
                    } else {
                        "Está por llenar la encuesta diaria de Covid-19 obligatoria. Por favor verifique que" +
                                " la información llenada sea correcta. Si marcó algun item de forma incorrecta informe a su supervisor."
                    }
                }

                title = "Advertencia"
            }

            2 -> {
                message = "Ud. Ya cuenta con una encuesta llena para el día de Hoy"
                title = "Solo se permite una encuesta por día"
            }

            3 -> {
                message =
                    "Estimado(a), desde las 00:00 hasta las 00:45 la encuesta se encuentra deshabilitada" +
                            " por favor intente más tarde. \nGracias."
                title = "Encuesta Deshabilitada"
            }

            4 -> {
                message =
                    "Ud. No Puede rendir la encuesta COVID, porque ya tiene una con observaciones." +
                            "\nPor lo tanto sigue bloqueado por 14 días"
                title = "Encuesta Deshabilitada"
            }

            else -> {
                message = "Ha ocurrido un error, por favor intente más tarde. \nGracias."
                title = "Error"
            }

        }
        MaterialDialog.Builder(requireContext())
            .title(title)
            .content(message)
            .positiveText("ACEPTAR")
            .negativeText("CANCELAR")
            .autoDismiss(true)
            .onPositive { dialog, which ->
                if (flag == 1)
                    findNavController().navigate(
                        R.id.tipoChecklistFragment, bundleOf(
                            TipoChecklistFragment.CHECKLIST_TYPE to tipoCheckList,
                            TipoChecklistFragment.CHECKLIST_NAME to nomCheckList
                        )
                    )
            }
            .onNegative { dialog, which -> dialog.dismiss() }
            .show()
    }

    override fun OnRowItemClick(checkListTest: CheckListTest?) {
        if (tipoCheckList != "COV")
            findNavController().navigate(
                R.id.historicoCheckListDetalleFragment, bundleOf(
                    HistoricoCheckListDetalleFragment.CHECKLIST_TYPE to tipoCheckList,
                    HistoricoCheckListDetalleFragment.CHECKLIST_NAME to checkListTest!!.titulo,
                    HistoricoCheckListDetalleFragment.CHECKLIST_ID to "0${checkListTest.idDb}"
                )
            )
    }

    private fun ShowLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(
            context,
            R.raw.loaddinglottie,
            "Cargando...",
            0,
            500,
            200
        )
    }

    private fun DismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(context)
    }

    private fun showError() {
        MaterialDialog.Builder(requireContext())
            .title("Error")
            .content("Usted no es un usuario conductor.")
            .positiveText("Aceptar")
            .autoDismiss(true)
            .show()
    }

    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.confirmSessionAbandon()
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_LOCATION)
    private fun getLocation() {
        if (!EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            EasyPermissions.requestPermissions(
                this,
                "Se necesita permiso para acceder a la ubicación",
                REQUEST_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            return
        }
        ShowLoader()
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isSpeedRequired = false
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        criteria.powerRequirement = Criteria.POWER_LOW
        var latitude = ""
        var longitude = ""
        var locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                latitude = location!!.latitude.toString()
                longitude = location!!.longitude.toString()

                if (latitude.isNotEmpty() && latitude.isNotEmpty()) {
                    SharedUtils.setLocationCoordinates(requireContext(), latitude + "," + longitude)
                    historicoCheckListViewModel.ValidateWorkerSurvey(
                        requireContext(),
                        Companies.QV.valor
                    )
                    DismissLoader()
                } else {
                    Toast.makeText(requireContext(), "Sin acceso a ubicación", Toast.LENGTH_SHORT)
                    DismissLoader()
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }

        }

        val provider = locationManager.getBestProvider(criteria, true)
        if ((!checkGPS && !checkNetwork) && provider != null)
            Toast.makeText(
                requireContext(),
                "Sin Servicio de Localizacion Disponible",
                Toast.LENGTH_SHORT
            ).show()
        else {
            try {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (askGpsProvider()) {
                        locationManager.requestSingleUpdate(criteria, locationListener, null)
                    }
                }

            } catch (e: Exception) {
                Log.e("LocationError", e.message!!)
            }
        }
    }

    private fun askGpsProvider(): Boolean {
        if (!(activity as MainActivity).locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            SharedUtils.showDialog(
                requireContext(),
                getString(R.string.title_dialog_alert),
                getString(R.string.text_dialog_ask_enable_gps),
                getString(R.string.buttonActivate),
                { dialog, _ ->
                    dialog.dismiss()
                    DismissLoader()
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0)
                },
                getString(R.string.buttonNegative),
                { dialog, _ ->
                    dialog.dismiss()
                    DismissLoader()
                }
            )
            return false
        } else
            return true
    }

    companion object {
        const val CHECKLIST_TYPE = "tipo_encuesta"
        const val CHECKLIST_NAME = "nombre_encuesta"
        const val CHECKLIST_ID = "checklist_id"
        const val COMPANY_ID = "company_id"

        const val REQUEST_LOCATION = 1
    }
}