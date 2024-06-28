package com.webcontrol.android.ui.lectorqr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.android.R
import com.webcontrol.android.data.RestInterfaceKinross
import com.webcontrol.android.data.model.Competencia
import com.webcontrol.android.data.model.WorkerKinross
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCompetenciasBinding
import com.webcontrol.android.ui.competencias.CompetenciaListAdapter
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.ui.lectorqr.ResultQrKinrossFragment
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response

@AndroidEntryPoint
class ResultQrKinrossFragment : Fragment() {
    private lateinit var binding: FragmentCompetenciasBinding
    private var rut: String? = null

    companion object {
        private const val argValue = "value"

        fun newInstance(value: String?): ResultQrKinrossFragment {
            val args = Bundle()
            val fragment = ResultQrKinrossFragment()
            args.putString(argValue, value)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rut = this.requireArguments().getString(argValue)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompetenciasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvSkills.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSkills.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        loadWorker()
        binding.btnBackKinross.setOnClickListener {
            clearCompetencias()
            val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.nav_host_fragment_content_main, LectorQrFragment.newInstance("KINROSS", true))
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    private fun loadWorker() {
        val api: RestInterfaceKinross = RestClient.buildKinross()
        var workerKinross = WorkerKinross()
        workerKinross.workerId = rut!!
        val call = api.getWorker(workerKinross)
        call.enqueue(object : retrofit2.Callback<ApiResponseAnglo<WorkerKinross>> {
            override fun onFailure(call: Call<ApiResponseAnglo<WorkerKinross>>, t: Throwable) {
                SharedUtils.showToast(requireContext(), getString(R.string.error_getting_worker))
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<WorkerKinross>>,
                response: Response<ApiResponseAnglo<WorkerKinross>>
            ) {
                binding.lblRut.text = ""
                binding.lblNombre.text = getString(R.string.there_is_no_worker)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        workerKinross = response.body()!!.data
                        binding.lblRut.text = workerKinross.workerId
                        binding.lblNombre.text = workerKinross.name + " " + workerKinross.lastname
                        loadCompetencias()
                    }
                }
            }
        })
    }

    private fun loadCompetencias() {
        val api: RestInterfaceKinross = RestClient.buildKinross()
        val call = api.getWorkerCompetencias(rut!!)
        call.enqueue(object : retrofit2.Callback<ApiResponseAnglo<List<Competencia>>> {
            override fun onFailure(call: Call<ApiResponseAnglo<List<Competencia>>>, t: Throwable) {
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.error_getting_competencies)
                )
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<List<Competencia>>>,
                response: Response<ApiResponseAnglo<List<Competencia>>>
            ) {
                binding.lblCompetencias.text = getString(R.string.no_competencies)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        val competencias = response.body()!!.data
                        if (competencias.isNotEmpty()) {
                            binding.lblCompetencias.text = getString(R.string.competencies)
                            val adapter = CompetenciaListAdapter(competencias, requireContext())
                            binding.rvSkills.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }

    private fun clearCompetencias() {
        binding.lblCompetencias.text = ""
        binding.lblRut.text = ""
        binding.lblNombre.text = ""
        binding.rvSkills.adapter = null
    }

}