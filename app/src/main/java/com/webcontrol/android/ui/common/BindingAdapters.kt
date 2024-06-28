package com.webcontrol.android.ui.common

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("app:settingItemIcon")
fun setSettingItemIcon(view: ImageView, icon: Int) {
    view.setImageDrawable(
        ContextCompat.getDrawable(
            view.context,
            icon
        )
    )
}

private fun getColor(context: Context, color: Int): Int {
    return ContextCompat.getColor(context, color)
}