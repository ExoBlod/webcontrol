package com.webcontrol.android.ui.reservacurso
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.ReservaCurso
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentReservaCursoBinding
import com.webcontrol.android.ui.common.adapters.ReservaCursoAdapter
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

@AndroidEntryPoint
class ReservaCursoFragment: Fragment(), ReservaCursoAdapter.ReservaCursoListAdapterListener {
    private lateinit var binding: FragmentReservaCursoBinding
    private var reservaCursoAdapter: ReservaCursoAdapter? = null
    private var reservaCursoList: List<ReservaCurso>? = null
    var layoutManager: LinearLayoutManager? = null

    fun nuevo(){
        findNavController().navigate(R.id.historialReservaCursoFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title="Reserva Cursos"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentReservaCursoBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.fragment_reserva_curso, container, false)
        binding.loaderReservaCurso.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark)
        binding.loaderReservaCurso.setOnRefreshListener { load() }
        binding.btnRegresar3.setOnClickListener {
            nuevo()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reservaCursoList = ArrayList()
        layoutManager = LinearLayoutManager(context)
        binding.rcvReservaCurso.layoutManager = layoutManager
        load()
    }

    fun load(){
        SharedUtils.showLoader(context, "Cargando ...")
        binding.loaderReservaCurso.isRefreshing = true
        val call: Call<ApiResponseAnglo<List<ReservaCurso>>>

        val api = RestClient.buildAnglo()
        call = api.getCourseList()
        call.enqueue(object : Callback<ApiResponseAnglo<List<ReservaCurso>>> {
            override fun onResponse(call: Call<ApiResponseAnglo<List<ReservaCurso>>>, response: Response<ApiResponseAnglo<List<ReservaCurso>>>) {

                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        reservaCursoList = response.body()?.data
                        reservaCursoList?.let { listaRes ->
                            val orderList = ArrayList(listaRes)
                                .filter{ it.divisions?.contains("QUELLAVECO") == true }
                                .sortedWith(compareBy<ReservaCurso> { it.dateCourse }
                                    .thenBy { it.timeCourse })
                            reservaCursoAdapter = ReservaCursoAdapter(requireContext(), orderList, this@ReservaCursoFragment)
                            binding.rcvReservaCurso.adapter = reservaCursoAdapter
                        }
                    }
                }
                binding.loaderReservaCurso.isRefreshing = false
                SharedUtils.dismissLoader(context)
            }

            override fun onFailure(call: Call<ApiResponseAnglo<List<ReservaCurso>>>, t: Throwable) {
                binding.loaderReservaCurso.isRefreshing = false
                SharedUtils.dismissLoader(context)
                Toast.makeText(requireContext(), "Error de red",Toast.LENGTH_SHORT).show()
            }
        })

    }

    companion object {

        @JvmStatic
        fun newInstance(): ReservaCursoFragment {
            val fragment = ReservaCursoFragment()
            return fragment
        }
    }


    override fun OnRowItemClick(reservaCurso: ReservaCurso?) {
        findNavController().navigate(R.id.detalleReservaCursoFragment,
            bundleOf("type" to 0,"itemReserva" to reservaCurso)
        )
    }

}