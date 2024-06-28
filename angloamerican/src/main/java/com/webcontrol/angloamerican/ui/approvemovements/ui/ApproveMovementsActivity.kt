package com.webcontrol.angloamerican.ui.approvemovements.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.ActivityApproveMovementsBinding
import com.webcontrol.angloamerican.databinding.ActivityCheckListPreUsoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApproveMovementsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApproveMovementsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApproveMovementsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val workerId = intent.getStringExtra("workerId") ?: ""
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ApproveMovementsFragment.newInstance())
                .commitNow()
        }
    }

    fun showLoading(isShowing: Boolean, text: String = getString(R.string.loading)){
        if(isShowing){
            binding.clProgressBar.visibility = View.VISIBLE
            binding.tvProgressBar.text = text
        } else {
            binding.clProgressBar.visibility = View.GONE
        }
    }
}