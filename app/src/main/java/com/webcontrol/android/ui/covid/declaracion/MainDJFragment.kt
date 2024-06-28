package com.webcontrol.android.ui.covid.declaracion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.model.ControlInicial
import com.webcontrol.android.data.model.DJPregunta
import com.webcontrol.android.data.model.WorkerAptoDJ
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentMainTestBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.SharedUtils.getDayCount
import com.webcontrol.android.util.SharedUtils.wCDate
import com.webcontrol.android.util.addFragment
import com.webcontrol.android.util.removeFragment
import com.webcontrol.android.util.replaceFragment
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class MainDJFragment : Fragment(), DJListFragment.DJListListeners, IOnBackPressed {
    private lateinit var binding: FragmentMainTestBinding
    private var isDECII = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Declaracion Jurada"
    }

    companion object {
        private const val TAG = "MainDJFragment"
        const val AFFIDAVIT = "SecondDeclaration"
        const val DJ_LIST_TAG = "DJList"
        const val DJ_DETAIL_TAG = "DJDetail"

        fun newInstance(isSecondDeclaration: Boolean = false): MainDJFragment {
            val args = Bundle()
            args.putBoolean(AFFIDAVIT, isSecondDeclaration)
            val fragment = MainDJFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainTestBinding.inflate(inflater, container, false)
        isDECII = arguments?.getBoolean(AFFIDAVIT) ?: false
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
                getString(R.string.want_to_return_report_not_saved),
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
        val api = RestClient.buildAnglo()
        val call = api.getWorkerAptoDJ(SharedUtils.getUsuarioId(activity), wCDate)

        showLoader()
        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<WorkerAptoDJ>>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<ArrayList<WorkerAptoDJ>>>,
                t: Throwable
            ) {
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.no_authorization_to_file_your_report)
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
                                getString(R.string.alert_title),
                                getString(R.string.you_are_not_authorized_to_fill_your_report),
                                getString(R.string.to_close),
                                { dialog, _ ->
                                    dialog.dismiss()
                                }
                            )
                        }
                    } else {
                        dismissLoader()
                        SharedUtils.showToast(
                            requireContext(),
                            getString(R.string.no_authorization_to_file_your_report)
                        )
                        Log.e(TAG, "checkWorkerApto result: $result")
                    }
                } else {
                    dismissLoader()
                    SharedUtils.showToast(
                        requireContext(),
                        getString(R.string.no_authorization_to_file_your_report)
                    )
                    Log.e(TAG, "checkWorkerApto response: $response")
                }
            }
        })
    }

    private fun getControlInicial() {
        val api = RestClient.buildAnglo()
        val call = api.getControlInicial(SharedUtils.getUsuarioId(activity), "null")

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
                        if (data.size > 0) {
                            if (isDECII)
                                evaluateSecondDeclarations(data)
                            else
                                evaluateNormalDeclarations(data)
                        } else
                            initDetailTestFragment()
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
                    initDetailTestFragment(false)
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
                    Log.d(TAG, "control inicial found -> $item")
                    if (getDayCount(
                            item.fecha!!,
                            wCDate,
                            "yyyyMMdd"
                        )!! <= 14 && item.positivo == "SI"
                    )
                        showDeniedMessage()
                    else if (item.positivo == null)
                        initDetailTestFragment(true)
                    else if (item.fecha!! != wCDate)
                        initDetailTestFragment(true)
                    else
                        showNoDups()
                } else
                    SharedUtils.showToast(
                        requireContext(),
                        getString(R.string.not_available_to_fill_report)
                    )
            } else
                SharedUtils.showToast(
                    requireContext(),
                    getString(R.string.no_parent_initial_control)
                )
        }
    }

    private fun showNoDups() {
        dismissLoader()
        showMessage(
            getString(R.string.alert_title),
            getString(R.string.you_cannot_fill_more_than_one_statement_per_day),
            getString(R.string.buttonOk),
            { dialog, _ ->
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
            { dialog, _ ->
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
            val api = RestClient.buildAnglo()
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