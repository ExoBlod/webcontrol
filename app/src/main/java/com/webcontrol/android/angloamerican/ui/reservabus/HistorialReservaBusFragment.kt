package com.webcontrol.android.angloamerican.ui.reservabus

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.webcontrol.android.R
import com.webcontrol.android.common.GenericAdapter
import com.webcontrol.android.databinding.HistorialReservaBusBinding
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.angloamerican.common.TypeFactoryHistBus
import com.webcontrol.angloamerican.data.db.entity.ReservaBus2
import com.webcontrol.core.common.model.Resource
import android.os.Handler
import android.os.Looper
import androidx.core.view.isInvisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistorialReservaBusFragment : Fragment() {

    private val viewModel by viewModels<HistorialReservaBusViewModel>()
    lateinit var listHistory: List<ReservaBus2>
    private lateinit var binding: HistorialReservaBusBinding
    var animation: LayoutAnimationController? = null
    var valor: String = "Reserva bus"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = valor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HistorialReservaBusBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.historial_reserva_bus, container, false)
        animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.slide_out)
        binding.loaderReservas.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark
        )
        binding.loaderReservas.setOnRefreshListener {
            binding.rcvHistoricoReserva8.layoutAnimation = animation
            syncData()
        }

        observeHistoryState()
        observeListReservesState()
        initRecyclerView()
        binding.rcvHistoricoReserva8.layoutAnimation = animation
        syncData()
        binding.btnNuevo.setOnClickListener {
            nuevo()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMenuProvider()
    }

    private fun setMenuProvider() {
        val menuHost = requireActivity() as MenuHost

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        })
    }

    private fun syncData() {
        binding.loaderReservas.isRefreshing = true
        if (SharedUtils.isOnline(requireContext())) {
            viewModel.getHistoryReserves(SharedUtils.getUsuarioId(context))
        } else {
            binding.loaderReservas.isRefreshing = false
            loadDatas()
        }
    }

    private fun loadDatas() {
        viewModel.getListReserves(SharedUtils.getUsuarioId(context))
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.rcvHistoricoReserva8.layoutManager = layoutManager
        val adapter = GenericAdapter(TypeFactoryHistBus())
        binding.rcvHistoricoReserva8.adapter = adapter
    }


    private fun observeHistoryState() {
        viewModel.historyState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    listHistory = it.data ?: ArrayList()
                    if (listHistory.isNotEmpty())
                        syncData()
                    else
                        loadDatas()
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeListReservesState() {
        viewModel.listReserveState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    listHistory = it.data ?: ArrayList()
                    if (listHistory.size > 0) {
                        binding.svReservaBus.visibility = View.GONE
                        binding.rcvHistoricoReserva8.visibility = View.VISIBLE
                        val adapter = GenericAdapter(TypeFactoryHistBus())
                        adapter.setOnClickListener { item ->
                            findNavController().navigate(
                                R.id.DetalleReservaFragment,
                                bundleOf("item" to item)
                            )
                        }
                        binding.rcvHistoricoReserva8.adapter = adapter
                        adapter.setItems(listHistory)


                    } else {
                        binding.svReservaBus.visibility = View.VISIBLE
                        binding.rcvHistoricoReserva8.visibility = View.GONE
                    }
                    binding.loaderReservas.isRefreshing = false
                }

                is Resource.Error -> {
                    binding.rcvHistoricoReserva8.visibility = View.GONE
                    binding.svReservaBus.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    fun nuevo() {
        val handler = Handler(Looper.getMainLooper())
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_message)
        dialog.window?.attributes?.gravity = Gravity.TOP
        dialog.window?.setWindowAnimations(R.style.CustomDialogAnimation)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.show()

        var message = dialog.findViewById<TextView>(R.id.textMessage)
        message.text = "Estimado Usuario,recuerde realizar sus reservas como minimo el dia anterior a su viaje"
        var btnCancel = dialog.findViewById<ImageButton>(R.id.btnCanceled)
        btnCancel.visibility=View.VISIBLE
        var line = dialog.findViewById<View>(R.id.divider9)
        line.visibility=View.VISIBLE
        btnCancel.setOnClickListener {
            dialog.dismiss()
            handler.removeCallbacksAndMessages(null)
            findNavController().navigate(
                R.id.fieldsFillReservaBusFragment,
                bundleOf("Reserva_bus" to "Reserva Bus")
            )
        }
        // Programar una tarea para cerrar automáticamente el diálogo después de 4 segundos
        handler.postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
                // Navegar a la otra vista
                findNavController().navigate(
                    R.id.fieldsFillReservaBusFragment,
                    bundleOf("Reserva_bus" to "Reserva Bus")
                )
            }
        }, 4000)

    }

    companion object {
        const val PARAM_NOMBRE_ENCUESTA = "nombre_encuesta"

        private var nombreEncuesta = PARAM_NOMBRE_ENCUESTA

        @JvmStatic
        fun newInstance(name: String?): HistorialReservaBusFragment {
            val fragment = HistorialReservaBusFragment()
            val args = Bundle()
            args.putString(nombreEncuesta, name)
            nombreEncuesta = name!!
            fragment.arguments = args
            return fragment
        }
    }
}