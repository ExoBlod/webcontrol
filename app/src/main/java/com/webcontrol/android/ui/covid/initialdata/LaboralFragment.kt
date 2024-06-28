package com.webcontrol.android.ui.covid.initialdata

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.webcontrol.android.R
import com.webcontrol.android.data.model.DatosInicialesWorker
import com.webcontrol.android.databinding.ContentDatosInicialesLaboralBinding
import com.webcontrol.android.ui.covid.DatosInicialesFragment
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.DatosInicialesSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class LaboralFragment : Fragment() {
    private lateinit var binding: ContentDatosInicialesLaboralBinding
    val viewModel: DatosInicialesSharedViewModel by viewModels()
    private var cliente: String? = ""
    lateinit var datosIniciales: DatosInicialesWorker

    fun newInstance(aaa: String?): DatosInicialesFragment {
        val fragment = DatosInicialesFragment()
        val args = Bundle()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContentDatosInicialesLaboralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("prueba laboral", "fragment 4")
        binding.includeid.animationView.enableMergePathsForKitKatAndAbove(true)
        loadData()
    }

    private fun loadData() {
        viewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                datosIniciales = user
                setUIElements()
            } else
                SharedUtils.showToast(requireContext(), "null")
        }
    }

    private fun setUIElements() {
        viewModel.getCustomer().observe(viewLifecycleOwner) {
            cliente = it
        }
        binding.includeid.btnValidar.visibility = View.VISIBLE
        if (cliente.equals("AA")) {
            binding.tilTipoSeguro.visibility = View.VISIBLE
            binding.tilIngresoTipoSeguro.visibility = View.VISIBLE
            binding.lblTipoSeguro.setText(datosIniciales.tipoSeguro)
            binding.lblIngresoTipoSeguro.setText(datosIniciales.ingresoSeguro)
        }
        binding.lblProfesion.setText(datosIniciales.ocupacion)
        binding.lblEmpresa.setText(datosIniciales.companiaId)
        binding.includeid.btnValidar.setOnClickListener {
            setBtnValida()
        }

        binding.lblTipoSeguro.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilTipoSeguro.isErrorEnabled = false
                datosIniciales.tipoSeguro = p0.toString().toUpperCase(Locale.ROOT)
                viewModel.user.value!!.tipoSeguro = p0.toString().toUpperCase(Locale.ROOT)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        })
        binding.lblIngresoTipoSeguro.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilIngresoTipoSeguro.isErrorEnabled = false
                datosIniciales.ingresoSeguro = p0.toString().toUpperCase(Locale.ROOT)
                viewModel.user.value!!.ingresoSeguro = p0.toString().toUpperCase(Locale.ROOT)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        })
        binding.lblProfesion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilProfesion.isErrorEnabled = false
                datosIniciales.ocupacion = p0.toString().toUpperCase(Locale.ROOT)
                viewModel.user.value!!.ocupacion = p0.toString().toUpperCase(Locale.ROOT)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        })
        binding.lblEmpresa.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilEmpresa.isErrorEnabled = false
                datosIniciales.companiaId = p0.toString().toUpperCase(Locale.ROOT)
                viewModel.user.value!!.companiaId = p0.toString().toUpperCase(Locale.ROOT)
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
        if (cliente.equals("AA")) {
            if (binding.lblTipoSeguro.text.isNullOrEmpty()) {
                binding.tilTipoSeguro.error = "Campo obligatorio"
                return false
            }
            if (binding.lblIngresoTipoSeguro.text.isNullOrEmpty()) {
                binding.tilIngresoTipoSeguro.error = "Campo obligatorio"
                return false
            }
        }
        if (binding.lblProfesion.text.isNullOrEmpty()) {
            binding.tilProfesion.error = "Campo obligatorio"
            return false
        }
        if (binding.lblEmpresa.text.isNullOrEmpty()) {
            binding.tilEmpresa.error = "Campo obligatorio"
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        binding.includeid.btnValidar.visibility = View.VISIBLE
    }
}