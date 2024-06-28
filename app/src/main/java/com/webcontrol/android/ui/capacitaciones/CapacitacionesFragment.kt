package com.webcontrol.android.ui.capacitaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Capacitaciones
import com.webcontrol.android.data.network.CourseRequest
import com.webcontrol.android.databinding.FragmentCapacitacionesBinding
import com.webcontrol.android.ui.common.adapters.CapacitacionesAdapter
import com.webcontrol.android.util.GlideApp
import com.webcontrol.android.util.LocalStorage
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.core.common.model.Resource
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.android.inject
import java.util.ArrayList

@AndroidEntryPoint
class CapacitacionesFragment : Fragment(), CapacitacionesAdapter.CapacitacionesAdapterListener {
    private lateinit var binding: FragmentCapacitacionesBinding
    private var capacitacionesAdapter: CapacitacionesAdapter? = null
    private var capacitacionesList: List<Capacitaciones>? = null
    var layoutManager: LinearLayoutManager? = null
    private var workerId: String = ""
    private var divisionId: String = ""
    private var client: String? = ""
    private val localStorage: LocalStorage by inject()
    private val viewModel: CapacitacionesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Capacitaciones"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCapacitacionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        capacitacionesList = ArrayList()
        layoutManager = LinearLayoutManager(context)
        binding.rvCapacitaciones.layoutManager = layoutManager
        checkArguments()
        observeCourse()
        load()

    }
    private fun checkArguments() {
        workerId = localStorage["WORKER_ID_QR"] ?: ""
        divisionId = localStorage["DIVISION_CP"] ?: ""
        client = localStorage["CLIENT"] ?: ""
    }

    private fun observeCourse() {
        viewModel.cursoListState.observe(viewLifecycleOwner) { courseData ->
            when (courseData) {
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Historial $workerId", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    if (courseData.data != null) {
                        if (courseData.data!!.success) {
                            val worker = courseData.data!!.data
                            if (worker.isNotEmpty()) {
                                binding.textName.text = "${worker[0].apellidos},${worker[0].nombres}"
                                binding.textDivision.text = "División: "+divisionId
                                binding.textEmpresa.text = "Empresa: ${worker[0].empresa}"
                                loadImg(worker[0].rut!!)
                                capacitacionesList = courseData.data!!.data
                                capacitacionesList?.let { listaRes ->
                                    val orderList = if(client == "Barrick") {
                                        ArrayList(listaRes)
                                            .filter { it.divCodigo!!.contains(divisionId) }
                                            .sortedBy { it.charla }
                                    }else ArrayList(listaRes)
                                    capacitacionesAdapter = CapacitacionesAdapter(
                                        requireContext(),
                                        orderList,
                                        this@CapacitacionesFragment
                                    )
                                    binding.rvCapacitaciones.adapter = capacitacionesAdapter
                                }
                            } else {
                                findNavController().navigateUp()
                                binding.textDivision.isVisible = false
                                binding.imagen.isVisible = false
                                binding.textEmpresa.isVisible= false
                                binding.textName.isVisible = false
                                Toast.makeText(requireContext(), "No existe data para \n este usuario $workerId", Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Toast.makeText(requireContext(), courseData.data!!.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), courseData.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun load() {
        SharedUtils.showLoader(context, "Cargando ...")
        val request = CourseRequest(
            workerId = workerId,
            divisionId = divisionId
        )
        if (client == "Barrick") viewModel.getCoursesList(request)
        else viewModel.getCoursesListEA(request.workerId,request.divisionId)
        SharedUtils.dismissLoader(requireContext())
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDivisions()
    }

    private fun loadImg(user: String) {
        var urlPhoto = "%suser/%s/foto"
        urlPhoto =
            String.format(
                urlPhoto, getString(R.string.ws_url_mensajeria),
                user
            )
        GlideApp.with(this)
            .load(urlPhoto)
            .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
            .error(R.drawable.ic_account_circle_materialgrey_240dp)
            .circleCrop()
            .fitCenter()
            .into(binding.imagen)
    }

    override fun onRowItemClick(capacitacion: Capacitaciones?) {
        findNavController().navigate(
            R.id.detalleCapacitacionesFragment,
            bundleOf("itemCourse" to capacitacion)
        )
    }

}