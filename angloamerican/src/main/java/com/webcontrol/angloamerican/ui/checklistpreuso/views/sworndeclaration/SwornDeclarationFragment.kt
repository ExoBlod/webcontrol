package com.webcontrol.angloamerican.ui.checklistpreuso.views.sworndeclaration

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.FragmentPreUsoSwornStatementBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.AppDataBase
import com.webcontrol.angloamerican.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class SwornDeclarationFragment : BaseFragment<FragmentPreUsoSwornStatementBinding, SwornDeclarationViewModel>() {

    private val database: AppDataBase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Declaracion Jurada"
    }
    override fun getViewModelClass() = SwornDeclarationViewModel::class.java
    override fun getViewBinding() =  FragmentPreUsoSwornStatementBinding.inflate (layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAcceptStatement.setOnClickListener {
            val hasSignatureRegistered = database.checkListPreUso().getSignature().isNotEmpty()
            if(hasSignatureRegistered)
                findNavController().navigate(R.id.action_swornDeclarationPreUsoFragment_to_inputDataPreUsoFragment)
            else {
                findNavController().navigate(R.id.action_swornDeclarationPreUsoFragment_to_signaturePreUsoFragment)
            }
        }
    }
}