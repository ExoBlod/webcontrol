package com.webcontrol.android.ui.covid.antapaccay.cuestionarios

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.webcontrol.android.R
import com.webcontrol.android.data.OnItemClickListener
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.ContentDetalleTestBinding
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.dismissLoader
import com.webcontrol.android.util.SharedUtils.showLoader
import com.webcontrol.android.util.SharedUtils.wCDate
import com.webcontrol.android.util.removeFragment
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class TestDetailFragment : Fragment() {

    private lateinit var binding: ContentDetalleTestBinding
    private var listCuestionario: java.util.ArrayList<Cuestionario> = ArrayList()
    private lateinit var adapter: TestDetailAdapter
    private lateinit var testResponseList: ArrayList<CuestionarioResponse>
    private lateinit var finishBtnContainer: LinearLayout
    private var testFormat: String = ""
    private lateinit var workerControlCuarentena: ControlCuarentena
    private var workerControlInicial: ControlInicial = ControlInicial()

    companion object {
        private const val CUESTIONARIO_LIST = "cuestionarioList"
        private const val CONTROL_INICIAL = "controlInicial"
        private const val CONTROL_CUARENTENA = "controlCuarentena"
        private const val TAG = "DetailTestFragment"
        private const val TEST_FORMAT = "TestFormat"

        fun newInstance(
            cuestionarioList: String,
            testFormat: String,
            ctrlInicial: ControlInicial,
            ctrlCuarentena: ControlCuarentena
        ): TestDetailFragment {
            val args = Bundle()
            args.putString(CUESTIONARIO_LIST, cuestionarioList)
            args.putString(TEST_FORMAT, testFormat)
            args.putParcelable(CONTROL_INICIAL, ctrlInicial)
            args.putParcelable(CONTROL_CUARENTENA, ctrlCuarentena)
            val fragment = TestDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        onCreateComponent()
    }

    private fun onCreateComponent() {
        testResponseList = ArrayList()
        adapter =
            TestDetailAdapter(requireActivity(), testResponseList, workerControlInicial, testFormat)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ContentDetalleTestBinding.inflate(inflater, container, false)
        val lista = requireArguments().getString(CUESTIONARIO_LIST)
        val typeToken = object : TypeToken<List<Cuestionario>>(){}.type
        listCuestionario = Gson().fromJson(lista,typeToken)
        testFormat = requireArguments().getString(TEST_FORMAT) ?: ""
        workerControlInicial = requireArguments().getParcelable(CONTROL_INICIAL)!!
        workerControlCuarentena = requireArguments().getParcelable(CONTROL_CUARENTENA)!!

        initUI()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lblTestFecha.text = SharedUtils.getNiceDate(SharedUtils.wCDate)
        binding.lblTestHora.text = SharedUtils.time
    }

    private fun initUI() {
        setUpAdapter()
        initializeRecyclerView()
        setUpData()
        setDataToUI()
        setUpSendBtn()
    }

    private fun setUpSendBtn() {
        binding.testBtnFinish.setOnClickListener {

            Log.i(TAG, "testResponseList -> $testResponseList")
            showMessage(
                getString(R.string.alert_title),
                getString(R.string.send_responses),
                getString(R.string.yes),
                MaterialDialog.SingleButtonCallback { dialog, _ ->
                    dialog.dismiss()
                    checkNotNullResponseList()
                    sendTest()
                },
                getString(R.string.no),
                MaterialDialog.SingleButtonCallback { dialog, _ ->
                    dialog.dismiss()
                }
            )
        }
    }

    private fun checkNotNullResponseList() {
        testResponseList.forEach { item ->
            item.codControlInicial = workerControlInicial.id
            item.codFormato = testFormat
            item.fecha = wCDate
            item.codCuarentena = workerControlCuarentena.codigo
        }
    }

    private fun showMessage(
        title: String,
        content: String,
        positiveText: String,
        positiveAction: MaterialDialog.SingleButtonCallback,
        negativeText: String? = null,
        negativeAction: MaterialDialog.SingleButtonCallback? = null
    ) {
        val dialogBuilder =
            MaterialDialog.Builder(requireActivity())
                .title(title)
                .content(content)
                .autoDismiss(true)
                .positiveText(positiveText)
                .onPositive(positiveAction)
        negativeText?.let {
            dialogBuilder.negativeText(it)
            negativeAction?.let { callback ->
                dialogBuilder.onNegative(callback)
            }
        }
        dialogBuilder.show()

    }

    private fun validateTest(): Boolean {

        val omArray = ArrayList<CuestionarioResponse>()
        testResponseList.forEach {
            if (listCuestionario.find { cuestionario ->
                    cuestionario.codigo == it.codCuestionario
                            && cuestionario.descripcion == "2.2 Sintomas"
                } != null)
                omArray.add(it)
        }
        return omArray.isNotEmpty()
    }

    private fun sendTest() {
        try {
            val api = RestClient.buildAnta()
            val call = api.sendCuestionario(testResponseList)

            showLoader(activity, getString(R.string.loading))
            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    dismissLoader(activity)
                    SharedUtils.showToast(
                        requireContext(),
                        getString(R.string.connection_error_answers_could_not_saved)
                    )
                    Log.e(TAG, "sendTest call failure -> ${t.message}", t)
                }

                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    dismissLoader(activity)

                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            SharedUtils.showToast(
                                requireContext(),
                                getString(R.string.send_test_success)
                            )
                            Log.i(TAG, "result data -> ${result.data}")

                            if (testFormat == "F200")
                                updateControlCuarentena(workerControlCuarentena)
                            else if (testFormat == "F300")
                                insertCuarentenaDetalle(workerControlCuarentena)
                            returnToMainFragment()
                        }
                    }
                }
            })
        } catch (ex: Exception) {
            Log.e(TAG, "send test error -> ${ex.message}", ex)
            SharedUtils.showToast(requireContext(), getString(R.string.not_send_test))
        }
    }

    private fun insertCuarentenaDetalle(ctrlCuarentena: ControlCuarentena) {
        val cuarentenaDetalle = CuarentenaDetalle(
            codCuarentena = ctrlCuarentena.codigo,
            fecha = wCDate,
            f300 = "SI",
            hora = SharedUtils.time
        )
        val api = RestClient.buildAnta()
        val call = api.insertCuarentenaDetalle(cuarentenaDetalle)

        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                Log.e(TAG, "insertCuarentenaDetalle error: ${t.message}")
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<String>>,
                response: Response<ApiResponseAnglo<String>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        Log.i(TAG, "insertCuarentenaDetalle onResponse: ${result.data}")
                    }
                }
            }

        })

    }

    private fun updateControlCuarentena(ctrlCuarentena: ControlCuarentena) {

        ctrlCuarentena.f200 = "SI"

        val api = RestClient.buildAnta()
        val call = api.updateControlCuarentena(ctrlCuarentena)

        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                Log.e(TAG, "updateControlCuarentena error: ${t.message}")
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<String>>,
                response: Response<ApiResponseAnglo<String>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        Log.i(TAG, "updateControlCuarentena onResponse: ${result.data}")
                    }
                }
            }

        })
    }

    private fun returnToMainFragment() {
        removeFragment(parentFragmentManager, this@TestDetailFragment)
        findNavController().navigate(R.id.mainTestFragment, bundleOf("id" to "F300"))
        //addFragment(parentFragmentManager, R.id.root_layout, MainTestFragment.newInstance("F300"), "MainTestFragment")
    }

    private fun setDataToUI() {
        setTestTitle()
        binding.lblTestNumRows.text = getString(R.string.number_questions, listCuestionario.size.toString())
    }

    private fun setTestTitle() {
        binding.lblTestTitle.text = when (testFormat) {
            "F00" -> " Triaje"
            "F100" -> "Registro de Pruebas Rápidas"
            "F200" -> "Investigación Epidemiológica"
            "F300" -> "Registro de Seguimiento Clínico"
            else -> "Test sin formato"
        }
    }

    private fun setUpData() {
        listCuestionario.forEach {
            Log.i(TAG, "tipo: ${it.tipo}")
            Log.w(TAG, "item Cuestionario -> $it")
        }
        listCuestionario =
            listCuestionario.filter { it.descripcion == "2.2 Sintomas" } as java.util.ArrayList<Cuestionario>
        adapter.addItems(listCuestionario)
        Log.e(TAG, "listCuestionario size -> ${listCuestionario.size}")
    }

    private fun initializeRecyclerView() {
        val divider = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)

        binding.testDetailRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.testDetailRecyclerView.addItemDecoration(divider)
        binding.testDetailRecyclerView.adapter = adapter
        /*
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                finishBtnContainer.visibility = if (!recyclerView.canScrollVertically(1))
                                                    View.VISIBLE
                                                else
                                                    View.GONE

                val params: ViewGroup.MarginLayoutParams = recyclerView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, 0, 0, finishBtnContainer.measuredHeight + 8)
                recyclerView.layoutParams = params
            }
        })
         */
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
                val cuestionario = adapter.getItem(position)
                cuestionario?.let {
                    createOrReplaceAnswer(it, alternativa)
                }
            }
        })
    }

    private fun createOrReplaceAnswer(cuestionario: Cuestionario, alternativa: Alternativa) {
        testResponseList.find { response -> response.codCuestionario == cuestionario.codigo }.let {
            if (it != null) {
                it.codAlternativa = alternativa.codigo
                //it.dato = alternativa.descripcion
            } else {
                val cuestionarioResponse = CuestionarioResponse(
                    codControlInicial = workerControlInicial.id,
                    rut = SharedUtils.getUsuarioId(activity),
                    codCuestionario = cuestionario.codigo,
                    codAlternativa = alternativa.codigo,
                    //dato = alternativa.descripcion,
                    fecha = wCDate,
                    codFormato = testFormat
                )

                testResponseList.add(cuestionarioResponse)
            }
        }
    }
}