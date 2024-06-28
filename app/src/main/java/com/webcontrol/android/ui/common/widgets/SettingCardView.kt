package com.webcontrol.android.ui.common.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.webcontrol.android.R
import kotlinx.android.synthetic.main.setting_card_view.view.*


class SettingCardView(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {

    init {
        if (attrs != null) {
            val typedArray =
                    context.obtainStyledAttributes(attrs, R.styleable.SettingCardView, 0, 0)
            try {
                val title = typedArray.getString(R.styleable.SettingCardView_cardText)
                val subtitle = typedArray.getString(R.styleable.SettingCardView_cardSubtext)
                val icon = typedArray.getResourceId(
                        R.styleable.SettingCardView_cardIcon,
                        R.drawable.ic_outline_settings_24
                )
                LayoutInflater.from(context).inflate(R.layout.setting_card_view, this)
                cardTitle.text = title
                cardSubtitle.text = subtitle
                cardIcon.setImageResource(icon)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setTitle(title: String) {
        cardTitle.text = title
    }

    fun setTitle(resId: Int) {
        val title = context.getString(resId)
        cardTitle.text = title
    }

    fun setSubtitle(subtitle: String) {
        cardSubtitle.text = subtitle
    }

    fun setSubtitle(resId: Int) {
        val subtitle = context.getString(resId)
        cardSubtitle.text = subtitle
    }

    fun setIcon(drawable: Drawable) {
        cardIcon.setImageDrawable(drawable)
    }

    fun setIcon(drawable: Int) {
        val img = ContextCompat.getDrawable(context, drawable)
        cardIcon.setImageDrawable(img)
    }
}