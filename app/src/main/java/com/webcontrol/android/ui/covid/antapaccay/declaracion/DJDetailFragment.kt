package com.webcontrol.android.ui.covid.antapaccay.declaracion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.data.model.ControlInicial
import com.webcontrol.android.data.model.DJPregunta
import com.webcontrol.android.data.model.DJRespuesta
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.ui.menu.Destination
import com.webcontrol.android.databinding.ContentDetalleTestBinding
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.time
import com.webcontrol.android.util.SharedUtils.wCDate
import com.webcontrol.android.util.addFragment
import com.webcontrol.android.util.removeFragment
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class DJDetailFragment : Fragment() {
    private lateinit var binding: ContentDetalleTestBinding
    private lateinit var listCuestionario: java.util.ArrayList<DJPregunta>
    private lateinit var controlInicial: ControlInicial
    private lateinit var adapter: DJDetailAdapter
    private lateinit var testResponseList: ArrayList<DJRespuesta>
    private var modificar: Boolean = false
    private var positiveFlag = false
    private var isDECII = false

    companion object {
        private const val CUESTIONARIO_LIST = "cuestionarioList"
        private const val TAG = "DJDetailFragment"
        private const val MODIFICAR = "MODIFICAR"
        private const val OM = 2
        private const val TXT = 4
        private const val DECII = "SecondDeclaration"

        fun newInstance(cuestionarioList: ArrayList<DJPregunta>, modificar: Boolean, isSecondDeclaration: Boolean): DJDetailFragment {
            val args = Bundle()
            args.putParcelableArrayList(CUESTIONARIO_LIST, cuestionarioList)
            args.putBoolean(MODIFICAR, modificar)
            args.putBoolean(DECII, isSecondDeclaration)
            val fragment = DJDetailFragment()
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
        adapter = DJDetailAdapter(requireActivity(), testResponseList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContentDetalleTestBinding.inflate(inflater, container, false)

        listCuestionario = requireArguments().getParcelableArrayList(CUESTIONARIO_LIST)!!
        modificar = requireArguments().getBoolean(MODIFICAR)
        isDECII = requireArguments().getBoolean(DECII)
        controlInicial = ControlInicial(
                rut = SharedUtils.getUsuarioId(activity),
                empresa = SharedUtils.getAntaCompany(activity),
                fecha = wCDate,
                hora = time,
                inicial = "SI"
        )
        initUI()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lblTestFecha.text = SharedUtils.getNiceDate(wCDate)
        binding.lblTestHora.text = time

        showMessage(
                "Declaración Jurada previa acceso a instalaciones",
                "Como parte de las medidas de prevención y control dispuestas por el Ministerio de Salud " +
                        "a partir de la emergencia sanitaria declarada mediante Decreto Supremo " +
                        "N° 008-2020-SA, requerimos que por favor complete la siguiente declaración. " +
                        "CONSIDERE SUS RESPUESTAS EN FUNCIÓN A LOS ÚLTIMOS 14 DIAS.",
                "Entiendo",
                MaterialDialog.SingleButtonCallback { dialog, which ->
                    dialog.dismiss()
                }
        )
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
            if (validateForm())
                showEmptyMessage()
            else {
                showMessage(
                        "Consentimiento",
                        "Tengo conocimiento que el presente cuestionario tiene caracter de " +
                                "declaración jurada, que en caso sea falsa o errónea puedo llegar a " +
                                "poner en riesgo mi salud y la del personal presente en el campamento; " +
                                "y que en dicho caso se podrán aplicar las medidas que la Ley " +
                                "establece para tal efecto.",
                        "Acepto",
                        MaterialDialog.SingleButtonCallback { dialog, _ ->
                            dialog.dismiss()
                            showPruebaRapidaDialog()
                        },
                        "No Acepto",
                        MaterialDialog.SingleButtonCallback { dialog, _ ->
                            showNoDeacuerdo()
                        }
                )
            }
        }
    }

    private fun showNoDeacuerdo() {
        showMessage(
                "Alerta",
                "Su declaración no ha sido enviada por no dar consentimiento. " +
                        "Para acceder a su faena debe llenar la declaración y aceptar el consentimiento.",
                "Entiendo",
                MaterialDialog.SingleButtonCallback { dialog, _ ->
                    dialog.dismiss()
                    returnToMainFragment()
                }
        )
    }

    private fun showPruebaRapidaDialog() {
        showMessage(
                "Consentimiento de pruebas",
                "En señal de conformidad, procedo a dar mi consentimiento para la prueba de " +
                        "despistaje para el COVID-19",
                "Acepto",
                MaterialDialog.SingleButtonCallback { dialog, _ ->
                    controlInicial.consentimiento = "SI"
                    sendDJ()
                    dialog.dismiss()
                },
                "No Acepto",
                MaterialDialog.SingleButtonCallback { dialog, _ ->
                    controlInicial.consentimiento = "NO"
                    sendControlInicial(controlInicial)
                    dialog.dismiss()
                    returnToMainFragment()
                }
        )
    }

    private fun sendDJ() {
        val api = RestClient.buildAnta()
        val call = api.sendDJTest(testResponseList)
        showLoader()
        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                dismissLoader()
                SharedUtils.showToast(requireContext(), "Error de conexión. No se pudo guardar las respuestas")
            }

            override fun onResponse(call: Call<ApiResponseAnglo<String>>, response: Response<ApiResponseAnglo<String>>) {
                dismissLoader()

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        SharedUtils.showToast(requireContext(), "Test enviado correctamente")
                        Log.i(TAG, "result data -> ${result.data}")
                        if (positiveFlag) {
                            showPositiveMessage()
                        }
                        controlInicial.positivo = if (positiveFlag) "SI" else "NO"

                        sendControlInicial(controlInicial)
                        returnToMainFragment()
                    }
                }
            }
        })
    }

    private fun sendControlInicial(controlInicial: ControlInicial) {
        val api = RestClient.buildAnta()
        val call =  if (modificar) api.updateControlInicial(arrayListOf(controlInicial))
                        else api.insertControlInicial(arrayListOf(controlInicial))

        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                Log.e(TAG, "sendControlInicial error: ${t.message}")
            }

            override fun onResponse(call: Call<ApiResponseAnglo<String>>, response: Response<ApiResponseAnglo<String>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        Log.e(TAG, "sendControlInicial onResponse: ${result.data}")
                    }
                }
            }

        })
    }

    private fun showLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(activity, R.raw.loaddinglottie, "Cargando...", 0, 500, 200)
    }

    private fun dismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(activity)
    }

    private fun validateForm(): Boolean {
        var emptyFlag = false
        positiveFlag = false
        val yNArray = testResponseList.filter { it.tipo == "SN" }
        val oMArray = testResponseList.filter { it.tipo == "OM" || it.tipo == "OMU" }
        val tRAVArray = testResponseList.filter { it.tipo == "TRAV" }
        val tRPEArray = testResponseList.filter { it.tipo == "TRPE" }
        val blockArray = testResponseList.filter { it.bloquea }

        if (testResponseList.isNullOrEmpty())
            emptyFlag = true

        val yesNoQuestionList = listCuestionario.filter { it.tipo == "SN" }

        if (yesNoQuestionList.size != yNArray.size)
            emptyFlag = true
        if (oMArray.isEmpty())
            emptyFlag = true

        val omGroup = oMArray.groupBy { it.idHito }.toList()

        omGroup.forEach { group ->
            val filterGroup = group.second.filter { !it.bloquea }
            Log.w(TAG, "filter group -> $filterGroup")
            if (filterGroup.isNotEmpty()) {
                val result = filterGroup.any { it.respuesta != "NO" }
                if (!result)
                    emptyFlag = true
            }


        }
        Log.e(TAG, "test blockArray: $blockArray")
        blockArray.forEach {
            val stringList = it.respuesta.split(",")
                    .map { str -> str.trim() } as ArrayList<String>
            if (stringList.isNotEmpty() && !stringList.contains("NO"))
                positiveFlag = true

            Log.w(TAG, "test stringList: $stringList")
        }


        Log.w(TAG, "test positiveFlag: $positiveFlag")

        return emptyFlag
    }

    private fun showEmptyMessage() {
        SharedUtils.showToast(requireContext(), "Por favor complete todo el cuestionario.")
    }

    private fun showPositiveMessage() {
        showMessage(
                "Alerta",
                "Segun los lineamientos para la vigilancia, prevención y control de la " +
                        "salud de los trabajadores con riesgo de exposición a COVID-19 y los " +
                        "protocolos de Antapaccay. Usted se encuentra dentro de la población " +
                        "considerada de riesgo o presenta sintomatología bajo vigilancia médica. " +
                        "Contacte al área de Salud Ocupacional de Antapaccay al 940498511.",
                "Entiendo",
                MaterialDialog.SingleButtonCallback { dialog, _ ->
                    dialog.dismiss()
                }
        )
    }

    private fun returnToMainFragment() {
        removeFragment(parentFragmentManager, this@DJDetailFragment)
        addFragment(parentFragmentManager, R.id.root_layout, MainDJFragment.newInstance(isDECII), "MainDJFragment")
    }

    private fun showMessage(
            title: String,
            content: String,
            positiveText: String,
            positiveAction: MaterialDialog.SingleButtonCallback,
            negativeText: String? = null,
            negativeAction: MaterialDialog.SingleButtonCallback? = null) {
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

    private fun setUpAdapter() {
        // adapter setup
    }

    private fun setDataToUI() {
        binding.lblTestTitle.text = "Declaración Jurada"
        binding.lblTestNumRows.text = "${listCuestionario.size} preguntas"
    }

    private fun setUpData() {
        if (modificar)
            getControlInicial()

        listCuestionario.forEach {
            Log.i(TAG, "tipo: ${it.tipo}")
        }
        adapter.addItems(listCuestionario)
    }

    private fun getControlInicial() {
        val api = RestClient.buildAnta()
        val call = api.getControlInicial(SharedUtils.getUsuarioId(activity))

        SharedUtils.showLoader(activity, "Cargando...")

        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<ControlInicial>>> {
            override fun onFailure(call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>, t: Throwable) {
                SharedUtils.showToast(requireContext(), "No se pudo obtener el histórico de control inicial.")
                Log.e(TAG, "getControlInicialResult error: ${t.message}")
                SharedUtils.dismissLoader(activity)
            }

            override fun onResponse(call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>, response: Response<ApiResponseAnglo<ArrayList<ControlInicial>>>) {
                SharedUtils.dismissLoader(activity)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if (data.size > 0) {
                            checkValidControlInicial(data)
                        }
                    } else{
                        SharedUtils.showToast(requireContext(), "No se pudo obtener el histórico de control inicial.")
                        Log.e(TAG, "getControlInicialResult result: $result")
                    }

                } else {
                    SharedUtils.showToast(requireContext(), "Ocurrio un error al consultar su control inicial.")
                    Log.e(TAG, "getControlInicialResult response: $response")
                }

            }

        })
    }

    private fun checkValidControlInicial(originalList: ArrayList<ControlInicial>) {
        if (isDECII) {
            originalList.filter {
                it.codControlInicialPadre != null &&
                !it.fechaVisible.isNullOrEmpty()
            }.let {
                if (it.isNotEmpty()) {
                    if (SharedUtils.compareDays(it[0].fechaVisible!!, wCDate, "yyyyMMdd")!! <= 0 ) {
                        controlInicial = it[0]
                    } else {
                        Toast.makeText(context, "No cuenta con control inicial válido", Toast.LENGTH_SHORT).show()
                        returnToMainFragment()
                    }

                }
            }
        } else {
            originalList.filter { it.codControlInicialPadre == null }.let {
                if (it.isNotEmpty()) {
                    controlInicial = it[0]
                }
            }
        }
    }

    private fun initializeRecyclerView() {
        val divider = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)

        binding.testDetailRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.testDetailRecyclerView.addItemDecoration(divider)
        binding.testDetailRecyclerView.adapter = adapter
        binding.testDetailRecyclerView.recycledViewPool.setMaxRecycledViews(OM, 0)
        binding.testDetailRecyclerView.recycledViewPool.setMaxRecycledViews(TXT, 0)
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
}