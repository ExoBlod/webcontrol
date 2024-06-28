package com.webcontrol.android.ui.reservacurso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.ReservaCurso
import com.webcontrol.android.data.model.WorkerAnglo
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.common.adapters.HistoricoReservaCursoListAdapter
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.webcontrol.android.databinding.FragmentHistoricoCursosBinding
import com.webcontrol.android.ui.reservacurso.DetalleReservaCursoFragment.Companion.TYPE_COURSE_RESERVADO
import com.webcontrol.android.vm.HistoricoReservaCursoViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class HistorialReservaCursoFragment : Fragment(),
    HistoricoReservaCursoListAdapter.HistoricoReservaCursoListAdapterListener, IOnBackPressed {
    private lateinit var binding: FragmentHistoricoCursosBinding
    private var listReservas: List<ReservaCurso>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    val historicoReservaCursoViewModel: HistoricoReservaCursoViewModel by viewModels()
    private var reservaCursoListAdapter: HistoricoReservaCursoListAdapter? = null
    var animation: LayoutAnimationController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoricoCursosBinding.inflate(inflater, container, false)
        historicoReservaCursoViewModel.getReservasSyncResult().observe(viewLifecycleOwner) {
            historicoReservaCursoViewModel.getHistoryReservas()
        }


        historicoReservaCursoViewModel.getReservasResult()
            .observe(viewLifecycleOwner) { listaReservas ->
                listReservas = listaReservas
                if (listReservas!!.isNotEmpty()) {
                    binding.emptyStateResCurso.visibility = View.GONE
                    binding.rcvHistoricoReserva.visibility = View.VISIBLE
                    val lista: MutableList<ReservaCurso> = mutableListOf()
                    listaReservas.forEach {
                        val reservaCurso = ReservaCurso(0, 0, "")
                        reservaCurso.codeReserve = it.codeReserve
                        reservaCurso.codeCourse = it.codeCourse
                        reservaCurso.nameCourse = it.nameCourse
                        reservaCurso.dateReserve = it.dateReserve
                        reservaCurso.timeReserve = it.timeReserve
                        reservaCurso.dateCourse = it.dateCourse
                        reservaCurso.timeCourse = it.timeCourse
                        reservaCurso.duration = it.duration
                        reservaCurso.statusReserve = it.statusReserve
                        reservaCurso.place = it.place
                        reservaCurso.sala = it.sala
                        reservaCurso.required = it.required
                        reservaCurso.location = it.location
                        reservaCurso.orador = it.orador ?: ""
                        lista.add(reservaCurso)
                    }
                    reservaCursoListAdapter = HistoricoReservaCursoListAdapter(
                        requireContext(),
                        lista,
                        this@HistorialReservaCursoFragment
                    )
                    binding.rcvHistoricoReserva.adapter = reservaCursoListAdapter
                } else {
                    binding.rcvHistoricoReserva.visibility = View.GONE
                    binding.emptyStateResCurso.visibility = View.VISIBLE
                }
                DismissLoader()
            }

        historicoReservaCursoViewModel.getReservaError().observe(viewLifecycleOwner) {
            binding.rcvHistoricoReserva.visibility = View.GONE
            binding.emptyStateResCurso.visibility = View.VISIBLE
            DismissLoader()
        }
        initRecyclerView()
        binding.rcvHistoricoReserva.layoutAnimation = animation
        loadData()
        binding.btnNuevo.setOnClickListener {
            nuevo()
        }
        return binding.root
    }

    fun DismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(context)
    }

    fun nuevo() {
        ShowLoader()
        if (SharedUtils.isOnline(requireContext())) {
            try {
                val api = RestClient.buildAnglo()
                val call = api.getWorker(object : HashMap<String, String>() {
                    init {
                        put("WorkerId", SharedUtils.getUsuarioId(requireContext()))
                        put("DivisionId", "QV")
                    }
                })
                call.enqueue(object : Callback<ApiResponseAnglo<WorkerAnglo>> {
                    override fun onResponse(
                        call: Call<ApiResponseAnglo<WorkerAnglo>>,
                        response: Response<ApiResponseAnglo<WorkerAnglo>>
                    ) {
                        if (response.isSuccessful) {
                            val workerAngloApiResponseAnglo = response.body()
                            if (workerAngloApiResponseAnglo!!.isSuccess) {
                                if (workerAngloApiResponseAnglo.data != null || workerAngloApiResponseAnglo.data.autor){
                                    DismissLoader()
                                    findNavController().navigate(R.id.reservaCursoFragment)
                                } else {
                                    MaterialDialog.Builder(requireContext())
                                        .title(R.string.alert_title)
                                        .content(R.string.txt_negative_pass)
                                        .positiveText(R.string.buttonOk).autoDismiss(true)
                                        .onPositive { dialog, _ -> dialog.dismiss() }.show()
                                    DismissLoader()
                                }

                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiResponseAnglo<WorkerAnglo>>, t: Throwable
                    ) {
                        Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            } catch (ex: Exception) {
                Toast.makeText(requireContext(), R.string.txt_error, Toast.LENGTH_SHORT).show()
                DismissLoader()
            }
        } else {
            Snackbar.make(requireView(), R.string.network_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.app_update_retry) {  }.show()
            DismissLoader()
        }


    }


    private fun loadData() {
        ShowLoader()
        if (SharedUtils.isOnline(requireContext())) {
            historicoReservaCursoViewModel.getSyncReservasListByRut(
                "${
                    SharedUtils.getUsuarioId(
                        requireContext()
                    )
                }"
            )
        } else {
            historicoReservaCursoViewModel.getHistoryReservas()
        }
    }

    private fun ShowLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(
            context, R.raw.loaddinglottie, "Cargando...", 0, 500, 200
        )
    }


    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))
        binding.rcvHistoricoReserva.addItemDecoration(divider)
        binding.rcvHistoricoReserva.layoutManager = layoutManager
    }

    companion object {
        @JvmStatic
        fun newInstance(): HistorialReservaCursoFragment {
            val fragment = HistorialReservaCursoFragment()
            return fragment
        }
    }

    override fun OnRowItemClick(reservaCurso: ReservaCurso?) {
        findNavController().navigate(
            R.id.detalleReservaCursoFragment,
            bundleOf("type" to TYPE_COURSE_RESERVADO, "itemReserva" to reservaCurso)
        )
    }

    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.confirmSessionAbandon()
        return false
    }
}