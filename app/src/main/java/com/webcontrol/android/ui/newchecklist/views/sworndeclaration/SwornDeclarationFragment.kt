package com.webcontrol.android.ui.newchecklist.views.sworndeclaration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentInspectionHistoryBinding
import com.webcontrol.android.databinding.FragmentSwornStatementBinding
import com.webcontrol.android.databinding.FragmentTestChecklistVehicularInspectionBinding
import com.webcontrol.android.ui.newchecklist.NewCheckListViewModel
import com.webcontrol.android.ui.newchecklist.data.NewCheckListScope
import com.webcontrol.android.ui.newchecklist.data.ScopesChecklist
import com.webcontrol.android.ui.newchecklist.views.history.HistoryChecklistViewModel
import com.webcontrol.android.util.BaseFragment
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwornDeclarationFragment : BaseFragment<FragmentSwornStatementBinding,SwornDeclarationViewModel>() {

    private val parentViewModel: NewCheckListViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Declaracion Jurada"
    }
    override fun getViewModelClass() = SwornDeclarationViewModel::class.java
    override fun getViewBinding() =  FragmentSwornStatementBinding.inflate (layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAcceptStatement.setOnClickListener {
            if(SharedUtils.isOnline(requireContext())){
                if(parentViewModel.uiState.isSignature == 1)
                    findNavController().navigate(R.id.action_swornDeclarationFragment_to_inputDataFragment)
                else
                    findNavController().navigate(R.id.action_swornDeclarationFragment_to_signatureFragment)
            }
            else{
                if(App.db.checkListBambaDao().getSignature()==null)
                    findNavController().navigate(R.id.action_swornDeclarationFragment_to_signatureFragment)
                else
                    findNavController().navigate(R.id.action_swornDeclarationFragment_to_inputDataFragment)
            }

        }
        NewCheckListScope.scope = ScopesChecklist.SWORN_DECLARATION
    }
}