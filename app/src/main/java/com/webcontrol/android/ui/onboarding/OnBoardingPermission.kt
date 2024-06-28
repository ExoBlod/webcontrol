package com.webcontrol.android.ui.onboarding

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.cuneytayyildiz.onboarder.OnboarderActivity
import com.cuneytayyildiz.onboarder.model.*
import com.cuneytayyildiz.onboarder.utils.OnboarderPageChangeListener
import com.cuneytayyildiz.onboarder.utils.color
import com.webcontrol.android.R

class OnBoardingPermission : OnboarderActivity(), OnboarderPageChangeListener {

    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pages: MutableList<OnboarderPage> = createOnBoarderPages()
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                finish()
            }
        setOnboarderPageChangeListener(this)

        SkipButton().text = getString(R.string.no_thanks)
        FinishButton().text = getString(R.string.buttonActivate)
        NextButton().visibility = View.GONE
        FinishButton().visibility = View.VISIBLE

        initOnboardingPages(pages)
    }

    private fun createOnBoarderPages(): MutableList<OnboarderPage> = mutableListOf(
        onboarderPage {
            backgroundColor = color(R.color.colorPrimary)

            image { imageResId = R.drawable.map_location }

            title {
                text = getString(R.string.use_location)
                textColor = color(R.color.slidesText)
            }

            description {
                text = getString(R.string.background_location_permission_rationale)
                textColor = color(R.color.slidesText)
                multilineCentered = true
            }
        }
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onFinishButtonPressed() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }

    override fun onSkipButtonPressed() {
        finish()
    }

    override fun onPageChanged(position: Int) {}
}