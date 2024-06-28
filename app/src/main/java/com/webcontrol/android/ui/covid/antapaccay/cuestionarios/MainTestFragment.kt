package com.webcontrol.android.ui.covid.antapaccay.cuestionarios

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.model.ControlCuarentena
import com.webcontrol.android.data.model.ControlInicial
import com.webcontrol.android.data.model.CuarentenaDetalle
import com.webcontrol.android.data.model.Cuestionario
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentMainTestBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.util.*
import com.webcontrol.android.util.SharedUtils.compareDays
import com.webcontrol.android.util.SharedUtils.dismissLoader
import com.webcontrol.android.util.SharedUtils.showLoader
import com.webcontrol.android.util.SharedUtils.showToast
import com.webcontrol.android.util.SharedUtils.wCDate
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

@AndroidEntryPoint
class MainTestFragment : Fragment(), TestListFragment.TestListListeners, IOnBackPressed {
    private lateinit var binding: FragmentMainTestBinding
    private lateinit var testFormat: String
    private var id: String?= ""

    companion object {
        private const val TAG = "MainTestFragment"
        private const val TEST_FORMAT = "testFormat"
        const val TEST_LIST_TAG = "testList"
        const val TEST_DETAIL_TAG = "detailTest"

        fun newInstance(testFormat: String): MainTestFragment {
            val args = Bundle()
            args.putString(TEST_FORMAT, testFormat)
            val fragment = MainTestFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id=requireArguments().getString("id")
        requireActivity().title="Fichas de seguimiento"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainTestBinding.inflate(inflater, container, false)
        testFormat = requireArguments().getString(TEST_FORMAT, "")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            addFragment(
                childFragmentManager,
                R.id.root_layout,
                TestListFragment.newInstance(testFormat),
                TEST_LIST_TAG
            )
        }
        if (testFormat == "F200")
            getControlInicial()
    }

    override fun onBackPressed(): Boolean {
        val childFragment = childFragmentManager.findFragmentByTag(TEST_DETAIL_TAG)
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
                        TestListFragment.newInstance(testFormat),
                        TEST_LIST_TAG
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
        getControlInicial()
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
                showToast(
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
                        showToast(
                            requireContext(),
                            getString(R.string.failed_to_get_initial_control_historico)
                        )
                        Log.e(TAG, "getControlInicialResult result: $result")

                        dismissLoader(activity)
                    }

                } else {
                    showToast(
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
                showToast(
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
                            if (compareDays(
                                    controlCuarentena.fechaIni!!,
                                    wCDate,
                                    "yyyyMMdd"
                                )!! >= 0
                            ) {
                                showToast(
                                    requireContext(),
                                    getString(R.string.not_have_an_associated_quarantine_control)
                                )
                                dismissLoader(activity)
                            } else {
                                if (testFormat == "F300")
                                    getHistoricoCuarentena(item, controlCuarentena)
                                else
                                    initDetailTestFragment(item, controlCuarentena)
                            }
                        } else {
                            dismissLoader(activity)
                            showMessage(
                                getString(R.string.title_dialog_alert),
                                getString(R.string.not_have_an_associated_quarantine_control),
                                getString(R.string.buttonOk),
                                MaterialDialog.SingleButtonCallback { dialog, _ ->
                                    dialog.dismiss()
                                    returnToMainFragment()
                                }
                            )
                            Log.e(TAG, "getControlCuarentena dataxx -> $data")
                        }
                    } else {
                        showToast(
                            requireContext(),
                            getString(R.string.error_ocurred_when_consulting_quarantine_control)
                        )
                        Log.e(TAG, "getControlCuarentena result -> $result")
                        dismissLoader(activity)
                    }

                } else {
                    showToast(
                        requireContext(),
                        getString(R.string.error_ocurred_when_consulting_quarantine_control)
                    )
                    Log.e(TAG, "getControlCuarentena response -> $response")
                    dismissLoader(activity)
                }
            }

        })
    }

    private fun returnToMainFragment() {
        findNavController().navigate(R.id.mensajesFragment)
    }

    private fun getHistoricoCuarentena(
        controlInicial: ControlInicial,
        controlCuarentena: ControlCuarentena
    ) {
        val api = RestClient.buildAnta()
        val call = api.getCuarentenaDetalle(controlCuarentena.codigo)

        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>>,
                t: Throwable
            ) {
                dismissLoader(activity)
                showToast(requireContext(), getString(R.string.failed_get_quarantine_history))
                Log.e(TAG, "getHistoricoCuarentena onFailure -> ${t.message}")
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>>,
                response: Response<ApiResponseAnglo<ArrayList<CuarentenaDetalle>>>
            ) {

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if (data.size > 0) {
                            Log.i(TAG, "historico cuarentena detalle data -> $data")
                            if (data[0].fecha != wCDate)
                                initDetailTestFragment(controlInicial, controlCuarentena)
                            else {
                                showMessage(
                                    getString(R.string.title_dialog_alert),
                                    getString(R.string.can_only_complete_one_tracking_sheet_per_day),
                                    getString(R.string.buttonOk),
                                    MaterialDialog.SingleButtonCallback { dialog, _ ->
                                        dialog.dismiss()
                                        dismissLoader(activity)
                                    })
                            }
                        } else
                            initDetailTestFragment(controlInicial, controlCuarentena)
                    } else {
                        dismissLoader(activity)
                        showToast(
                            requireContext(),
                            getString(R.string.error_ocurred_when_consulting_daily_quarantine)
                        )
                    }
                } else {
                    dismissLoader(activity)
                    showToast(
                        requireContext(),
                        getString(R.string.error_ocurred_when_consulting_daily_quarantine)
                    )
                }
            }

        })
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

    private fun initDetailTestFragment(inicial: ControlInicial, cuarentena: ControlCuarentena) {
        try {
            val api = RestClient.buildAnta()
            val call = api.getCuestionarioByFormat(testFormat)

            call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<Cuestionario>>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<ArrayList<Cuestionario>>>,
                    response: Response<ApiResponseAnglo<ArrayList<Cuestionario>>>
                ) {

                    dismissLoader(activity)

                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            val data = result.data
                            if (data.isNotEmpty()) {
                                replaceFragment(
                                    childFragmentManager,
                                    R.id.root_layout,
                                    TestDetailFragment.newInstance(
                                        Gson().toJson(data),
                                        testFormat,
                                        inicial,
                                        cuarentena
                                    ),
                                    TEST_DETAIL_TAG
                                )
                            } else {
                                Log.e(
                                    TAG,
                                    "initDetailTestFragment, no existen preguntas para el formato $testFormat"
                                )
                                showToast(
                                    requireContext(),
                                    getString(R.string.not_get_test_no_questions)
                                )
                            }
                        } else {
                            Log.e(TAG, "initDetailTestFragment, result: $result")
                            showToast(requireContext(), getString(R.string.not_get_test))
                        }
                    } else {
                        Log.e(
                            TAG,
                            "initDetailTestFragment, response not successful, reason: ${response.message()}"
                        )
                        showToast(requireContext(), getString(R.string.not_get_test))
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponseAnglo<ArrayList<Cuestionario>>>,
                    t: Throwable
                ) {

                    dismissLoader(activity)

                    Log.e(TAG, "initDetailTestFragment, call failure: ${t.message}", t)
                    showToast(requireContext(), getString(R.string.not_get_test))
                }
            })
        } catch (ex: Exception) {
            dismissLoader(activity)
            Log.e(TAG, "initDetailTestFragment, reason: ${ex.message}", ex)
            showToast(requireContext(), getString(R.string.not_get_test))
        }
    }


}