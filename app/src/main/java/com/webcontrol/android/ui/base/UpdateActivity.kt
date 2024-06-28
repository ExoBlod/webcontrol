package com.webcontrol.android.ui.base

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.webcontrol.android.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class UpdateActivity: AppCompatActivity() {

    private lateinit var appUpdateManager: AppUpdateManager

    private val listener = InstallStateUpdatedListener {
        when (it.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                popupSnackbarForCompleteUpdate()
            }
            InstallStatus.DOWNLOADING -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        checkUpdateResult()
        checkForUpdates()
    }

    override fun onPause() {
        super.onPause()
        appUpdateManager.unregisterListener(listener)
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.registerListener(listener)
        checkDownloadedUpdate()
    }

    private fun checkUpdateResult() {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                RESULT_OK -> {}
                RESULT_CANCELED,
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED-> {
                    popupSnackbarForRetryUpdate()
                }
            }
        }
    }

    private fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener {
            when (it.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    if ((it.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE &&
                            it.isUpdateTypeAllowed(IMMEDIATE)) {
                        appUpdateManager.startUpdateFlowForResult(
                                it,
                                IMMEDIATE,
                                this,
                                UPDATE_REQUEST_CODE
                        )
                    } else {
                        if (it.isUpdateTypeAllowed(FLEXIBLE)) {
                            appUpdateManager.startUpdateFlowForResult(
                                    it,
                                    FLEXIBLE,
                                    this,
                                    UPDATE_REQUEST_CODE
                            )
                        }
                    }
                }
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    appUpdateManager.startUpdateFlowForResult(
                            it,
                            IMMEDIATE,
                            this,
                            UPDATE_REQUEST_CODE
                    )
                }
                UpdateAvailability.UPDATE_NOT_AVAILABLE -> {}
                UpdateAvailability.UNKNOWN -> {}
            }
        }
    }

    private fun checkDownloadedUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate()
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
                findViewById(R.id.main_nav_menu_recyclerview),
                getString(R.string.app_update_downloaded),
                Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(getString(R.string.app_update_restart)) { appUpdateManager.completeUpdate() }
            show()
        }
    }

    private fun popupSnackbarForRetryUpdate() {
        Snackbar.make(
                findViewById(R.id.main_nav_menu_recyclerview),
                getString(R.string.app_update_failed),
                Snackbar.LENGTH_LONG
        ).apply {
            setAction(getString(R.string.app_update_retry)) { checkForUpdates() }
            show()
        }
    }

    companion object {
        const val UPDATE_REQUEST_CODE = 700
        const val DAYS_FOR_FLEXIBLE_UPDATE = 2
    }
}