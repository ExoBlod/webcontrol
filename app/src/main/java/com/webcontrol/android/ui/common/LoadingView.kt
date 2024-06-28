package com.webcontrol.android.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.webcontrol.android.R

class LoadingView(context: Context) : Dialog(context, R.style.AppTheme_Transparent) {
    init {
        this.setContentView(R.layout.view_loading)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.setCancelable(false)
    }
}