package com.webcontrol.android.ui.settings

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.webcontrol.android.R
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_permission_disclosure.*

@AndroidEntryPoint
class PermissionDisclosureActivity: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_disclosure)
        setupUI()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupUI() {
        btnGrant.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (!granted) {
                    SharedUtils.showToast(this, "Background location not granted")
                }
                finish()
            }.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        btnCancel.setOnClickListener {
            finish()
        }
    }
}