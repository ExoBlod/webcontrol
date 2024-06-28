package com.webcontrol.android.ui.common.adapters.holder

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.webcontrol.android.R
import com.webcontrol.android.data.OnCheckListener
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle
import com.webcontrol.android.data.model.Antecedentes
import com.webcontrol.android.ui.checklist.CheckListEditTextListener
import com.webcontrol.android.ui.common.adapters.AntecedentesAdapterListener
import com.webcontrol.android.ui.common.adapters.CheckListAdapterListener
import kotlinx.android.synthetic.main.row_question_date.view.*
import kotlinx.android.synthetic.main.row_question_txt.view.*
import kotlinx.android.synthetic.main.row_question_txt.view.lblTestTitle
import kotlinx.android.synthetic.main.row_question_txt.view.lbl_answer
import kotlinx.android.synthetic.main.row_question_txt_switch.view.*

class TxtViewHolder(val view: View, val checkListEditTextListener: CheckListEditTextListener, private val buttonsDisabled: Boolean) : BetterViewHolder<CheckListTest_Detalle>(view) {

    companion object {
        const val LAYOUT: Int = R.layout.row_question_txt
    }

    private val testAnswer: EditText = view.lbl_answer

    init {
        testAnswer.addTextChangedListener(checkListEditTextListener)
    }

    override fun bind(item: CheckListTest_Detalle) {
        val testDescripcion: String = item.descripcion!!
        view.lblTestTitle.text = testDescripcion
        checkListEditTextListener.item = item
        view.lbl_answer.setText(item.respuestaSeleccionada)
        if(buttonsDisabled)
            view.lbl_answer.isEnabled = false
    }
}