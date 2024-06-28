package com.webcontrol.android.ui.covid.initialdata

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.webcontrol.android.R
import com.webcontrol.android.data.model.DatosInicialesWorker
import com.webcontrol.android.databinding.ContentDatosInicialesDatospersonalesBinding
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.vm.DatosInicialesSharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalesFragment : Fragment() {
    private lateinit var binding: ContentDatosInicialesDatospersonalesBinding
    val viewModel: DatosInicialesSharedViewModel by viewModels()
    lateinit var datosIniciales: DatosInicialesWorker
    private val TIPOID = arrayOf(
            "DNI", "CARNET EXTRANJERÍA", "PASAPORTE"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContentDatosInicialesDatospersonalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeid.animationView.enableMergePathsForKitKatAndAbove(true)
        loadData()
    }

    private fun loadData() {
        viewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                datosIniciales = user
                viewModel.setIsValid(false)
                setUIElements()
            } else
                SharedUtils.showToast(requireContext(), "null")
        }
    }

    private fun setUIElements() {
        binding.includeid.btnValidar.visibility = View.VISIBLE
        datosIniciales.let {
            val adapterId: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, TIPOID)
            datosIniciales.tipoDoc?.let {
                binding.spinnerTipoId.setText(it)
                if (it == "CE") binding.spinnerTipoId.setText(TIPOID[1]) else if (it == "PAS") binding.spinnerTipoId.setText(TIPOID[2])
            }
            binding.spinnerTipoId.setAdapter(adapterId)
            binding.lblId.setText(datosIniciales.rut)
            binding.lblNombres.setText(datosIniciales.nombre)
            val index = datosIniciales.apellidos!!.indexOf(" ")
            if (index > -1) {
                binding.lblApellidoPaterno.setText(datosIniciales.apellidos!!.substring(0, index))
                binding.lblApellidoMaterno.setText(datosIniciales.apellidos!!.substring(index + 1))
            } else {
                binding.lblApellidoPaterno.setText(datosIniciales.apellidos)
            }
            binding.lblNacionalidad.setText(datosIniciales.nacionalidad)
            binding.lblSexo.setText(datosIniciales.sexo)
            datosIniciales.fechaNacimiento?.let {
                binding.lblFechaNacimiento.setText(SharedUtils.getNiceDate(datosIniciales.fechaNacimiento))
                binding.lblEdad.setText(SharedUtils.getUserAge(datosIniciales.fechaNacimiento!!).toString())
            }
            binding.spinnerTipoId.setOnItemClickListener { adapterView, view, i, l ->
                datosIniciales.tipoDoc = binding.spinnerTipoId.text.toString()
                viewModel.user.value!!.tipoDoc = if (binding.spinnerTipoId.text.toString() == TIPOID[0]) "DNI" else if (binding.spinnerTipoId.text.toString() == TIPOID[1]) "CE" else "PAS"
                binding.includeid.btnValidar.visibility = View.VISIBLE
                viewModel.setIsValid(false)
            }
            binding.includeid.btnValidar.setOnClickListener { setBtnValida() }
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

        if (binding.spinnerTipoId.text.isNullOrEmpty()) {
            binding.tilTipoId.error = getString(R.string.required_fields)
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        binding.includeid.btnValidar.visibility = View.VISIBLE
    }
}
