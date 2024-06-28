package com.webcontrol.android.ui.covid.antapaccay.cuestionarios

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.webcontrol.android.R
import com.webcontrol.android.data.OnItemClickListener
import com.webcontrol.android.data.model.Alternativa
import com.webcontrol.android.data.model.ControlCuarentena
import com.webcontrol.android.data.model.ControlInicial
import com.webcontrol.android.data.model.CuarentenaDetalle
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.ContentListTestBinding
import com.webcontrol.android.ui.covid.cuestionarios.TestListAdapter
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
class TestListFragment : Fragment() {
    private lateinit var binding: ContentListTestBinding
    lateinit var listener: TestListListeners
    private lateinit var testFormat: String
    lateinit var adapter: TestListAdapter


    companion object {
        private const val TEST_FORMAT = "testFormat"
        private const val TAG = "TestListFragment"

        fun newInstance(testFormat: String): TestListFragment {
            val args = Bundle()
            args.putString(TEST_FORMAT, testFormat)
            val fragment = TestListFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        onAttachToParentFragment(parentFragment)
        onCreateComponent()
    }

    private fun onCreateComponent() {
        adapter = TestListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ContentListTestBinding.inflate(inflater, container, false)
        testFormat = requireArguments().getString(TEST_FORMAT, "")
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
                val cuestionario = adapter.getItem(position)
                cuestionario?.let {
                    //listener.onTestSelected(it.codigo)
                }
            }

            override fun onYesNOItemClick(position: Int, view: View?, alternativa: Alternativa) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setUpData() {
        if (testFormat == "F300")
            getControlInicial()

        if (adapter.itemCount == 0)
            showEmptyList()
        else
            showRecyclerList()

    }

    private fun getControlInicial() {
        val api = RestClient.buildAnta()
        val call = api.getControlInicial(SharedUtils.getUsuarioId(activity))

        showLoader(activity, getString(R.string.loading))

        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<ControlInicial>>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>,
                t: Throwable
            ) {
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.failed_to_get_initial_control_historico)
                )
                Log.e(TAG, "getControlInicialResult error: ${t.message}")
                dismissLoader(activity)
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>,
                response: Response<ApiResponseAnglo<ArrayList<ControlInicial>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if (data.size > 0) {
                            val item = data[0]
                            Log.d(TAG, "control inicial found -> $item")
                            getControlCuarentena(item)
                        }
                    } else {
                        SharedUtils.showToast(
                            requireContext(),
                            getString(R.string.failed_to_get_initial_control_historico)
                        )
                        Log.e(TAG, "getControlInicialResult result: $result")

                        dismissLoader(activity)
                    }

                } else {
                    SharedUtils.showToast(
                        requireContext(),
                        getString(R.string.error_occurred_querying_initial_control)
                    )
                    Log.e(TAG, "getControlInicialResult response: $response")
                    dismissLoader(activity)
                }

            }

        })
    }

    private fun getControlCuarentena(item: ControlInicial) {
        val api = RestClient.buildAnta()
        val call = api.getControlCuarentena(rut = SharedUtils.getUsuarioId(activity), id = item.id)

        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<ControlCuarentena>>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<ArrayList<ControlCuarentena>>>,
                t: Throwable
            ) {
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.failed_get_quarantine_history_control)
                )
                Log.e(TAG, "getControlCuarentenaResult error: ${t.message}")
                dismissLoader(activity)
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<ArrayList<ControlCuarentena>>>,
                response: Response<ApiResponseAnglo<ArrayList<ControlCuarentena>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if (data.isNotEmpty()) {
                            val controlCuarentena = data[0]
                            Log.d(TAG, "control cuarentena found -> $controlCuarentena")
                            getHistoricoCuarentena(controlCuarentena)

                        } else {
                            Log.e(TAG, "getControlCuarentena data -> $data")
                            dismissLoader(activity)
                        }
                    } else {
                        SharedUtils.showToast(
                            requireContext(),
                            getString(R.string.error_ocurred_when_consulting_daily_quarantine)
                        )
                        Log.e(TAG, "getControlCuarentena result -> $result")
                        dismissLoader(activity)
                    }

                } else {
                    SharedUtils.showToast(
                        requireContext(),
                        getString(R.string.error_ocurred_when_consulting_daily_quarantine)
                    )
                    Log.e(TAG, "getControlCuarentena response -> $response")
                    dismissLoader(activity)
                }
            }

        })
    }

    private fun getHistoricoCuarentena(controlCuarentena: ControlCuarentena) {

        val api = RestClient.buildAnta()
        val call = api.getCuarentenaDetalle(controlCuarentena.codigo)

        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>>,
                t: Throwable
            ) {
                dismissLoader(activity)
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.failed_get_quarantine_history)
                )
                Log.e(TAG, "getHistoricoCuarentena onFailure -> ${t.message}")
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>>,
                response: Response<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>>
            ) {

                dismissLoader(activity)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if (data.size > 0) {
                            Log.i(TAG, "historico cuarentena detalle data -> $data")
                            adapter.addItems(data)
                            showRecyclerList()
                        } else
                            showEmptyList()
                    } else
                        SharedUtils.showToast(
                            requireContext(),
                            getString(R.string.error_ocurred_when_consulting_daily_quarantine)
                        )
                } else
                    SharedUtils.showToast(
                        requireContext(),
                        getString(R.string.error_ocurred_when_consulting_daily_quarantine)
                    )
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
            listener = parentFragment as TestListListeners
        } catch (ex: ClassCastException) {
            throw ClassCastException("${parentFragment.toString()} debe implementar los métodos de TestListListener")
        }
    }

    private fun setFabAction() {
        binding.fabNewTest.setOnClickListener {
            listener.onFabPressed()
        }
    }

    interface TestListListeners {
        fun onTestSelected(testID: Int)
        fun onFabPressed()
    }
}