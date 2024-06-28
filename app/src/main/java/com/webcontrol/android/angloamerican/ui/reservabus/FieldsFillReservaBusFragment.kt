package com.webcontrol.android.angloamerican.ui.reservabus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.angloamerican.ui.DatePickerFragment
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.WorkerRequest
import com.webcontrol.android.databinding.FieldsFillReservaBusBinding
import com.webcontrol.android.util.*
import com.webcontrol.android.util.PreferenceUtil.setStringPreference
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FieldsFillReservaBusFragment : Fragment() {

    private val viewModel by viewModels<FieldsFillReservaBusViewModel>()
    private var worker: WorkerAnglo? = null
    private lateinit var binding: FieldsFillReservaBusBinding
    private var typeTrip: TypeReservaBus? = null
    private lateinit var listSources: List<SourceReservaBus>
    private lateinit var listDestinies: List<DestinyReservaBus>
    private lateinit var listDivitions: List<Division>
    private lateinit var listParametro: List<ParametroViaje>
    private var source: String = ""
    private var destiny: String = ""
    private var divition: String = ""
    private var dateReturn: String = ""
    private var dateDeparture: String = ""
    private var reserva: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reserva = requireArguments().getString("Reserva_bus")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FieldsFillReservaBusBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.fields_fill_reserva_bus, container, false)
        binding.etDepartureDate.setOnClickListener { showDateDeparturePickerDialog() }
        binding.etReturnDate.setOnClickListener { showDateReturnPickerDialog() }
        initializeTypeTrip()

        binding.actTypeTrip.setOnItemClickListener { adapter, _, position, _ ->
            val item = adapter.getItemAtPosition(position) as String
            if (item == TypeReservaBus.IDA.valor) {
                typeTrip = TypeReservaBus.IDA
                binding.tilDestiny.visibility = View.VISIBLE
                binding.tilSource.visibility = View.VISIBLE
                binding.tilDivition.visibility = View.VISIBLE
                binding.tilReturnDate.visibility = View.GONE
                binding.tilDepartureDate.visibility = View.VISIBLE

            } else if (item == TypeReservaBus.VUELTA.valor) {
                typeTrip = TypeReservaBus.VUELTA
                binding.tilDestiny.visibility = View.VISIBLE
                binding.tilSource.visibility = View.VISIBLE
                binding.tilDivition.visibility = View.VISIBLE
                binding.tilReturnDate.visibility = View.VISIBLE
                binding.tilDepartureDate.visibility = View.GONE
            } else {
                typeTrip = TypeReservaBus.IDAYVUELTA
                binding.tilDestiny.visibility = View.VISIBLE
                binding.tilSource.visibility = View.VISIBLE
                binding.tilDivition.visibility = View.VISIBLE
                binding.tilReturnDate.visibility = View.VISIBLE
                binding.tilDepartureDate.visibility = View.VISIBLE
            }

        }

        binding.actDestiny.setOnItemClickListener { parent, view, position, id ->
            try {
                listDestinies.find {
                    it.toString()  == parent.getItemAtPosition(position).toString()
                }.let {
                    if (it != null) {
                        destiny = it.id
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Ocurrio un problema al obtener destino seleccionado",
                    Toast.LENGTH_SHORT
                ).show()
            }

            //destiny = listDestinies[position].id

        }

        binding.actSource.setOnItemClickListener { parent, view, position, id ->
            try {
                listSources.find {
                    it.toString() == parent.getItemAtPosition(position).toString()
                }.let {
                    if (it != null) {
                        source = it.id
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Ocurrio un problema al obtener origen seleccionado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.actDivition.setOnItemClickListener { parent, view, position, id ->
            try {
                listDivitions.find {
                    it.toString()  == parent.getItemAtPosition(position).toString()
                }.let {
                    if (it != null) {
                        divition = it.id
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Ocurrio un problema al obtener division seleccionada",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.actSource.setAdapter(null)
            binding.actSource.setText("")
            binding.actDestiny.setAdapter(null)
            binding.actDestiny.setText("")
            ShowLoader()
            viewModel.getSources(divition)
            viewModel.getDestinies(divition)

        }
        binding.btnNext.setOnClickListener {
            pressButtonNext()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDatas()
        observeDivisionState()
        observeSourcesState()
        observeDestiniesState()
        observeWorkerInfo()
    }

    private fun loadDatas() {
        ShowLoader()
        viewModel.getDivitions()
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

    private fun showDateDeparturePickerDialog() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateDepartureSelected(day, month, year) }
        datePicker.show(getParentFragmentManager(), "datePicker1")
    }

    private fun showDateReturnPickerDialog() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateReturnSelected(day, month, year) }
        datePicker.show(getParentFragmentManager(), "datePicker2")
    }

    fun onDateDepartureSelected(day: Int, month: Int, year: Int) {
        val strDay = if (day > 9) "${day}" else "0${day}"
        val strMonth = if ((month + 1) > 9) "${month + 1}" else "0${month + 1}"
        binding.etDepartureDate!!.setText("${year}/${strMonth}/${strDay}")
        dateDeparture = "${year}${strMonth}${strDay}"
    }

    fun onDateReturnSelected(day: Int, month: Int, year: Int) {
        val strDay = if (day > 9) "${day}" else "0${day}"
        val strMonth = if ((month + 1) > 9) "${month + 1}" else "0${month + 1}"
        binding.etReturnDate!!.setText("${year}/${strMonth}/${strDay}")
        dateReturn = "${year}${strMonth}${strDay}"
    }


    fun validateFields(): String? {
        if (typeTrip == null)
            return "Seleccione un Tipo de Viaje."
        if (divition.isNullOrEmpty())
            return "Seleccione una Division."
        if (source.isNullOrEmpty())
            return "Seleccione un Origen."
        if (destiny.isNullOrEmpty())
            return "Seleccione un Destino."

        if (typeTrip == TypeReservaBus.IDA) {
            if (dateDeparture.isNullOrEmpty())
                return "Ingrese Fecha de Ida."
        } else if (typeTrip == TypeReservaBus.VUELTA) {
            if (dateReturn.isNullOrEmpty())
                return "Ingrese Fecha de Vuelta."
        } else if (typeTrip == TypeReservaBus.IDAYVUELTA) {
            if (dateDeparture.isNullOrEmpty())
                return "Ingrese Fecha de Ida."
            if (dateReturn.isNullOrEmpty())
                return "Ingrese Fecha de Vuelta."
            if (dateReturn < dateDeparture)
                return "La fecha de Ida no puede ser mayor al de Regreso."
        } else {
        }
        return null
    }

    fun cleanPreferencesBus() {
        SharedUtils.setBusIda(context, -1L)
        SharedUtils.setBusVuelta(context, -1L)
        SharedUtils.setBusAsientoVuelta(context, -1)
        SharedUtils.setBusAsientoIda(context, -1)
    }


    fun savePreferencesFilterBus() {
        setStringPreference(context, PreferenceUtil.PREF_RBUS_DESTINO, destiny)
        setStringPreference(context, PreferenceUtil.PREF_RBUS_ORIGEN, source)
        setStringPreference(context, PreferenceUtil.PREF_RBUS_DIVSION, divition)

        if (typeTrip == TypeReservaBus.VUELTA) {
            setStringPreference(context, PreferenceUtil.PREF_RBUS_FECHA_IDA, "")
            setStringPreference(context, PreferenceUtil.PREF_RBUS_FECHA_VUELTA, dateReturn)
        } else if (typeTrip == TypeReservaBus.IDA) {
            setStringPreference(context, PreferenceUtil.PREF_RBUS_FECHA_VUELTA, "")
            setStringPreference(context, PreferenceUtil.PREF_RBUS_FECHA_IDA, dateDeparture)
        } else {
            setStringPreference(context, PreferenceUtil.PREF_RBUS_FECHA_VUELTA, dateReturn)
            setStringPreference(context, PreferenceUtil.PREF_RBUS_FECHA_IDA, dateDeparture)
        }
    }

    fun pressButtonNext() {
        var message = validateFields()
        if (message == null) {
            val request = WorkerRequest(
                workerId = SharedUtils.getUsuarioId(requireContext()),
                divisionId = divition

            )
            ShowLoader()
            viewModel.getWorkerInfo(request)

        } else {
            MaterialDialog.Builder(requireContext())
                .title("Mensaje")
                .content(message)
                .positiveText("Aceptar")
                .autoDismiss(true)
                .show()
        }


    }

    fun initializeTypeTrip() {
        val arrayAdapter: ArrayAdapter<*>
        val adapterList = listOf(
            TypeReservaBus.IDA.valor,
            TypeReservaBus.IDAYVUELTA.valor
        )
        arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item, adapterList
        )
        binding.actTypeTrip.setAdapter(arrayAdapter)
    }

    private fun observeDivisionState() {
        viewModel.divitionState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    listDivitions = it.data ?: ArrayList()
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.dropdown_menu_popup_item,
                        listDivitions
                    )
                    binding.actDivition!!.setAdapter(adapter)
                    DismissLoader()

                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    DismissLoader()
                }
            }
        }
    }

    private fun observeSourcesState() {
        viewModel.sourceState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    listSources = it.data ?: ArrayList()
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.dropdown_menu_popup_item,
                        listSources
                    )
                    binding.actSource!!.setAdapter(adapter)
                    DismissLoader()

                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    DismissLoader()
                }
            }
        }
    }

    private fun observeDestiniesState() {
        viewModel.destinyState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    listDestinies = it.data ?: ArrayList()
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.dropdown_menu_popup_item,
                        listDestinies
                    )
                    binding.actDestiny!!.setAdapter(adapter)
                    DismissLoader()

                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    DismissLoader()
                }
            }
        }
    }

    private fun observeWorkerInfo() {
        viewModel.workerInfoState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    if (it.data != null && it.data!!.autor && it.data!!.validated) {
                        SharedUtils.setUsuarioId(context, it.data!!.id)
                        SharedUtils.setIdCompany(context, it.data!!.companiaId)
                        SharedUtils.setOSTCheckList(context, it.data!!.ost)
                        cleanPreferencesBus()
                        savePreferencesFilterBus()
                        findNavController().navigate(
                            R.id.selectReservaBusFragment,
                            bundleOf(TITLE to "Reserva Bus", TRIP_ID to typeTrip?.valor)
                        )
                        DismissLoader()
                    } else {
                        MaterialDialog.Builder(requireContext())
                            .title("Alerta")
                            .content("No puede realizar reservas! Su usuario no se registra como acreditado para esta Division.")
                            .positiveText("Aceptar")
                            .autoDismiss(true)
                            .show()
                        DismissLoader()
                    }

                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    DismissLoader()
                }
            }
        }

    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        const val TITLE = "title"
        const val TRIP_ID = "trip_id"

        @JvmStatic
        fun newInstance(name: String?): FieldsFillReservaBusFragment {
            val fragment = FieldsFillReservaBusFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, name)
            fragment.arguments = args
            return fragment
        }
    }
}