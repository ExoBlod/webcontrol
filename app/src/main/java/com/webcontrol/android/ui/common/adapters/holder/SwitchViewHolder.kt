package com.webcontrol.android.ui.common.adapters.holder

import android.view.View
import androidx.annotation.LayoutRes
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Antecedentes
import com.webcontrol.android.ui.common.adapters.AntecedentesAdapterListener
import kotlinx.android.synthetic.main.row_question_switch.view.*

class SwitchViewHolder(val view: View, val listener: AntecedentesAdapterListener) : BetterViewHolder<Antecedentes>(view) {
    companion object {
        @LayoutRes
        const val LAYOUT: Int = R.layout.row_question_switch
    }

    override fun bind(item: Antecedentes) {
        val testDescripcion: String = item.descripcion!!
        view.swOption.text = testDescripcion ?: "-"
        view.swOption.setOnCheckedChangeListener(null)
        view.swOption.isChecked = item.isChecked!!
        view.swOption.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                view.swOption.isChecked = true
                listener.onClickButtonSI(item)
            } else {
                view.swOption.isChecked = false
                listener.onClickButtonNO(item)
            }
        }
    }
}