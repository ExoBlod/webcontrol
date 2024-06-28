package com.webcontrol.android.ui.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.webcontrol.android.BuildConfig
import com.webcontrol.android.R
import com.webcontrol.android.databinding.ActivityChangePermissionBinding
import com.webcontrol.android.ui.onboarding.OnBoardingPermission
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.hasPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CambiarPermisosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePermissionBinding
    lateinit var toolbar: Toolbar
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                checkPermission()
            }

        setUI()
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun setUI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            binding.containerBackgroundLocation.visibility = View.GONE
        }

        binding.swCoarseLocationPerm.setOnCheckedChangeListener { _, isChecked ->
            checkPermission()

            if (isChecked) {
                if (!this.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            } else {
                redirectToSettings()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.swBackgroundLocationPerm.setOnCheckedChangeListener { _, isChecked ->
                checkPermission()
                if (isChecked) {
                    if (!this.hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            startActivity(Intent(this, OnBoardingPermission::class.java))
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    }
                } else {
                    redirectToSettings()
                }
            }
        }
    }

    private fun setToolbar() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
    }

    private fun checkPermission() {
        binding.swCoarseLocationPerm.isChecked =
            this.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.swBackgroundLocationPerm.isChecked =
                this.hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun redirectToSettings() {
        SharedUtils.showDialog(
            this,
            getString(R.string.title_dialog_alert),
            getString(R.string.txt_dialog_redirect_config_screen),
            getString(R.string.buttonOk),
            { dialog, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID,
                    null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                dialog.dismiss()
                startActivity(intent)
            },
            getString(R.string.buttonCancel),
            { dialog, _ ->
                dialog.dismiss()
            }
        )
    }
}