package com.webcontrol.android.ui.covid.antapaccay.declaracion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.android.data.OnItemClickListener
import com.webcontrol.android.data.model.Alternativa
import com.webcontrol.android.data.model.DJConsolidado
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.ContentListTestBinding
import com.webcontrol.android.ui.covid.declaracion.DJListAdapter
import com.webcontrol.android.util.Constants
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.dismissLoader
import com.webcontrol.android.util.SharedUtils.showLoader
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ClassCastException

@AndroidEntryPoint
class DJListFragment: Fragment() {
    private lateinit var binding: ContentListTestBinding
    lateinit var listener: DJListListeners
    lateinit var adapter: DJListAdapter

    companion object {
        private  const val TAG = "DJListFragment"

        fun newInstance(): DJListFragment {
            return DJListFragment()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        onAttachToParentFragment(parentFragment)
        onCreateComponent()
    }

    private fun onCreateComponent() {
        adapter = DJListAdapter(Constants.CLIENTE_ANTA, requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContentListTestBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {
        setUpAdapter()
        initializeRecyclerView()
        setUpData()
    }

    private fun setUpAdapter() {
        adapter.setOnItemClickListener(onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int, view: View?) {
                val declaracion = adapter.getItem(position)
                declaracion?.let {
                    //listener.onTestSelected(it.codigo)
                }
            }

            override fun onYesNOItemClick(position: Int, view: View?, alternativa: Alternativa) {}
        })
    }

    private fun setUpData() {
        getConsolidado()
    }

    private fun getConsolidado() {
        val api = RestClient.buildAnta()
        val call = api.getDJConsolidado(SharedUtils.getUsuarioId(activity))
        showLoader(activity, "Cargando...")
        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<DJConsolidado>>> {
            override fun onFailure(call: Call<ApiResponseAnglo<ArrayList<DJConsolidado>>>, t: Throwable) {
                dismissLoader(activity)
                SharedUtils.showToast(requireContext(), "No se pudo obtener el histórico de declaraciones.")
            }

            override fun onResponse(
                    call: Call<ApiResponseAnglo<ArrayList<DJConsolidado>>>,
                    response: Response<ApiResponseAnglo<ArrayList<DJConsolidado>>>) {

                dismissLoader(activity)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if ( data.size > 0 ) {
                            adapter.addItems(data)
                            showRecyclerList()
                        } else
                            showEmptyList()
                    } else
                        SharedUtils.showToast(requireContext(), "Ocurrio un error al consultar declaraciones anteriores.")
                } else
                    SharedUtils.showToast(requireContext(), "Ocurrio un error al consultar declaraciones anteriores.")
            }

        })
    }

    private fun showRecyclerList() {
        binding.emptyContainer.visibility = View.GONE
        binding.testRecyclerView.visibility = View.VISIBLE
    }

    private fun showEmptyList() {
        binding.emptyContainer.visibility = View.VISIBLE
        binding.testRecyclerView.visibility = View.GONE
    }

    private fun initializeRecyclerView() {
        val divider = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)

        binding.testRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.testRecyclerView.addItemDecoration(divider)
        binding.testRecyclerView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setFabAction()
    }

    private fun onAttachToParentFragment(parentFragment: Fragment?) {
        try {
            listener = parentFragment as DJListListeners
        } catch (ex: ClassCastException) {
            throw ClassCastException("${parentFragment.toString()} debe implementar los métodos de TestListListener")
        }
    }

    private fun setFabAction() {
        binding.fabNewTest.setOnClickListener {
            listener.onFabPressed()
        }
    }

    interface DJListListeners {
        fun onTestSelected(testID: Int)
        fun onFabPressed()
    }
}