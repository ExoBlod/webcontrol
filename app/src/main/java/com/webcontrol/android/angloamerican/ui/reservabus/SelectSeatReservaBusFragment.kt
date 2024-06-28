package com.webcontrol.android.angloamerican.ui.reservabus

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
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
import com.webcontrol.android.data.model.RequestReservaBus
import com.webcontrol.android.databinding.FragmentAttendanceBinding
import com.webcontrol.android.databinding.FragmentSeatReservaBusBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.PreferenceUtil
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_DESTINO
import com.webcontrol.android.util.PreferenceUtil.PREF_RBUS_ORIGEN
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.getBusAsientoIda
import com.webcontrol.android.util.SharedUtils.getBusAsientoVuelta
import com.webcontrol.android.util.SharedUtils.getBusIda
import com.webcontrol.android.util.SharedUtils.getBusPatenteIda
import com.webcontrol.android.util.SharedUtils.getBusPatenteVuelta
import com.webcontrol.android.util.SharedUtils.getBusVuelta
import com.webcontrol.android.util.SharedUtils.setBusAsientoIda
import com.webcontrol.android.util.SharedUtils.setBusAsientoVuelta
import com.webcontrol.android.util.TypeReservaBus
import com.webcontrol.angloamerican.data.ResponseSeatBus
import com.webcontrol.angloamerican.utils.StateSeatBus
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectSeatReservaBusFragment : Fragment() {
    private val viewModel by viewModels<SelectSeatReservaBusViewModel>()
    private var seatSelected: ResponseSeatBus? = null

    private var typeTrip: TypeReservaBus? = null
    private var imgPrev: ImageView? = null

    lateinit var listSeats: MutableList<ResponseSeatBus>
    private lateinit var binding:FragmentSeatReservaBusBinding
    private var title: String? = null
    private var tripId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = requireArguments().getString(SelectReservaBusFragment.TITLE)
        tripId = requireArguments().getString(SelectReservaBusFragment.TRIP_ID)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeatReservaBusBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.fragment_seat_reserva_bus, container, false)
        if (tripId == null) {
            throw IllegalArgumentException("tripId is null")
        }
        typeTrip = SharedUtils.getTypeReservaBusFromString(
            this.requireArguments().getString(SelectReservaBusFragment.TRIP_ID)!!
        )
        binding.btnNext.setOnClickListener {
            reservar()
        }

        return binding.root
    }
    fun reservar() {
        if (seatSelected == null) {
            MaterialDialog.Builder(requireContext())
                .title(getString(R.string.message))
                .content(getString(R.string.did_not_select_any_seat))
                .positiveText(getString(R.string.buttonOk))
                .autoDismiss(true)
                .show()
        } else {
            if (typeTrip == TypeReservaBus.IDAYVUELTA) {
                setBusAsientoIda(context, seatSelected!!.idSeat)

                val tmpOrigen = PreferenceUtil.getStringPreference(context, PREF_RBUS_ORIGEN)
                val tmpDestino = PreferenceUtil.getStringPreference(context, PREF_RBUS_DESTINO)
                PreferenceUtil.setStringPreference(context, PREF_RBUS_ORIGEN, tmpDestino)
                PreferenceUtil.setStringPreference(context, PREF_RBUS_DESTINO, tmpOrigen)

                findNavController().navigate(R.id.selectReservaBusFragment,
                    bundleOf(SelectReservaBusFragment.TITLE to title ,SelectReservaBusFragment.TRIP_ID to TypeReservaBus.VUELTA.valor)
                )
            } else {
                if (typeTrip == TypeReservaBus.IDA)
                    setBusAsientoIda(context, seatSelected!!.idSeat)
                if (typeTrip == TypeReservaBus.VUELTA)
                    setBusAsientoVuelta(context, seatSelected!!.idSeat)

                val busIda = getBusIda(context)
                val busVuelta = getBusVuelta(context)
                val asientoIda = getBusAsientoIda(context)
                val asientoVuelta = getBusAsientoVuelta(context)

                ShowLoader()
                viewModel.makeReservationBus(
                    RequestReservaBus(
                        asientoIda = if (asientoIda > 0) asientoIda else null,
                        asientoVuelta = if (asientoVuelta > 0) asientoVuelta else null,
                        idProgIda = if (busIda > 0) busIda else null,
                        idProgVuelta = if (busVuelta > 0) busVuelta else null,
                        rut = SharedUtils.getUsuarioId(context),
                        empresa = SharedUtils.getIdCompany(context),
                        ost = SharedUtils.getOSTCheckList(context)
                    )
                )
            }
        }
    }

    fun showMessageErrorReserve(message: String) {
        MaterialDialog.Builder(requireContext())
            .title(getString(R.string.alert_title))
            .content(message)
            .positiveText(getString(R.string.buttonOk))
            .autoDismiss(true)
            .onPositive { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    fun showMessageSuccessReserve(message: String) {
        MaterialDialog.Builder(requireContext())
            .title(getString(R.string.alert_title))
            .content(message)
            .positiveText(getString(R.string.buttonOk))
            .autoDismiss(true)
            .onPositive { dialog, which ->
            }
            .show()
    }


    private fun observeReserveBusState() {
        viewModel.reserveState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    ShowLoader()
                }
                is Resource.Success -> {
                    val message =
                        it!!.data!![0].message ?: getString(R.string.error_occurred_the_reserve)
                    if (it!!.data!![0].status == "OK") {

                        showMessageSuccessReserve(message)
                        DismissLoader()
                        findNavController().navigate(
                            R.id.historialReservaBusFragment, bundleOf("historial" to "historial")
                        )
                    } else {
                        showMessageErrorReserve(message)
                        DismissLoader()
                    }

                }
                is Resource.Error -> {
                    showMessageErrorReserve(getString(R.string.error_occurred_the_reserve))
                    DismissLoader()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeid.linearLayout1.removeAllViews()
        loadDatas()
        observeSeatsState()
        observeReserveBusState()
    }

    private fun loadDatas() {
        ShowLoader()
        var patente = ""
        var idProg = -1L
        if (typeTrip == TypeReservaBus.IDA || typeTrip == TypeReservaBus.IDAYVUELTA) {
            patente = getBusPatenteIda(context)
            idProg = getBusIda(context)
        } else {
            patente = getBusPatenteVuelta(context)
            idProg = getBusVuelta(context)
        }
        Log.d("buses",patente + " / " + idProg + " / " + typeTrip)
        viewModel.getSeatsBus(
            RequestReservaBus(
                idProg = idProg,
                patente = patente
            )
        )
    }

    private fun initSeats() {
        ShowLoader()
        var size = listSeats.size
        if (size < 4)
            return
        size += (size % 4)

        val row = size / 4
        val col = 2

        val layoutParamsRow = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1.0f
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val layoutParamsCol = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1.0f
            gravity = Gravity.CENTER
        }

        val textParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
            marginStart = 10
            marginEnd = 10
        }

        var key = 1
        val itemsForCol = 2
        var color = true
        var indexSeat = 0
        for (i in 1..row) {
            val rowLinear = LinearLayout(requireContext())
            rowLinear.setOrientation(LinearLayout.HORIZONTAL)
            rowLinear.layoutParams = layoutParamsRow
            var showRow = true
            for (k in 1..col) {
                val colLinear = LinearLayout(requireContext())
                colLinear.setOrientation(LinearLayout.HORIZONTAL)
                colLinear.layoutParams = layoutParamsCol
                if (showRow) {
                    val textView = TextView(requireContext())
                    if (i < 10)
                        textView.setText(" $i")
                    else
                        textView.setText("$i")
                    textView.setTextSize(18f)
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.layoutParams = textParams
                    colLinear.addView(textView)
                    showRow = false
                }


                for (j in 1..itemsForCol) {
                    val imageView = ImageView(requireContext())
                    imageView.layoutParams = LinearLayout.LayoutParams(100, 100)

                    if (listSeats[indexSeat].stateSeat == StateSeatBus.FREE) {
                        imageView.setImageResource(R.drawable.ic_seat_free)
                    } else if (listSeats[indexSeat].stateSeat == StateSeatBus.RESERVED) {
                        imageView.setImageResource(R.drawable.ic_seat_selected)
                    } else {
                        imageView.setImageResource(R.drawable.ic_seat_busy)
                    }

                    imageView.setId(listSeats.get(indexSeat).idSeat)
                    indexSeat++;
                    imageView.setOnClickListener {
                        setSeatSelected(imageView.getId(), imageView)
                    }
                    colLinear.addView(imageView)
                    key += 1;
                }
                colLinear.gravity = Gravity.CENTER
                rowLinear.addView(colLinear)
            }
            binding.includeid.linearLayout1.addView(rowLinear)
        }

        DismissLoader()
    }


    private fun setSeatSelected(id: Int, imageView: ImageView) {
        val index = id - 1
        if (listSeats[index].stateSeat == StateSeatBus.RESERVED)
            return
        if (listSeats[index].stateSeat == StateSeatBus.BUSY) {
            Toast.makeText(requireContext(), getString(R.string.occupied_seat), Toast.LENGTH_SHORT)
                .show()
            return
        }
        binding.txtSeatReserved.text = getString(R.string.selected_seat, index + 1)

        listSeats[index].stateSeat = StateSeatBus.RESERVED

        imageView.setImageResource(R.drawable.ic_seat_selected)
        for (i in listSeats.indices) {
            if (index == i)
                continue
            if (listSeats[i].stateSeat == StateSeatBus.RESERVED) {
                listSeats[i].stateSeat = StateSeatBus.FREE
            }
        }
        if (imgPrev != null) {
            imgPrev!!.setImageResource(R.drawable.ic_seat_free)
        }
        imageView.setImageResource(R.drawable.ic_seat_selected)
        seatSelected = listSeats[index]
        imgPrev = imageView
    }


    private fun observeSeatsState() {

        viewModel.seatsState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    ShowLoader()
                }
                is Resource.Success -> {
                    listSeats = it.data ?: ArrayList()
                    normalizeSeats()
                    DismissLoader()
                    initSeats()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    DismissLoader()
                }
            }
        }
    }

    fun normalizeSeats() {
        val nroSeats = listSeats.size
        var totSeats = nroSeats
        if (totSeats % 4 != 0) {
            totSeats = ((totSeats / 4) + 1) * 4
        }
        for (index in (1..(totSeats - nroSeats))) {
            listSeats.add(
                ResponseSeatBus(
                    idSeat = nroSeats + index,
                    stateSeat = "BUSY"
                )
            )
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

        @JvmStatic
        fun newInstance(name: String?, typeTrip: String): SelectSeatReservaBusFragment {
            val fragment = SelectSeatReservaBusFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, name)
            args.putString(ARG_PARAM2, typeTrip)
            fragment.arguments = args

            return fragment
        }
    }
}