package com.webcontrol.android.ui.lectorqr

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.webcontrol.android.R

class ResultQr private constructor(
        val fragment: Fragment
) {
    data class Builder(
            var fragment: Fragment = Fragment()
    ) {
        fun fragment(value: String?, vista: String?) = apply {
            if (vista!!.compareTo("DEFAULT") == 0) {
                this.fragment = ResultQrFragment.newInstance(value)
            }
            if (vista.compareTo("KINROSS") == 0) {
                this.fragment = ResultQrKinrossFragment.newInstance(value)
            }
        }
        fun build() = ResultQr(this.fragment)
    }

}