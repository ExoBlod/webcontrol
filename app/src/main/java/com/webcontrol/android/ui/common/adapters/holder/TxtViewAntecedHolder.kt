package com.webcontrol.android.ui.common.adapters.holder

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Antecedentes
import com.webcontrol.android.ui.common.adapters.AntecedentesAdapterListener
import kotlinx.android.synthetic.main.row_question_txt_switch.view.*

class TxtViewAntecedHolder(val view: View, val listener: AntecedentesAdapterListener) : BetterViewHolder<Antecedentes>(view) {

    companion object {
        const val LAYOUT: Int = R.layout.row_question_txt_switch
    }

    override fun bind(item: Antecedentes) {
        val testDescripcion: String = item.descripcion!!
        view.lbl_answer.setOnClickListener(null)
        view.lblTestTitle.text = testDescripcion ?: "-"
        view.lblTestTitle.textSize = 14.0F
        view.lblTestTitle.setOnCheckedChangeListener(null)
        view.lblTestTitle.isChecked = item.isChecked!!
        if (item.isChecked!!) {
            item.comentario?.let {
                view.lbl_answer.setText(item.comentario)
            }
            view.tilAnswer.visibility = View.VISIBLE
        } else {
            view.tilAnswer.visibility = View.GONE
        }
        view.lblTestTitle.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                view.tilAnswer.visibility = View.VISIBLE
                item.isChecked = true
            } else {
                item.isChecked = false
                item.comentario = ""
                listener.onTextChanged(item)
                view.tilAnswer.visibility = View.GONE
            }
        }
        view.lbl_answer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("changed", "aqui")
                item.comentario = p0.toString()
                listener.onTextChanged(item)
            }
        })
    }
}