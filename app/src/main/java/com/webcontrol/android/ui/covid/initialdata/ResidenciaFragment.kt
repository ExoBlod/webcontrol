package com.webcontrol.android.ui.covid.initialdata

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
import androidx.lifecycle.ViewModelProviders
import com.webcontrol.android.R
import com.webcontrol.android.data.model.*
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
    val  viewModel: DatosInicialesSharedViewModel by viewModels()
    lateinit var datosIniciales: DatosInicialesWorker
    private val api = RestClient.buildAnglo()
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
        viewModel.getUser().observe(viewLifecycleOwner) { user ->
            user?.let {
                datosIniciales = it
                setUIElements()
            }
        }
    }

    private fun setUIElements() {
        binding.includeid.btnValidar.visibility = View.VISIBLE
        datosIniciales.let {
            loadTipoResidencia()
            loadPaises()
            binding.lblDireccion.setText(datosIniciales.direccion)
            datosIniciales.pais?.let {
                binding.spinnerPais.setText(it)
                loadRegiones(it)
            }
            datosIniciales.departamentoId?.let { loadCiudades(it) }
            datosIniciales.provincia?.let {
                binding.spinnerProvincia.setText(it)
                loadComunas(it)
            }
            datosIniciales.departamento?.let {  binding.spinnerRegion.setText(it) }
            datosIniciales.distrito?.let { binding.spinnerDistrito.setText(it) }
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
            binding.includeid.btnValidar.setOnClickListener { setBtnValida() }
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

    private fun loadPaises() {
        val call = api.paises()
        call.enqueue(object : Callback<ApiResponseAnglo<List<Pais>>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<List<Pais>>>,
                response: Response<ApiResponseAnglo<List<Pais>>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.data.isNotEmpty()) {
                        val adapterPais: ArrayAdapter<Pais> = ArrayAdapter(
                            context!!,
                            android.R.layout.simple_dropdown_item_1line,
                            response.body()!!.data
                        )
                        binding.spinnerPais.setAdapter(adapterPais)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<List<Pais>>>, t: Throwable) {
                t.printStackTrace()
            }
        })
        binding.spinnerPais.setOnItemClickListener { _, _, _, _ ->
            loadRegiones(binding.spinnerPais.text.toString())
            datosIniciales.pais = binding.spinnerPais.text.toString()
            viewModel.user.value!!.pais = binding.spinnerPais.text.toString()
            binding.includeid.btnValidar.visibility = View.VISIBLE
            viewModel.setIsValid(false)
        }
    }

    private fun loadRegiones(pais: String) {
        binding.tilDepartamento.error = null
        binding.spinnerRegion.setAdapter(null)
        binding.spinnerRegion.text = null
        binding.spinnerProvincia.setAdapter(null)
        binding.spinnerProvincia.text = null
        binding.spinnerDistrito.setAdapter(null)
        binding.spinnerDistrito.text = null
        val call = api.getRegiones(pais)
        var listRegiones: List<RegionPais>? = null
        call.enqueue(object : Callback<ApiResponseAnglo<List<RegionPais>>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<List<RegionPais>>>,
                response: Response<ApiResponseAnglo<List<RegionPais>>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.data.isNotEmpty()) {
                        listRegiones = response.body()!!.data
                        val adapterRegion: ArrayAdapter<RegionPais> = ArrayAdapter(
                            context!!,
                            android.R.layout.simple_dropdown_item_1line,
                            response.body()!!.data
                        )
                        binding.spinnerRegion.setAdapter(adapterRegion)
                    } else {
                        binding.tilDepartamento.error = getString(R.string.no_registered_departments)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<List<RegionPais>>>, t: Throwable) {
                t.printStackTrace()
            }
        })
        binding.spinnerRegion.setOnItemClickListener { _, _, position, _ ->
            loadCiudades(listRegiones!![position].region!!)
            datosIniciales.departamento = binding.spinnerRegion.text.toString()
            viewModel.user.value!!.departamento = binding.spinnerRegion.text.toString()
            viewModel.user.value!!.departamentoId = listRegiones!![position].region!!
            binding.includeid.btnValidar.visibility = View.VISIBLE
            viewModel.setIsValid(false)
        }
    }

    private fun loadCiudades(region: String) {
        binding.spinnerProvincia.text = null
        binding.spinnerDistrito.text = null
        val call = api.getCiudades(region)
        call.enqueue(object : Callback<ApiResponseAnglo<List<CiudadRegion>>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<List<CiudadRegion>>>,
                response: Response<ApiResponseAnglo<List<CiudadRegion>>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.data.isNotEmpty()) {
                        val adapterCiudad: ArrayAdapter<CiudadRegion> = ArrayAdapter(
                            context!!,
                            android.R.layout.simple_dropdown_item_1line,
                            response.body()!!.data
                        )
                        binding.spinnerProvincia.setAdapter(adapterCiudad)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<List<CiudadRegion>>>, t: Throwable) {
                t.printStackTrace()
            }
        })

        binding.spinnerProvincia.setOnItemClickListener { _, _, _, _ ->
            loadComunas(binding.spinnerProvincia.text.toString())
            datosIniciales.provincia = binding.spinnerProvincia.text.toString()
            viewModel.user.value!!.provincia = binding.spinnerProvincia.text.toString()
            binding.includeid.btnValidar.visibility = View.VISIBLE
            viewModel.setIsValid(false)
        }
    }

    private fun loadComunas(ciudad: String) {
        binding.spinnerDistrito.text = null
        val call = api.getComunas(ciudad)
        call.enqueue(object : Callback<ApiResponseAnglo<List<ComunaCiudad>>> {
            override fun onResponse(
                call: Call<ApiResponseAnglo<List<ComunaCiudad>>>,
                response: Response<ApiResponseAnglo<List<ComunaCiudad>>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.data.isNotEmpty()) {
                        val adapterComuna: ArrayAdapter<ComunaCiudad> = ArrayAdapter(
                            context!!,
                            android.R.layout.simple_dropdown_item_1line,
                            response.body()!!.data
                        )
                        binding.spinnerDistrito.setAdapter(adapterComuna)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseAnglo<List<ComunaCiudad>>>, t: Throwable) {
                t.printStackTrace()
            }
        })

        binding.spinnerDistrito.setOnItemClickListener { _, _, _, _ ->
            datosIniciales.distrito = binding.spinnerDistrito.text.toString()
            viewModel.user.value!!.distrito = binding.spinnerDistrito.text.toString()
            binding.includeid.btnValidar.visibility = View.VISIBLE
            viewModel.setIsValid(false)
        }
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