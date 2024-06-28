package com.webcontrol.android.ui.covid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.kofigyan.stateprogressbar.StateProgressBar
import com.webcontrol.android.R
import com.webcontrol.android.data.model.ControlInicial
import com.webcontrol.android.data.model.DatosInicialesWorker
import com.webcontrol.android.data.model.WorkerPase
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.FragmentCovidDatosInicialesBinding
import com.webcontrol.android.ui.common.adapters.ViewPagerAdapter
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.DatosInicialesSharedViewModel
import com.webcontrol.android.vm.DatosInicialesViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class DatosInicialesFragment : Fragment() {
    private lateinit var binding: FragmentCovidDatosInicialesBinding
    val viewModel: DatosInicialesViewModel by viewModels()
    val sharedViewModel: DatosInicialesSharedViewModel by viewModels()
    lateinit var datosIniciales: DatosInicialesWorker
    private var controlInicial = ControlInicial()
    private var mensajeFinal: String? = ""
    private var razonBloqueo: String? = ""
    private var title: String? = ""

    companion object {
        private val BLOQUEO_POR_EDAD: Int = 0
        private val BLOQUEO_POR_ANTECEDENTES: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title=("Datos Iniciales")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //val root: View = inflater.inflate(R.layout.fragment_covid_datos_iniciales, container, false)
        binding = FragmentCovidDatosInicialesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        setViewPager()
    }

    private fun setViewPager() {
        binding.viewpager.adapter = ViewPagerAdapter(requireActivity())
        binding.viewpager.isUserInputEnabled = false
        binding.btnAnterior.isEnabled = false
        binding.btnSiguiente.setOnClickListener {
            if (binding.btnSiguiente.text == "FINALIZAR") {
                sharedViewModel.getUser().observe(viewLifecycleOwner) { user ->
                    user?.let {
                        datosIniciales = it
                        guardarDatos()
                    }
                }
            } else {
                if (validarDatos()) {
                    binding.viewpager.currentItem = binding.viewpager.currentItem + 1
                    sharedViewModel.setIsValid(false)
                } else {
                    SharedUtils.showToast(requireContext(), "Los datos deben estar correctamente validados")
                }
            }
        }
        binding.btnAnterior.setOnClickListener {
            if (validarDatos()) {
                binding.viewpager.currentItem = binding.viewpager.currentItem - 1
                sharedViewModel.setIsValid(false)
            } else
                if (binding.viewpager.currentItem == 4) {
                    binding.viewpager.currentItem = binding.viewpager.currentItem - 1
                    sharedViewModel.setIsValid(false)
                } else
                    SharedUtils.showToast(requireContext(), "Los datos deben estar correctamente validados")
        }
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setProgressBar(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun loadData() {
        SharedUtils.showLoader(requireContext(), "Cargando datos...")
        viewModel.getDataInicial(SharedUtils.getUsuarioId(requireContext()))
        viewModel.getDataInicialResult().observe(viewLifecycleOwner) { user ->
            SharedUtils.dismissLoader(context)
            sharedViewModel.setUser(user)
            sharedViewModel.setCustomer("AA")
            sharedViewModel.setAntecedentes(user.listAntecedentes)
        }
        viewModel.getDataInicialError().observe(viewLifecycleOwner) { it ->
            SharedUtils.dismissLoader(context)
            SharedUtils.showToast(requireContext(), it)
        }
    }

    private fun guardarDatos() {
        title = ""
        mensajeFinal = "Datos Actualizados correctamente"
        var edad: Int? = 0
        sharedViewModel.user.value!!.fechaNacimiento.let {
            edad = SharedUtils.getUserAge(it!!)
        }
        if (edad != null) {
            if (edad!! >= 60) {
                bloquearPase(BLOQUEO_POR_EDAD)
            }
        }
        if (isBlocked()) {
            bloquearPase(BLOQUEO_POR_ANTECEDENTES)
        }
        SharedUtils.showLoader(requireContext(), "Actualizando Datos...")
        viewModel.updateDataInicial(sharedViewModel.getUser().value!!)
        viewModel.getDataUpdateResult().observe(viewLifecycleOwner) {
            getControlInicial(SharedUtils.getUsuarioId(context))
            SharedUtils.dismissLoader(requireContext())
        }
        viewModel.getDataUpdateError().observe(viewLifecycleOwner) {
            SharedUtils.dismissLoader(requireContext())
            //SharedUtils.showToast(requireContext(), it)
        }
    }

    private fun validarDatos(): Boolean {
        var isValid = true
        sharedViewModel.getIsValid().observe(viewLifecycleOwner) {
            isValid = it
        }
        return isValid
    }

    private fun bloquearPase(tipo: Int) {
        if (tipo == BLOQUEO_POR_EDAD) {
            title = "BLOQUEO POR EDAD"
            mensajeFinal = getString(R.string.mensaje_coronavirus_edad)
            razonBloqueo = getString(R.string.reason_coronavirus_edad)
        }
        if (tipo == BLOQUEO_POR_ANTECEDENTES) {
            title = "BLOQUEO POR ANTECEDENTES"
            mensajeFinal = getString(R.string.mensaje_coronavirus_antecedentes)
            razonBloqueo = getString(R.string.reason_coronavirus_test)
        }
        val workerPase = WorkerPase()
        workerPase.WorkerId = SharedUtils.getUsuarioId(context)
        workerPase.Reason = razonBloqueo
        workerPase.State = "BLOQUEADO"
        workerPase.Date = SharedUtils.wCDate
        workerPase.Time = SharedUtils.time
        viewModel.blockPass(workerPase)
        viewModel.blockPassResult.observe(viewLifecycleOwner) {
        }
        viewModel.blockPassError.observe(viewLifecycleOwner) {
        }
    }

    private fun isBlocked(): Boolean {
        var listAntecedentes = sharedViewModel.user.value!!.listAntecedentes
        for (i in listAntecedentes.indices) {
            if (listAntecedentes[i].tipo == "SWITCH" && listAntecedentes[i].isChecked == true) {
                return true
            }
            if (listAntecedentes[i].tipo == "TXT") {
                if (listAntecedentes[i].comentario != null && listAntecedentes[i].comentario!!.isNotEmpty())
                    return true
            }
        }
        return false
    }

    private fun getControlInicial(rut: String) {
        val api = RestClient.buildAnglo()
        val call = api.getControlInicial(rut, "null")
        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<ControlInicial>>> {
            override fun onFailure(call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>, t: Throwable) {
                SharedUtils.showToast(requireContext(), "No se pudo obtener el histórico de control inicial.")
            }

            override fun onResponse(call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>, response: Response<ApiResponseAnglo<ArrayList<ControlInicial>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        val data = result.data
                        if (data.size > 0) {
                            controlInicial = data[0]
                            updateControlInicial()
                        } else
                            insertControlInicial()
                    } else
                        SharedUtils.showToast(requireContext(), "Usted no tiene un control inicial asociado.")
                } else
                    SharedUtils.showToast(requireContext(), "Ocurrio un error al consultar su control inicial.")
            }
        })
    }

    private fun insertControlInicial() {
        val controlInicial = ControlInicial(
                rut = SharedUtils.getUsuarioId(activity),
                fecha = SharedUtils.wCDate,
                hora = SharedUtils.time,
                empresa = SharedUtils.getCompanyCheckList(activity),
                division = SharedUtils.getWorkerDivision(activity),
                inicial = "SI"
        )
        val api = RestClient.buildAnglo()
        val call = api.insertControlInicial(arrayListOf(controlInicial))

        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                SharedUtils.showToast(requireContext(), "No se pudo obtener el histórico de control inicial.")
            }

            override fun onResponse(call: Call<ApiResponseAnglo<String>>, response: Response<ApiResponseAnglo<String>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        MaterialDialog.Builder(requireContext())
                                .title(title!!)
                                .iconRes(R.drawable.ic_check)
                                .content(mensajeFinal!!)
                                .positiveText("OK")
                                .autoDismiss(true)
                                .cancelable(false)
                                .onPositive { dialog, which ->
                                    dialog.dismiss()
                                    returnToMainFragment()
                                }
                                .show()

                    } else
                        SharedUtils.showToast(requireContext(), "Usted no tiene un control inicial asociado.")
                } else
                    SharedUtils.showToast(requireContext(), "Ocurrio un error al consultar su control inicial.")
            }
        })
    }

    private fun updateControlInicial() {
        SharedUtils.showLoader(requireContext(), "Verificando Control Inicial...")
        controlInicial.inicial = "SI"
        Log.i("CI", controlInicial.toString())
        val api = RestClient.buildAnglo()
        val call = api.updateControlInicial(arrayListOf(controlInicial))
        call.enqueue(object : Callback<ApiResponseAnglo<String>> {
            override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                SharedUtils.dismissLoader(requireContext())
                Snackbar.make(requireView(), "No se pudo guardar", Snackbar.LENGTH_LONG).setAction("Reintentar") { guardarDatos() }.show()
            }

            override fun onResponse(call: Call<ApiResponseAnglo<String>>, response: Response<ApiResponseAnglo<String>>) {
                SharedUtils.dismissLoader(requireContext())
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        MaterialDialog.Builder(requireContext())
                                .title(title!!)
                                .iconRes(R.drawable.ic_check)
                                .content(mensajeFinal!!)
                                .positiveText("OK")
                                .autoDismiss(true)
                                .cancelable(false)
                                .onPositive { dialog, which ->
                                    dialog.dismiss()
                                    returnToMainFragment()
                                }
                                .show()
                    }
                }
            }
        })
    }

    private fun returnToMainFragment() {
        findNavController().navigate(R.id.mainDJFragment, bundleOf("Declaracion Jurada" to "Declaracion Jurada"))
    }

    fun setProgressBar(position: Int) {
        binding.btnAnterior.isEnabled = true
        binding.btnSiguiente.text = "SIGUIENTE"
        if (position == 0) {
            binding.btnAnterior.isEnabled = false
        }
        if (position == 4) {
            binding.btnSiguiente.text = "FINALIZAR"
        }
        when (position) {
            0 -> {
                binding.progressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
            }
            1 -> {
                binding.progressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
            }
            2 -> {
                binding.progressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
            }
            3 -> {
                binding.progressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
            }
            4 -> {
                binding.progressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE)
            }
        }
    }
}
