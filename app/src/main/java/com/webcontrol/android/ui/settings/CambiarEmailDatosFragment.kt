package com.webcontrol.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.webcontrol.android.databinding.FragmentCambiarEmailDatosBinding
import com.webcontrol.android.vm.CambiarEmailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CambiarEmailDatosFragment : Fragment() {
    private lateinit var binding: FragmentCambiarEmailDatosBinding
    val vm: CambiarEmailViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentCambiarEmailDatosBinding.inflate(inflater, container, false)
        //val binding: FragmentCambiarEmailDatosBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cambiar_email_datos, container, false)
        initViewModel(binding)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        vm.errorEmail.observe(viewLifecycleOwner, Observer { s: String? -> binding.ilEmail.error = s })
        vm.errorClave.observe(viewLifecycleOwner, Observer { s: String? -> binding.ilClave.error = s })
    }

    private fun initViewModel(binding: FragmentCambiarEmailDatosBinding) {
        binding.vm = vm
    }
}