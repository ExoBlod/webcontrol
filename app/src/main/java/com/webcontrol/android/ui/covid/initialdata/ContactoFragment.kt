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
import com.webcontrol.android.databinding.ContentDatosInicialesContactoBinding
import com.webcontrol.android.ui.covid.DatosInicialesFragment
import com.webcontrol.android.vm.DatosInicialesSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.content_boton_validar.*
import java.util.*

@AndroidEntryPoint
class ContactoFragment : Fragment() {
    private lateinit var binding: ContentDatosInicialesContactoBinding
    val viewModel: DatosInicialesSharedViewModel by viewModels()
    lateinit var datosIniciales: DatosInicialesWorker

    fun newInstance(aaa: String?): DatosInicialesFragment {
        val fragment = DatosInicialesFragment()
        val args = Bundle()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContentDatosInicialesContactoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("prueba contacto", "fragment 2")
        animationView!!.enableMergePathsForKitKatAndAbove(true)
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
        binding.lblEmail.setText(datosIniciales.email)
        binding.lblCelular.setText(datosIniciales.celular)
        binding.lblTelefono.setText(datosIniciales.telefono)
        binding.lblNombreContacto.setText(datosIniciales.contactoFamiliar)
        binding.lblTelefonoContacto.setText(datosIniciales.contactoCelular)

        binding.includeid.btnValidar.setOnClickListener { setBtnValida() }

        binding.lblEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilEmail.isErrorEnabled = false
                val regexEmail: String = getString(R.string.regex_email)
                binding.lblEmail.text?.let {
                    if (!binding.lblEmail.text!!.matches(regexEmail.toRegex())) {
                        binding.tilEmail.error = "Correo Incorrecto"
                        binding.tilEmail.isErrorEnabled = true
                        viewModel.isValid.value = false
                    } else {
                        binding.tilEmail.isErrorEnabled = false
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
                datosIniciales.email = p0.toString().toUpperCase(Locale.ROOT)
                viewModel.user.value!!.email = p0.toString().toUpperCase(Locale.ROOT)
            }
        })
        binding.lblCelular.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilCelular.isErrorEnabled = false
                val regexCelular: String = getString(R.string.regex_cellphone)
                binding.lblCelular.text?.let {
                    if (!binding.lblCelular.text!!.matches(regexCelular.toRegex())) {
                        binding.tilCelular.error = "Correo Incorrecto"
                        binding.tilCelular.isErrorEnabled = true
                        viewModel.isValid.value = false
                    } else {
                        binding.tilCelular.isErrorEnabled = false
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
                datosIniciales.celular = p0.toString()
                viewModel.user.value!!.celular = p0.toString()
            }
        })
        binding.lblTelefono.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilTelefono.isErrorEnabled = false
                datosIniciales.telefono = p0.toString()
                viewModel.user.value!!.telefono = p0.toString()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        })
        binding.lblNombreContacto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilNombreContacto.isErrorEnabled = false
                datosIniciales.contactoFamiliar = p0.toString().toUpperCase(Locale.ROOT)
                viewModel.user.value!!.contactoFamiliar = p0.toString().toUpperCase(Locale.ROOT)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
        })
        binding.lblTelefonoContacto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilTelefonoContacto.isErrorEnabled = false
                datosIniciales.contactoCelular = p0.toString()
                viewModel.user.value!!.contactoCelular = p0.toString()
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

        if (binding.lblEmail.text.isNullOrEmpty()) {
            binding.tilEmail.error = "Campo obligatorio"
            return false
        }
        if (binding.lblCelular.text.isNullOrEmpty()) {
            binding.tilCelular.error = "Campo obligatorio"
            return false
        }

        if (binding.lblTelefono.text.isNullOrEmpty()) {
            binding.tilTelefono.error = "Campo obligatorio"
            return false
        }
        if (binding.lblNombreContacto.text.isNullOrEmpty()) {
            binding.tilNombreContacto.error = "Campo obligatorio"
            return false
        }
        if (binding.lblTelefonoContacto.text.isNullOrEmpty()) {
            binding.tilTelefonoContacto.error = "Campo obligatorio"
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        binding.includeid.btnValidar.visibility = View.VISIBLE
    }
}