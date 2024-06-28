package com.webcontrol.angloamerican

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("app:courseApprovedImage")
fun courseApprovedImage(view: ImageView, status: String? = "") {
    when (status) {
        "SI" -> {
            ImageViewCompat.setImageTintList(
                view,
                ColorStateList.valueOf(getColor(view.context, R.color.green))
            )

            view.setImageDrawable(
                ContextCompat.getDrawable(
                    view.context,
                    R.drawable.ic_outline_check_circle_24
                )
            )

        }

        "NO" -> {
            ImageViewCompat.setImageTintList(
                view,
                ColorStateList.valueOf(getColor(view.context, R.color.red))
            )

            view.setImageDrawable(
                ContextCompat.getDrawable(
                    view.context,
                    R.drawable.ic_outline_close_circle_24
                )
            )
        }

        else -> {
        }
    }
}

@BindingAdapter("attended")
fun attended(view: TextView, attended: String? = "") {
    val attendedText = if (attended == "SI") "Asistio" else "No asistio"
    view.text = attendedText
}


private fun getColor(context: Context, color: Int): Int {
    return ContextCompat.getColor(context, color)
}


@BindingAdapter("app:reserveStatus")
fun reserveBusState(view: ImageView, status: String? = "") {
    when (status) {
        "NO" -> {
            ImageViewCompat.setImageTintList(
                view,
                ColorStateList.valueOf(getColor(view.context, R.color.red))
            )

            view.setImageDrawable(
                ContextCompat.getDrawable(
                    view.context,
                    R.drawable.ic_outline_close_circle_24
                )
            )

        }
        "SI" -> {
            ImageViewCompat.setImageTintList(
                view,
                ColorStateList.valueOf(getColor(view.context, R.color.green))
            )

            view.setImageDrawable(
                ContextCompat.getDrawable(
                    view.context,
                    R.drawable.ic_outline_check_circle_24
                )
            )
        }
        else -> {
        }
    }
}
