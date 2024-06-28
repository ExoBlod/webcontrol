package com.webcontrol.android.ui.newchecklist

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.webcontrol.android.R
import com.webcontrol.android.data.network.ApiResponseSearchBambas
import com.webcontrol.android.databinding.ActivityNewCheckListBinding
import com.webcontrol.android.ui.newchecklist.data.NewCheckListScope
import com.webcontrol.android.ui.newchecklist.data.ScopesChecklist
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_FILTER_INSPECTOR
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_NAME_INSPECTOR
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_SEARCH_INSPECTOR
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_SIGNATURE
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_TO_HISTORY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCheckListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewCheckListBinding

    private val parentViewModel: NewCheckListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCheckListBinding.inflate(layoutInflater)
        val signature = intent.getIntExtra(BUNDLE_SIGNATURE,-1)
        val nameInspector = intent.getStringExtra(BUNDLE_NAME_INSPECTOR)?:"Nada"
        val searchBambas = intent.getBooleanExtra(BUNDLE_SEARCH_INSPECTOR,false)
        val toHistory = intent.getBooleanExtra(BUNDLE_TO_HISTORY,false)
        val filter = intent.getBooleanExtra(BUNDLE_FILTER_INSPECTOR,false)
        parentViewModel.setResponseSearch(ApiResponseSearchBambas(0,0,"",nameInspector,nameInspector,signature,searchBambas,toHistory,filter))
        setContentView(binding.root)
        title = "Check List Bambas"
        //registerStep()
    }

    private fun registerStep() {
        with(supportFragmentManager.findFragmentById(R.id.nav_host_fragment_new_checklist) as NavHostFragment) {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.historyFragment,
                    R.id.swornDeclarationFragment-> {
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        with(supportFragmentManager.findFragmentById(R.id.nav_host_fragment_new_checklist) as NavHostFragment) {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.historyFragment -> {
                        if(!parentViewModel.uiState.isSearching && NewCheckListScope.scope == ScopesChecklist.HISTORY) finish()
                    }
                    R.id.consultInspectionsFragment -> finish()
                }
            }
        }
    }
}