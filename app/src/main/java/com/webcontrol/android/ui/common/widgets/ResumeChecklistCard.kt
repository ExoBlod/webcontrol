package com.webcontrol.android.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.webcontrol.android.R
import kotlinx.android.synthetic.main.resume_checklist_card.view.*
import kotlinx.android.synthetic.main.resume_checklist_card.view.lblDivision
import kotlinx.android.synthetic.main.resume_checklist_card.view.lblFecha

class ResumeChecklistCard(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {
    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ResumeChecklistCard, 0, 0)
            try {
                val title = typedArray.getString(R.styleable.ResumeChecklistCard_cardText)
                val subtitle = typedArray.getString(R.styleable.ResumeChecklistCard_cardSubtitle)
                val textDate = typedArray.getString(R.styleable.ResumeChecklistCard_cardDate)
                val icon = typedArray.getResourceId(
                        R.styleable.ResumeChecklistCard_cardIcon,
                        R.drawable.ic_checklist
                )
                val iconState = typedArray.getResourceId(
                        R.styleable.ResumeChecklistCard_cardIconState,
                        R.drawable.ic_cancel_red_24dp
                )
                LayoutInflater.from(context).inflate(R.layout.resume_checklist_card, this)
                lblNameChecklist.text = title
                lblDivision.text = subtitle
                lblFecha.text = textDate
                iconCard.setImageResource(icon)
                icState.setImageResource(iconState)
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setTextDate(textButton: String) {
        lblFecha.text = textButton
    }

    fun setSubtitle(subtitle: String) {
        lblDivision.text = subtitle
    }

    fun setIconState(drawable: Int) {
        val img = ContextCompat.getDrawable(context, drawable)
        icState.setImageDrawable(img)
    }

    fun setIconStateVisibility(visibility : Int)
    {
        icState.visibility = visibility
    }
}
