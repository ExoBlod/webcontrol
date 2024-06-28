package com.webcontrol.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.webcontrol.android.databinding.FragmentCambiarTelefonoDatosBinding
import com.webcontrol.android.vm.CambiarTelefonoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CambiarTelefonoDatosFragment : Fragment() {
    private lateinit var binding: FragmentCambiarTelefonoDatosBinding
    val vm: CambiarTelefonoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCambiarTelefonoDatosBinding.inflate(inflater, container, false)
        //val binding: FragmentCambiarTelefonoDatosBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cambiar_telefono_datos, container, false)
        initViewModel(binding)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.zipCodePicker.setAutoDetectedCountry(true)
        binding.zipCodePicker.setOnCountryChangeListener { vm.extension.setValue(binding.zipCodePicker.selectedCountryCode) }
        if (vm.extension.value != null && vm.extension.value != "") binding.zipCodePicker.setCountryForPhoneCode(
            vm.extension.value!!.toInt()
        ) else vm.extension.setValue(binding.zipCodePicker.selectedCountryCode)
        vm.errorTelefono.observe(
            viewLifecycleOwner
        ) { s: String? -> binding.phoneNumberInputLayout.error = s }
        vm.errorClave.observe(
            viewLifecycleOwner
        ) { s: String? -> binding.ilClave.error = s }
    }

    private fun initViewModel(binding: FragmentCambiarTelefonoDatosBinding) {
        binding.vm = vm
    }
}