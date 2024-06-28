package com.webcontrol.android.ui.reservabus

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.ReservaBus
import com.webcontrol.android.databinding.FragmentHistoricoReservasBinding
import com.webcontrol.android.ui.common.adapters.ReservasBusListAdapter
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.HistoricoReservasBusViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoricoReservasFragment : Fragment(),
    ReservasBusListAdapter.ReservasBusListAdapterListener {
    private lateinit var binding: FragmentHistoricoReservasBinding
    private var listReservas: List<ReservaBus>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    var historicoReservasBusViewModel: HistoricoReservasBusViewModel? = null
    private var reservasBusListAdapter: ReservasBusListAdapter? = null
    var animation: LayoutAnimationController? = null

    private var NameTitulo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NameTitulo = requireArguments().getString(ARG_PARAM1)
        requireActivity().title = NameTitulo
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View{
        binding = FragmentHistoricoReservasBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.fragment_historico_reservas, container, false)
        historicoReservasBusViewModel =
                ViewModelProviders.of(this).get(HistoricoReservasBusViewModel::class.java)

        animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.slide_out)
        binding.loaderReservas.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark)
        binding.loaderReservas.setOnRefreshListener {
            syncData()
            binding.rcvHistoricoReserva.layoutAnimation = animation
        }

        historicoReservasBusViewModel!!.getReservasSyncResult()
                .observe(viewLifecycleOwner) { listReservasSync ->
                    if (listReservasSync.isNotEmpty())
                        syncData()
                    else
                        loadData()
                }

        historicoReservasBusViewModel!!.getReservasResult()
                .observe(viewLifecycleOwner) { listaReservas ->
                    listReservas = listaReservas
                    if (listReservas!!.isNotEmpty()) {
                        binding.emptyState.visibility = View.GONE
                        binding.rcvHistoricoReserva.visibility = View.VISIBLE
                        val lista: MutableList<ReservaBus> = mutableListOf()
                        listaReservas.forEach {
                            val reservaBus = ReservaBus(0, "", syncId = 0)
                            reservaBus.progId = it.progId
                            reservaBus.workerId = it.workerId
                            reservaBus.origenName = it.origenName
                            reservaBus.destinoName = it.destinoName
                            reservaBus.fecha = it.fecha
                            reservaBus.hora = it.hora
                            reservaBus.asiento = it.asiento
                            reservaBus.utilizo = it.utilizo
                            reservaBus.estado = it.estado
                            lista.add(reservaBus)
                        }

                        reservasBusListAdapter = ReservasBusListAdapter(
                            requireContext(),
                            lista,
                            this@HistoricoReservasFragment
                        )
                        binding.rcvHistoricoReserva.adapter = reservasBusListAdapter
                    } else {
                        binding.rcvHistoricoReserva.visibility = View.GONE
                        binding.emptyState.visibility = View.VISIBLE
                    }
                    binding.loaderReservas.isRefreshing = false
                }

        initRecyclerView()
        binding.rcvHistoricoReserva.layoutAnimation = animation
        syncData()
        return binding.root
    }

    private fun syncData() {
        binding.loaderReservas.isRefreshing = true
        if (SharedUtils.isOnline(requireContext())) {
            historicoReservasBusViewModel!!.getReservasListByRut(SharedUtils.getUsuarioId(requireContext()), SharedUtils.wCDate)
        } else {
            binding.loaderReservas.isRefreshing = false
            Snackbar.make(requireView(), "Error de red", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar") { syncData() }.show()
        }
    }

    private fun loadData() {
        historicoReservasBusViewModel!!.getReservasList(SharedUtils.wCDate)
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(context)
        binding.rcvHistoricoReserva.layoutManager = layoutManager
    }

    override fun OnRowItemClick(reservaBus: ReservaBus?) {
        val intent = Intent(context, ReservaBusDetalleActivity::class.java)
        intent.putExtra("PROG_ID", reservaBus!!.progId)
        intent.putExtra("WORKER_ID", reservaBus.workerId)
        startActivity(intent)
    }

    companion object {
        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(name: String?): HistoricoReservasFragment {
            val fragment = HistoricoReservasFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, name)
            fragment.arguments = args
            return fragment
        }
    }
}