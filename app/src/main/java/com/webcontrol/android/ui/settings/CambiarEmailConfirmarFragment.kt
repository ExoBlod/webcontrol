package com.webcontrol.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.webcontrol.android.databinding.FragmentCambiarEmailConfirmarBinding
import com.webcontrol.android.vm.CambiarEmailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CambiarEmailConfirmarFragment : Fragment() {
    private lateinit var binding: FragmentCambiarEmailConfirmarBinding
    val vm: CambiarEmailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCambiarEmailConfirmarBinding.inflate(inflater, container, false)
        //val binding: FragmentCambiarEmailConfirmarBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cambiar_email_confirmar, container, false)
        initViewModel(binding)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        vm.errorCodigo.observe(viewLifecycleOwner, Observer { s: String? -> binding.ilCodigo.error = s })
    }

    private fun initViewModel(binding: FragmentCambiarEmailConfirmarBinding) {
        binding.vm = vm
    }
}