package com.webcontrol.android.angloamerican.ui.reservabus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.common.ReservaBusAdapter
import com.webcontrol.android.data.model.RequestReservaBus
import com.webcontrol.android.databinding.FragmentListReservaBusBinding
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_DESTINO
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_DIVSION
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_FECHA_IDA
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_FECHA_VUELTA
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_ORIGEN
import com.webcontrol.android.util.PreferenceUtil.getStringPreference
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.getTypeReservaBusFromString
import com.webcontrol.android.util.SharedUtils.setBusIda
import com.webcontrol.android.util.SharedUtils.setBusPatenteIda
import com.webcontrol.android.util.SharedUtils.setBusPatenteVuelta
import com.webcontrol.android.util.SharedUtils.setBusVuelta
import com.webcontrol.android.util.TypeReservaBus
import com.webcontrol.angloamerican.data.ResponseReservaBus
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectReservaBusFragment : Fragment(),
    ReservaBusAdapter.ReservaBusAdapterListener {
    private val viewModel by viewModels<SelectReservaBusViewModel>()
    private var itemSelected: ResponseReservaBus? = null
    lateinit var listBuses: List<ResponseReservaBus>
    private var title: String? = null
    private var tripId: String? = null

    private var typeTrip: TypeReservaBus? = null
    private lateinit var binding: FragmentListReservaBusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = requireArguments().getString(FieldsFillReservaBusFragment.TITLE)
        tripId = requireArguments().getString(FieldsFillReservaBusFragment.TRIP_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentListReservaBusBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.fragment_list_reserva_bus, container, false)
        binding.btnReservar.setOnClickListener {
            reservarBus()
        }
        if (tripId == null) {
            throw IllegalArgumentException("tripId is null")
        }
        typeTrip = getTypeReservaBusFromString(tripId!!)
        if (typeTrip == TypeReservaBus.VUELTA)
            binding.tvSubtitle.text = getString(R.string.select_bus_drop)
        else
            binding.tvSubtitle.text = getString(R.string.select_bus_pick_up)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemSelected = null

        loadDatas()
        observeBusesState()
        observeCheckExistReserve()
        initRecyclerView()

    }

    private fun loadDatas() {
        ShowLoader()
        var fechaReserva = ""
        var sentido = ""
        if (typeTrip == TypeReservaBus.VUELTA) {
            sentido = getString(R.string.down)
            fechaReserva = getStringPreference(context, PREF_RBUS_FECHA_VUELTA)
        } else {
            sentido = getString(R.string.up)
            fechaReserva = getStringPreference(context, PREF_RBUS_FECHA_IDA)
        }
        val request = RequestReservaBus(
            divisionId = getStringPreference(context, PREF_RBUS_DIVSION),
            source = getStringPreference(context, PREF_RBUS_ORIGEN),
            destiny = getStringPreference(context, PREF_RBUS_DESTINO),
            fecha = fechaReserva,
            typeTrip = sentido,
            rut = SharedUtils.getUsuarioId(context)
        )
        viewModel.getBusesAvailable(
            request
        )
    }

    fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.rvReservaBus.layoutManager = layoutManager
    }

    fun showMessageExistReserve() {
        MaterialDialog.Builder(requireContext())
            .title(getString(R.string.alert_title))
            .content(getString(R.string.there_is_reservation_for_bus))
            .positiveText(getString(R.string.buttonOk))
            .autoDismiss(true)
            .onPositive { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    fun reservarBus() {
        if (itemSelected == null) {
            MaterialDialog.Builder(requireContext())
                .title(getString(R.string.message))
                .content(getString(R.string.you_did_not_select_any_bus))
                .positiveText(getString(R.string.buttonOk))
                .autoDismiss(true)
                .show()
        } else {
            val seatDisponibles = itemSelected!!.disponible ?: 0
            if (seatDisponibles < 1) {
                MaterialDialog.Builder(requireContext())
                    .title(getString(R.string.message))
                    .content(getString(R.string.the_bus_no_longer_seats_available))
                    .positiveText(getString(R.string.buttonOk))
                    .autoDismiss(true)
                    .show()
            } else {
                ShowLoader()
                viewModel.checkIfExistReserve(
                    itemSelected!!.codeProg!!,
                    SharedUtils.getUsuarioId(context)
                )
            }

        }

    }

    fun toGoSelectSeatBus() {
        if (typeTrip == TypeReservaBus.IDAYVUELTA || typeTrip == TypeReservaBus.IDA) {
            setBusIda(context, itemSelected!!.codeProg!!)
            setBusPatenteIda(context, itemSelected!!.patente!!)
        } else {
            setBusVuelta(context, itemSelected!!.codeProg!!)
            setBusPatenteVuelta(context, itemSelected!!.patente!!)
        }
        findNavController().navigate(
            R.id.selectSeatReservaBusFragment, bundleOf(TITLE to "Select Reserva", TRIP_ID to typeTrip?.valor)
        )
    }

    private fun observeCheckExistReserve() {
        viewModel.existReserveState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    // show loader
                    ShowLoader()
                }
                is Resource.Success -> {
                    DismissLoader()
                    if (it.data == null) {
                        toGoSelectSeatBus()
                    } else {
                        showMessageExistReserve()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    DismissLoader()
                }
            }
        }
    }

    private fun observeBusesState() {
        viewModel.busesState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    // show loader
                    ShowLoader()
                }
                is Resource.Success -> {
                    binding.svReservaBus.visibility = View.GONE
                    binding.rvReservaBus.visibility = View.VISIBLE
                    listBuses = it.data ?: ArrayList()
                    if (listBuses.size > 0) {

                        listBuses.forEach { it -> it.setColorBus() }
                        val adapter = ReservaBusAdapter(
                            listBuses,
                            requireContext(),
                            this@SelectReservaBusFragment
                        )
                        binding.rvReservaBus.adapter = adapter
                        binding.tvTitle.visibility = View.VISIBLE
                        binding.tvSubtitle.visibility = View.VISIBLE
                        binding.btnReservar.visibility = View.VISIBLE
                    } else {
                        binding.rvReservaBus.visibility = View.GONE
                        binding.svReservaBus.visibility = View.VISIBLE
                        binding.tvTitle.visibility = View.GONE
                        binding.tvSubtitle.visibility = View.GONE
                        binding.tvEmptyReservas.text = "No hay buses para la fecha seleccionada"
                        binding.btnReservar.visibility = View.GONE
                    }
                    DismissLoader()
                }
                is Resource.Error -> {
                    binding.rvReservaBus.visibility = View.GONE
                    binding.svReservaBus.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    DismissLoader()
                }
            }
        }
    }


    private fun ShowLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(
            context,
            R.raw.loaddinglottie,
            getString(R.string.loading),
            0,
            500,
            200
        )
    }

    private fun DismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(context)
    }


    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        const val TITLE = "title"
        const val TRIP_ID = "trip_id"
        @JvmStatic
        fun newInstance(name: String?, typeTrip: String): SelectReservaBusFragment {
            val fragment = SelectReservaBusFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, name)
            args.putString(ARG_PARAM2, typeTrip)
            fragment.arguments = args
            return fragment
        }
    }

    override fun OnRowItemClick(reservaBus: ResponseReservaBus?) {
        itemSelected = reservaBus
    }
}