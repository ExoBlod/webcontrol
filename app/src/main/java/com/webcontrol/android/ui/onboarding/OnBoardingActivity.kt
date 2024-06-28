package com.webcontrol.android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import com.cuneytayyildiz.onboarder.OnboarderActivity
import com.cuneytayyildiz.onboarder.model.*
import com.cuneytayyildiz.onboarder.utils.OnboarderPageChangeListener
import com.cuneytayyildiz.onboarder.utils.color
import com.webcontrol.android.R
import com.webcontrol.android.util.SharedUtils.setFirstRun
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : OnboarderActivity(), OnboarderPageChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pages: MutableList<OnboarderPage> = createOnBoarderPages()
        setOnboarderPageChangeListener(this)
        SkipButton().text = "Omitir"
        FinishButton().text = "Listo"
        initOnboardingPages(pages)
    }

    private fun createOnBoarderPages(): MutableList<OnboarderPage> {
        return mutableListOf(
            onboarderPage {
                backgroundColor = color(R.color.colorPrimary)

                image { imageResId = R.drawable.connect }

                title {
                    text = getString(R.string.timely_communication)
                    textColor = color(R.color.slidesText)
                }

                description {
                    text = getString(R.string.stay_connected_device)
                    textColor = color(R.color.slidesText)
                    multilineCentered = true
                }
            },
            onboarderPage {
                backgroundColor = color(R.color.colorPrimary)

                image { imageResId = R.drawable.city }

                title {
                    text = getString(R.string.access_wherever_you_are)
                    textColor = color(R.color.slidesText)
                }

                description {
                    text = getString(R.string.receive_notifications)
                    textColor = color(R.color.slidesText)
                    multilineCentered = true
                }
            },
            onboarderPage {
                backgroundColor = color(R.color.colorPrimary)

                image { imageResId = R.drawable.rocket }

                title {
                    text = getString(R.string.easy_and_uncomplicated)
                    textColor = color(R.color.slidesText)
                }

                description {
                    text = getString(R.string.receive_and_manage_messages)
                    textColor = color(R.color.slidesText)
                    multilineCentered = true
                }
            }
        )
    }

    override fun onFinishButtonPressed() {
        startActivity(Intent(this, LauncherActivity::class.java))
        finish()
        setFirstRun(this, false)
    }

    override fun onSkipButtonPressed() {
        startActivity(Intent(this, LauncherActivity::class.java))
        finish()
        setFirstRun(this, false)
    }

    override fun onPageChanged(position: Int) {}
}