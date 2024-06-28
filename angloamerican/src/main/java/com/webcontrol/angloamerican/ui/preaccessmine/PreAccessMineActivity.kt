package com.webcontrol.angloamerican.ui.preaccessmine

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.ActivityPreAccessMineBinding
import com.webcontrol.angloamerican.ui.preaccessmine.models.WorkerInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreAccessMineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreAccessMineBinding
    private val parentViewModel: PreAccessMineViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreAccessMineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = resources.getString(R.string.pam_title)
        setSupportActionBar(binding.toolbar)

        val idWorker = intent.getStringExtra("workerId")?:""
        val workerName = intent.getStringExtra("workerName")?:""
        parentViewModel.setWorkerId(WorkerInfo(idWorker, workerName))
    }

    fun isLoading(state: Boolean, textLoading: String = "Cargando..."){
        if(state){
            binding.pbTitle.text = textLoading
            binding.clLoading.visibility = View.VISIBLE
        } else {
            binding.clLoading.visibility = View.GONE
        }
    }

    fun isVisibleToolBar(state: Boolean){
        binding.mainToolbar.visibility =  if(state) View.VISIBLE else View.GONE
    }
}