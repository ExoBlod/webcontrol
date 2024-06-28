package com.webcontrol.android.ui.covid.antapaccay.initialdata

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.webcontrol.android.R
import com.webcontrol.android.data.model.DatosInicialesWorker
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.databinding.ContentDatosInicialesResidenciaBinding
import com.webcontrol.android.ui.covid.DatosInicialesFragment
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.vm.DatosInicialesSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@AndroidEntryPoint
class ResidenciaFragment : Fragment() {
    private lateinit var binding: ContentDatosInicialesResidenciaBinding
    val viewModel: DatosInicialesSharedViewModel by viewModels()
    private val api = RestClient.buildAnta()
    lateinit var datosIniciales: DatosInicialesWorker
    private val RESIDENCIA = arrayOf(
        "Información de Domicilio", "Lugar donde se hospeda actualmente"
    )

    fun newInstance(aaa: String?): DatosInicialesFragment {
        val fragment = DatosInicialesFragment()
        val args = Bundle()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ContentDatosInicialesResidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeid.animationView.enableMergePathsForKitKatAndAbove(true)
        loadData()
    }

    private fun loadData() {
        viewModel.getUser().observe(viewLifecycleOwner, Observer<DatosInicialesWorker> { user ->
            user?.let {
                datosIniciales = it
                setUIElements()
            }
        })
    }

    private fun setUIElements() {
        binding.includeid.btnValidar.visibility = View.VISIBLE
        datosIniciales.let {
            loadTipoResidencia()
            binding.lblDireccion.setText(datosIniciales.direccion)
            loadPaises()
            datosIniciales.pais?.let {
                binding.spinnerPais.setText(it)
                loadRegiones(it)
            }
            datosIniciales.departamento?.let {
                binding.spinnerRegion.setText(it)
                loadCiudades(it)
            }
            datosIniciales.provincia?.let {
                binding.spinnerProvincia.setText(it)
                loadComunas(it)
            }
            datosIniciales.distrito?.let {
                binding.spinnerDistrito.setText(it)
            }
            setListeners()
        }
    }

    private fun loadPaises() {
        val call = api.getPaises()
        call.enqueue(object : Callback<ApiResponseAnglo<List<Pais>>> {
            override fun onFailure(call: Call<ApiResponseAnglo<List<Pais>>>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<List<Pais>>>,
                response: Response<ApiResponseAnglo<List<Pais>>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.data.isNotEmpty()) {
                            val adapterPais: ArrayAdapter<Pais> = ArrayAdapter(
                                context!!,
                                android.R.layout.simple_dropdown_item_1line,
                                it.data
                            )
                            binding.spinnerPais.setAdapter(adapterPais)
                        }
                    }
                }
            }
        })

        binding.spinnerPais.setOnItemClickListener { _, _, _, _ ->
            if (datosIniciales.pais != binding.spinnerPais.text.toString()) {
                binding.spinnerRegion.setAdapter(null)
                binding.spinnerRegion.text = null
                binding.spinnerProvincia.setAdapter(null)
                binding.spinnerProvincia.text = null
                binding.spinnerDistrito.setAdapter(null)
                binding.spinnerDistrito.text = null

                datosIniciales.pais = binding.spinnerPais.text.toString()
                loadRegiones(binding.spinnerPais.text.toString())
                viewModel.user.value!!.pais = binding.spinnerPais.text.toString()
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        }
    }

    private fun loadRegiones(pais: String) {
        val call = api.getRegiones(pais)
        call.enqueue(object : Callback<ApiResponseAnglo<List<Region>>> {
            override fun onFailure(call: Call<ApiResponseAnglo<List<Region>>>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<List<Region>>>,
                response: Response<ApiResponseAnglo<List<Region>>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.data.isNotEmpty()) {
                            val adapterRegion: ArrayAdapter<Region> = ArrayAdapter(
                                context!!,
                                android.R.layout.simple_dropdown_item_1line,
                                it.data
                            )
                            binding.spinnerRegion.setAdapter(adapterRegion)
                        }
                    }
                }
            }
        })

        binding.spinnerRegion.setOnItemClickListener { _, _, _, _ ->
            if (datosIniciales.departamento != binding.spinnerRegion.text.toString()) {
                binding.spinnerProvincia.setAdapter(null)
                binding.spinnerProvincia.text = null
                binding.spinnerDistrito.setAdapter(null)
                binding.spinnerDistrito.text = null

                datosIniciales.departamento = binding.spinnerRegion.text.toString()
                loadCiudades(binding.spinnerRegion.text.toString())
                viewModel.user.value!!.departamento = binding.spinnerRegion.text.toString()
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        }
    }

    private fun loadCiudades(region: String) {
        val call = api.getCiudades(region)

        call.enqueue(object : Callback<ApiResponseAnglo<List<Ciudad>>> {
            override fun onFailure(call: Call<ApiResponseAnglo<List<Ciudad>>>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<List<Ciudad>>>,
                response: Response<ApiResponseAnglo<List<Ciudad>>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.data.isNotEmpty()) {
                            val adapterCiudad: ArrayAdapter<Ciudad> = ArrayAdapter(
                                context!!,
                                android.R.layout.simple_dropdown_item_1line,
                                it.data
                            )

                            binding.spinnerProvincia.setAdapter(adapterCiudad)
                        }
                    }
                }
            }
        })

        binding.spinnerProvincia.setOnItemClickListener { _, _, _, _ ->
            if (datosIniciales.provincia != binding.spinnerProvincia.text.toString()) {
                binding.spinnerDistrito.setAdapter(null)
                binding.spinnerDistrito.text = null

                datosIniciales.provincia = binding.spinnerProvincia.text.toString()
                loadComunas(binding.spinnerProvincia.text.toString())
                viewModel.user.value!!.provincia = binding.spinnerProvincia.text.toString()
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        }
    }

    private fun loadComunas(ciudad: String) {
        val call = api.getComunas(ciudad)

        call.enqueue(object : Callback<ApiResponseAnglo<List<Comuna>>> {
            override fun onFailure(call: Call<ApiResponseAnglo<List<Comuna>>>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<List<Comuna>>>,
                response: Response<ApiResponseAnglo<List<Comuna>>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.data.isNotEmpty()) {
                            val adapterComuna: ArrayAdapter<Comuna> = ArrayAdapter(
                                context!!,
                                android.R.layout.simple_dropdown_item_1line,
                                it.data
                            )

                            binding.spinnerDistrito.setAdapter(adapterComuna)
                        }
                    }
                }
            }
        })

        binding.spinnerDistrito.setOnItemClickListener { _, _, _, _ ->
            if (datosIniciales.distrito != binding.spinnerDistrito.text.toString()) {
                datosIniciales.distrito = binding.spinnerDistrito.text.toString()
                viewModel.user.value!!.distrito = binding.spinnerDistrito.text.toString()
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        }
    }

    private fun loadTipoResidencia() {
        val adapterResidencia: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, RESIDENCIA)
        datosIniciales.tipoResidencia?.let {
            if (it == "DOMICILIO") binding.spinnerResidencia.setText(RESIDENCIA[0]) else binding.spinnerResidencia.setText(
                RESIDENCIA[1]
            )
        }
        binding.spinnerResidencia.setAdapter(adapterResidencia)
        binding.spinnerResidencia.setOnItemClickListener { adapterView, view, i, l ->
            viewModel.user.value!!.tipoResidencia =
                if (RESIDENCIA[i] == RESIDENCIA[0]) "DOMICILIO" else "HOSPEDAJE"
            datosIniciales.tipoResidencia = viewModel.user.value!!.tipoResidencia
            binding.includeid.btnValidar.visibility = View.VISIBLE
            viewModel.setIsValid(false)
        }
    }

    private fun setListeners() {
        binding.includeid.btnValidar.setOnClickListener { setBtnValida() }
        binding.lblDireccion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilDireccion.isErrorEnabled = false
                datosIniciales.direccion = p0.toString().toUpperCase(Locale.ROOT)
                viewModel.user.value!!.direccion = p0.toString().toUpperCase(Locale.ROOT)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        })
    }

    private fun setBtnValida() {
        binding.includeid.animationView.setAnimation(R.raw.loader_rest)
        binding.includeid.animationView.visibility = View.VISIBLE
        binding.includeid.animationView.repeatCount = ValueAnimator.INFINITE
        binding.includeid.animationView.playAnimation()
        if (validate()) {
            viewModel.setIsValid(true)
            binding.includeid.animationView.pauseAnimation()
            binding.includeid.animationView.setAnimation(R.raw.spinner_done)
            binding.includeid.animationView.repeatCount = 0
            binding.includeid.animationView.playAnimation()
            binding.includeid.animationView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    binding.includeid.btnValidar.visibility = View.INVISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationCancel(animation: Animator) {
                    binding.includeid.btnValidar.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
        } else {
            binding.includeid.animationView.setAnimation(R.raw.spinner_error)
            binding.includeid.animationView.repeatCount = 0
            binding.includeid.animationView.playAnimation()
            binding.includeid.animationView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    binding.includeid.btnValidar.visibility = View.INVISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationCancel(animation: Animator) {
                    binding.includeid.btnValidar.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun validate(): Boolean {

        if (binding.spinnerResidencia.text.isNullOrEmpty()) {
            binding.tilResidencia.error = getString(R.string.required_fields)
            return false
        }
        if (binding.lblDireccion.text.isNullOrEmpty()) {
            binding.tilDireccion.error = getString(R.string.required_fields)
            return false
        }
        if (binding.spinnerPais.text.isNullOrEmpty()) {
            binding.tilPais.error = getString(R.string.required_fields)
            return false
        }

        if (binding.spinnerRegion.text.isNullOrEmpty()) {
            binding.tilDepartamento.error = getString(R.string.required_fields)
            return false
        }
        if (binding.spinnerProvincia.text.isNullOrEmpty()) {
            binding.tilProvincia.error = getString(R.string.required_fields)
            return false
        }
        if (binding.spinnerDistrito.text.isNullOrEmpty()) {
            binding.tilDistrito.error = getString(R.string.required_fields)
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        binding.includeid.btnValidar.visibility = View.VISIBLE
    }
}