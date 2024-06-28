package com.webcontrol.android.ui.covid.antapaccay.declaracion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.model.ControlInicial
import com.webcontrol.android.data.model.DJConsolidado
import com.webcontrol.android.data.model.DJPregunta
import com.webcontrol.android.data.model.WorkerAptoDJ
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentMainTestBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.*
import com.webcontrol.android.util.SharedUtils.getDayCount
import com.webcontrol.android.util.SharedUtils.wCDate
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

@AndroidEntryPoint
class MainDJFragment : Fragment(), DJListFragment.DJListListeners, IOnBackPressed {
    private lateinit var binding: FragmentMainTestBinding
    private var isDECII = false

    companion object {
        private const val TAG = "MainDJFragment"
        private const val DECII = "SecondDeclaration"
        const val DJ_LIST_TAG = "DJList"
        const val DJ_DETAIL_TAG = "DJDetail"
        const val title = "title"

        fun newInstance(isSecondDeclaration: Boolean = false): MainDJFragment {
            val args = Bundle()
            args.putBoolean(DECII, isSecondDeclaration)
            val fragment = MainDJFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title=requireArguments().getString(title)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainTestBinding.inflate(inflater, container, false)
        isDECII = requireArguments().getBoolean(DECII)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            addFragment(
                childFragmentManager,
                R.id.root_layout,
                DJListFragment.newInstance(),
                DJ_LIST_TAG
            )
        }
    }

    override fun onBackPressed(): Boolean {
        val childFragment = childFragmentManager.findFragmentByTag(DJ_DETAIL_TAG)
        Log.d(TAG, "Fragment found: $childFragment")
        if (childFragment != null) {
            showMessage(
                getString(R.string.alert_title),
                getString(R.string.want_to_return_test_not_saved),
                getString(R.string.yes),
                { dialog, _ ->
                    dialog.dismiss()
                    removeFragment(childFragmentManager, childFragment)
                    addFragment(
                        childFragmentManager,
                        R.id.root_layout,
                        DJListFragment.newInstance(),
                        DJ_LIST_TAG
                    )
                },
                getString(R.string.no),
                { dialog, _ ->
                    dialog.dismiss()
                }
            )
        } else
            (activity as MainActivity).confirmSessionAbandon()

        return false
    }

    override fun onTestSelected(testID: Int) {
        TODO("Not yet implemented")
    }

    override fun onFabPressed() {
        checkWorkerApto()

    }

    private fun checkWorkerApto() {
        val api = RestClient.buildAnta()
        val call = api.getWorkerAptoDJ(SharedUtils.getUsuarioId(activity), wCDate)

        showLoader()

        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<WorkerAptoDJ>>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<ArrayList<WorkerAptoDJ>>>,
                t: Throwable
            ) {
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.your_authorization_file_return_not_obtained)
                )
                Log.e(TAG, "checkWorkerApto failure: ${t.message}")
                dismissLoader()
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<ArrayList<WorkerAptoDJ>>>,
                response: Response<ApiResponseAnglo<ArrayList<WorkerAptoDJ>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if (data.isNotEmpty())
                            getControlInicial()
                        else {
                            dismissLoader()
                            showMessage(
                                getString(R.string.title_dialog_alert),
                                getString(R.string.you_are_not_authorized_to_fill_your_declaration),
                                getString(R.string.to_close),
                                MaterialDialog.SingleButtonCallback { dialog, _ ->
                                    dialog.dismiss()
                                }
                            )
                        }
                    } else {
                        dismissLoader()
                        SharedUtils.showToast(
                            requireContext(),
                            getString(R.string.your_authorization_file_return_not_obtained)
                        )
                        Log.e(TAG, "checkWorkerApto result: $result")
                    }
                } else {
                    dismissLoader()
                    SharedUtils.showToast(
                        requireContext(),
                        getString(R.string.your_authorization_file_return_not_obtained)
                    )
                    Log.e(TAG, "checkWorkerApto response: $response")
                }
            }
        })
    }

    private fun getControlInicial() {
        val api = RestClient.buildAnta()
        val call = api.getControlInicial(SharedUtils.getUsuarioId(activity))

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
                dismissLoader()
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>,
                response: Response<ApiResponseAnglo<ArrayList<ControlInicial>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if (data.isNotEmpty()) {
                            val last = data[0]
                            // es 2do control inicial?
                            if (last.codControlInicialPadre != null &&
                                !last.fechaVisible.isNullOrEmpty()
                            ) {
                                if (last.positivo == null) { // es control inicial sin resultado ?
                                    if (SharedUtils.compareDays(
                                            last.fechaVisible!!,
                                            SharedUtils.wCDate,
                                            "yyyyMMdd"
                                        )!! <= 0
                                    ) { // es fecha válida?
                                        isDECII = true
                                        evaluateSecondDeclarations(data)
                                    }
                                } else {
                                    isDECII = false
                                    evaluateNormalDeclarations(data)
                                }
                            } else if (last.codControlInicialPadre == null) {
                                if (!last.inicial.isNullOrEmpty() && last.inicial == "SI") {
                                    isDECII = false
                                    evaluateNormalDeclarations(data)
                                }
                            }
                        } else {
                            isDECII = false
                            initDetailTestFragment()
                        }
                    } else {
                        SharedUtils.showToast(
                            requireContext(),
                            getString(R.string.failed_to_get_initial_control_historico)
                        )
                        Log.e(TAG, "getControlInicialResult result: $result")

                        dismissLoader()
                    }

                } else {
                    SharedUtils.showToast(
                        requireContext(),
                        getString(R.string.error_occurred_querying_initial_control)
                    )
                    Log.e(TAG, "getControlInicialResult response: $response")
                    dismissLoader()
                }

            }

        })
    }

    private fun evaluateNormalDeclarations(originalList: ArrayList<ControlInicial>) {
        originalList.filter { it.codControlInicialPadre == null }.let {
            if (it.isNotEmpty()) {
                val item = it[0]
                Log.d(TAG, "control inicial found -> $item")
                if (getDayCount(item.fecha!!, wCDate, "yyyyMMdd")!! <= 14 && item.positivo == "SI")
                    showDeniedMessage()
                else if (item.positivo == null)
                    initDetailTestFragment(true)
                else if (item.fecha!! != wCDate)
                    getConsolidado()
                else
                    showNoDups()
            }
        }
    }

    private fun evaluateSecondDeclarations(originalList: ArrayList<ControlInicial>) {
        originalList.filter {
            it.codControlInicialPadre != null &&
                    !it.fechaVisible.isNullOrEmpty()
        }.let {
            if (it.isNotEmpty()) {
                if (SharedUtils.compareDays(it[0].fechaVisible!!, wCDate, "yyyyMMdd")!! <= 0) {
                    val item = it[0]
                    if (getDayCount(
                            item.fecha!!,
                            wCDate,
                            "yyyyMMdd"
                        )!! <= 14 && item.positivo == "SI"
                    )
                        showDeniedMessage()
                    else if (item.positivo == null)
                        initDetailTestFragment(true)
                    else if (item.fecha!! == wCDate)
                        getConsolidado(true)
                    else
                        showNoDups()
                } else
                    Toast.makeText(
                        context,
                        getString(R.string.not_available_to_fill_declaration),
                        Toast.LENGTH_SHORT
                    ).show()
            } else
                Toast.makeText(context, getString(R.string.no_parent_statement), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun getConsolidado(mod: Boolean = false) {
        val api = RestClient.buildAnta()
        val call = api.getDJConsolidado(SharedUtils.getUsuarioId(activity))

        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<DJConsolidado>>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<ArrayList<DJConsolidado>>>,
                t: Throwable
            ) {
                dismissLoader()
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.not_available_to_fill_declaration)
                )
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<ArrayList<DJConsolidado>>>,
                response: Response<ApiResponseAnglo<ArrayList<DJConsolidado>>>
            ) {

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if (data.size > 0) {
                            val ultimo = data[0]
                            if (ultimo.fecha != wCDate)
                                initDetailTestFragment(mod)
                            else
                                showNoDups()
                        } else
                            initDetailTestFragment(mod)
                    } else {
                        dismissLoader()
                        SharedUtils.showToast(
                            requireContext(),
                            getString(R.string.error_occurred_querying_statement)
                        )
                    }

                } else {
                    dismissLoader()
                    SharedUtils.showToast(
                        requireContext(),
                        getString(R.string.error_occurred_querying_statement)
                    )
                }

            }

        })
    }

    private fun showNoDups() {
        dismissLoader()
        showMessage(
            getString(R.string.title_dialog_alert),
            getString(R.string.you_cannot_fill_more_than_one_statement_per_day),
            getString(R.string.yes),
            MaterialDialog.SingleButtonCallback { dialog, _ ->
                dialog.dismiss()
            }
        )
    }

    private fun showDeniedMessage() {
        dismissLoader()
        showMessage(
            getString(R.string.alert_title),
            getString(R.string.you_not_fill_declaration_suspected_of_covid),
            getString(R.string.understand),
            MaterialDialog.SingleButtonCallback { dialog, _ ->
                dialog.dismiss()
            }
        )
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

    private fun initDetailTestFragment(modificar: Boolean = false) {
        try {
            val api = RestClient.buildAnta()
            val call = api.dJPreguntas()


            call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<DJPregunta>?>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<ArrayList<DJPregunta>?>>,
                    response: Response<ApiResponseAnglo<ArrayList<DJPregunta>?>>
                ) {

                    dismissLoader()

                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            val data = result.data
                            data?.let {
                                if (it.isNotEmpty()) {
                                    replaceFragment(
                                        childFragmentManager,
                                        R.id.root_layout,
                                        DJDetailFragment.newInstance(it, modificar, isDECII),
                                        DJ_DETAIL_TAG
                                    )
                                } else {
                                    Log.e(TAG, "initDetailTestFragment, no existen preguntas ")
                                    SharedUtils.showToast(
                                        requireContext(),
                                        getString(R.string.not_get_test_no_questions)
                                    )
                                }
                            }
                        } else {
                            Log.e(TAG, "initDetailTestFragment, result: $result")
                            SharedUtils.showToast(
                                requireContext(),
                                getString(R.string.not_get_test)
                            )
                        }
                    } else {
                        Log.e(
                            TAG,
                            "initDetailTestFragment, response not successful, reason: ${response.message()}"
                        )
                        SharedUtils.showToast(requireContext(), getString(R.string.not_get_test))
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponseAnglo<ArrayList<DJPregunta>?>>,
                    t: Throwable
                ) {

                    dismissLoader()

                    Log.e(TAG, "initDetailTestFragment, call failure: ${t.message}", t)
                    SharedUtils.showToast(requireContext(), getString(R.string.not_get_test))
                }
            })
        } catch (ex: Exception) {
            dismissLoader()
            Log.e(TAG, "initDetailTestFragment, reason: ${ex.message}", ex)
            SharedUtils.showToast(requireContext(), getString(R.string.not_get_test))
        }
    }

    private fun showLoader() {
        ProgressLoadingJIGB.startLoadingJIGB(
            activity,
            R.raw.loaddinglottie,
            getString(R.string.loading),
            0,
            500,
            200
        )
    }

    private fun dismissLoader() {
        ProgressLoadingJIGB.finishLoadingJIGB(activity)
    }
}