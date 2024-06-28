package com.webcontrol.android.ui.checklist

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
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.model.Checklists
import com.webcontrol.android.data.model.EncuestasList
import com.webcontrol.android.data.model.RequestExam
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentTipoChecklistBinding
import com.webcontrol.android.ui.common.adapters.TipoChecklistAdapter
import com.webcontrol.android.ui.common.adapters.TipoChecklistAdapter.TipoChecklistListener
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.RestClient.buildAnglo
import com.webcontrol.android.util.RestClient.buildCaserones
import com.webcontrol.android.util.RestClient.buildCdl
import com.webcontrol.android.util.RestClient.buildGmp
import com.webcontrol.android.util.RestClient.buildKinross
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.dismissLoader
import com.webcontrol.android.util.SharedUtils.showLoader
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@AndroidEntryPoint
class TipoChecklistFragment : Fragment(), TipoChecklistListener, IOnBackPressed {
    var tipoCheckList: String? = null
    var nomCheckList: String? = null
    var idCompany: String? = ""

    private lateinit var binding: FragmentTipoChecklistBinding
    var checklistAdapter: TipoChecklistAdapter? = null
    var checklistsList: List<Checklists>? = null
    var encuestasList: List<EncuestasList>? = null
    var layoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tipoCheckList = this.requireArguments().getString(CHECKLIST_TYPE)
        nomCheckList = this.requireArguments().getString(CHECKLIST_NAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTipoChecklistBinding.inflate(inflater, container, false)
        when (tipoCheckList) {
            "ENC", "EQV", "ENCUESTA" -> {
                binding.lblSubtitle.text = getString(R.string.select_survey_type)
            }

            "TMZ" -> {
                binding.lblSubtitle.text = getString(R.string.select_tamizaje_type)
            }

            "COV" -> {
                binding.lblSubtitle.text = getString(R.string.select_survey)
            }
        }
        binding.loaderChecklists.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark
        )
        binding.loaderChecklists.setOnRefreshListener { load() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checklistsList = ArrayList()
        layoutManager = LinearLayoutManager(context)
        binding.rcvChecklists.layoutManager = layoutManager
        idCompany = SharedUtils.getIdCompany(context)
        if (tipoCheckList == "ENCUESTA")
            loadEncuesta()
        else load()
    }

    private fun loadEncuesta() {
        showLoader(context, getString(R.string.loading))
        binding.loaderChecklists.isRefreshing = true
        val call: Call<ApiResponseAnglo<List<EncuestasList>>>
        val api = buildAnglo()
        call = api.getExamList(RequestExam(0, tipoCheckList!!))
        call.enqueue(object : Callback<ApiResponseAnglo<List<EncuestasList>>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<List<EncuestasList>>>,
                response: Response<ApiResponseAnglo<List<EncuestasList>>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        encuestasList = response.body()!!.data
                        encuestasList?.let { listaEncuesta ->
                            val listaCL: MutableList<Checklists> = mutableListOf()
                            listaEncuesta.forEach { item ->
                                listaCL.add(
                                    Checklists(
                                        item.ExamId.toString(),
                                        item.ExamType.toString(),
                                        item.ExamName.toString()
                                    )
                                )
                            }

                            checklistAdapter = TipoChecklistAdapter(
                                requireContext(),
                                listaCL,
                                this@TipoChecklistFragment
                            )
                            binding.rcvChecklists.adapter = checklistAdapter
                        }
                    }
                }
                binding.loaderChecklists.isRefreshing = false
                dismissLoader(context)
            }

            override fun onFailure(
                call: Call<ApiResponseAnglo<List<EncuestasList>>>,
                t: Throwable
            ) {
                binding.loaderChecklists.isRefreshing = false
                dismissLoader(context)
                Toast.makeText(
                    context,
                    getString(R.string.error_ocurred_getting_list_checklists),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun load() {
        showLoader(context, getString(R.string.loading))
        binding.loaderChecklists.isRefreshing = true
        val call: Call<ApiResponseAnglo<List<Checklists>>>
        if (tipoCheckList == "COV") {
            when (idCompany) {
                Companies.MC.valor -> {
                    call = RestClient.buildMc().getChecklists(
                        object : HashMap<String?, String?>() {
                            init {
                                put("ChecklistTypeId", tipoCheckList)
                            }
                        }
                    )
                }

                Companies.BR.valor -> {
                    call = RestClient.buildBarrick().getChecklists(
                        object : HashMap<String?, String?>() {
                            init {
                                put("ChecklistTypeId", tipoCheckList)
                            }
                        }
                    )
                }

                Companies.GF.valor -> {
                    val apiGF = RestClient.buildGf()
                    call = apiGF.getChecklists(
                        object : HashMap<String?, String?>() {
                            init {
                                put("ChecklistTypeId", tipoCheckList)
                            }
                        }
                    )
                }

                Companies.CDL.valor -> {
                    val apiCDL = buildCdl()
                    call = apiCDL.getChecklists(
                        object : HashMap<String?, String?>() {
                            init {
                                put("ChecklistTypeId", tipoCheckList)
                            }
                        }
                    )
                }

                Companies.KRS.valor -> {
                    val apiKRS = buildKinross()
                    call = apiKRS.getChecklists(
                        object : HashMap<String?, String?>() {
                            init {
                                put("ChecklistTypeId", tipoCheckList)
                            }
                        }
                    )
                }

                Companies.CAS.valor -> {
                    val apiCas = buildCaserones()
                    call = apiCas.getChecklists(
                        object : HashMap<String?, String?>() {
                            init {
                                put("ChecklistTypeId", tipoCheckList)
                            }
                        }
                    )
                }

                else -> {
                    val apiCDL = buildCdl()
                    call = apiCDL.getChecklists(
                        object : HashMap<String?, String?>() {
                            init {
                                put("ChecklistTypeId", tipoCheckList)
                            }
                        }
                    )
                }
            }
        } else {
            call = if (tipoCheckList == "TMZ") {
                val api = buildGmp()
                api.getChecklists(
                    object : HashMap<String?, String?>() {
                        init {
                            put("ChecklistTypeId", tipoCheckList)
                        }
                    }
                )
            } else {
                if (tipoCheckList == "TFS" && idCompany == Companies.KRS.valor) {
                    val api = buildKinross()
                    api.getChecklists(
                        object : HashMap<String?, String?>() {
                            init {
                                put("ChecklistTypeId", tipoCheckList)
                            }
                        }
                    )
                } else {
                    val api = buildAnglo()
                    api.getChecklists(
                        object : HashMap<String?, String?>() {
                            init {
                                put("ChecklistTypeId", tipoCheckList)
                            }
                        }
                    )
                }
            }
        }
        call.enqueue(object : Callback<ApiResponseAnglo<List<Checklists>>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<List<Checklists>>>,
                response: Response<ApiResponseAnglo<List<Checklists>>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        checklistsList = response.body()!!.data
                        checklistsList?.let {
                            checklistAdapter = TipoChecklistAdapter(
                                requireContext(),
                                it,
                                this@TipoChecklistFragment
                            )
                            binding.rcvChecklists.adapter = checklistAdapter
                        }
                    }
                }
                binding.loaderChecklists.isRefreshing = false
                dismissLoader(context)
            }

            override fun onFailure(call: Call<ApiResponseAnglo<List<Checklists>>>, t: Throwable) {
                binding.loaderChecklists.isRefreshing = false
                dismissLoader(context)
                Toast.makeText(
                    context,
                    getString(R.string.error_ocurred_getting_list_checklists),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onRowItemClick(checklists: Checklists?) {
        if (tipoCheckList == "ENCUESTA")
            findNavController().navigate(
                R.id.encuestasTestFragment,
                bundleOf(
                    "TipoEncuesta" to checklists!!.tipoId, "NomEncuesta" to checklists.nombre,
                    "idEncuesta" to checklists.id
                )
            )
        else findNavController().navigate(
            R.id.checkListIngresoFragment,
            bundleOf(
                "TipoCheckList" to checklists!!.tipoId, "NomCheckList" to checklists.nombre,
                "idChecklist" to checklists.id
            )
        )
    }

    override fun onBackPressed(): Boolean {
        findNavController().navigate(
            R.id.historicoCheckListFragment,
            bundleOf(
                HistoricoCheckListFragment.CHECKLIST_TYPE to tipoCheckList,
                HistoricoCheckListFragment.CHECKLIST_NAME to nomCheckList,
            )
        )
        return false
    }

    companion object {
        const val CHECKLIST_TYPE = "tipo_encuesta"
        const val CHECKLIST_NAME = "nombre_encuesta"
    }
}